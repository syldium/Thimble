package me.syldium.thimble.common.dependency;

import me.syldium.thimble.common.ThimblePlugin;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A .jar downloader for Maven repositories.
 *
 * <p><a href="https://github.com/Byteflux/libby">Based on Libby</a></p>
 */
public class DependencyResolver {

    private static final List<String> REPOSITORIES = Collections.singletonList("https://repo1.maven.org/maven2/");

    private final Path saveDirectory;
    private final Logger logger;

    public DependencyResolver(@NotNull ThimblePlugin plugin) {
        this(plugin.getDataFolder().toPath().toAbsolutePath(), plugin.getLogger());
    }

    public DependencyResolver(@NotNull Path path, @NotNull Logger logger) {
        this.saveDirectory = path.resolve("libs");
        this.saveDirectory.toFile().mkdirs();
        this.logger = logger;
    }

    /**
     * Gets all the possible download URLs for this library. Entries are
     * ordered by direct download URLs first and then repository download URLs.
     *
     * @param dependency The dependency to resolve.
     * @return Download URLs.
     */
    private @NotNull List<@NotNull String> resolveDependency(@NotNull Dependency dependency) {
        List<String> urls = new LinkedList<>();
        for (String repository : REPOSITORIES) {
            urls.add(repository + dependency.getPath());
        }
        return urls;
    }

    /**
     * Downloads a dependency jar and returns the contents as a byte array.
     *
     * @param url The URL to the dependency jar.
     * @return Downloaded jar as byte array or null if nothing was downloaded.
     */
    private byte[] downloadDependency(@NotNull String url) {
        this.logger.info("Download from " + url);
        try {
            URLConnection connection = new URL(url).openConnection();

            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("User-Agent", "Thimble plugin (driver resolver)");

            try (InputStream in = connection.getInputStream()) {
                int len;
                byte[] buf = new byte[8192];
                ByteArrayOutputStream out = new ByteArrayOutputStream();

                try {
                    while ((len = in.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }
                } catch (SocketTimeoutException e) {
                    this.logger.warning("Download timed out: " + connection.getURL());
                    return null;
                }

                this.logger.info("Download complete");
                return out.toByteArray();
            }
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        } catch (IOException ex) {
            if (ex instanceof FileNotFoundException) {
                this.logger.warning("File not found: " + url);
            } else if (ex instanceof SocketTimeoutException) {
                this.logger.warning("Connect timed out: " + url);
            } else if (ex instanceof UnknownHostException) {
                this.logger.warning("Unknown host: " + url);
            } else {
                this.logger.log(Level.SEVERE, "Unexpected IOException", ex);
            }

            return null;
        }
    }

    /**
     * Downloads a dependency jar to the save directory if it doesn't already
     * exist and returns the local file path.
     *
     * @param dependency The dependency to download.
     * @return Local file path to dependency.
     */
    public Path downloadDependency(Dependency dependency) {
        Path file = this.saveDirectory.resolve(dependency.getPath());
        if (Files.exists(file)) {
            return file;
        }

        this.logger.info("The " + dependency.name() + " dependency is not available. Downloading it...");
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        Path out = file.resolveSibling(file.getFileName() + ".tmp");
        out.toFile().deleteOnExit();

        try {
            Files.createDirectories(file.getParent());

            for (String url : this.resolveDependency(dependency)) {
                byte[] bytes = this.downloadDependency(url);
                if (bytes == null) {
                    continue;
                }

                byte[] checksum = md.digest(bytes);
                if (!Arrays.equals(checksum, dependency.getChecksum())) {
                    this.logger.log(Level.WARNING, "*** INVALID CHECKSUM ***");
                    this.logger.log(Level.WARNING, " Dependency :  " + dependency);
                    this.logger.log(Level.WARNING, " URL :  " + url);
                    this.logger.log(Level.WARNING, " Expected :  " + Base64.getEncoder().encodeToString(dependency.getChecksum()));
                    this.logger.log(Level.WARNING, " Actual :  " + Base64.getEncoder().encodeToString(checksum));
                    continue;
                }

                Files.write(out, bytes);
                Files.move(out, file);

                return file;
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            try {
                Files.deleteIfExists(out);
            } catch (IOException ignored) { }
        }

        throw new RuntimeException("Failed to download library '" + dependency + "'");
    }
}
