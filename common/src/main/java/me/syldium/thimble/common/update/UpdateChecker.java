package me.syldium.thimble.common.update;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.syldium.thimble.api.util.PluginVersion;
import me.syldium.thimble.common.util.ServerType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdateChecker implements Runnable {

    private static final long CACHE_TIME = 30 * 60 * 1000;
    private static final String RELEASES_URL = "https://api.github.com/repos/syldium/Thimble/releases/latest";
    private static final String USER_AGENT = "Thimble";

    private final Logger logger;
    private final PluginVersion currentVersion;
    private final ServerType serverType;

    private long lastCheck = 0;
    private @Nullable GitHubReleaseInfo releaseInfo;
    private boolean checkingVersion = false;
    private boolean hasVersion = false;
    private boolean upToDate = false;
    private final ReentrantLock versionLock = new ReentrantLock();

    public UpdateChecker(@NotNull PluginVersion currentVersion, @NotNull ServerType serverType, @NotNull Logger logger) {
        this.logger = logger;
        this.serverType = serverType;
        this.currentVersion = currentVersion;
    }

    @Override
    public void run() {
        boolean isOwner = this.versionLock.tryLock();
        this.checkingVersion = true;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(RELEASES_URL).openConnection();
            connection.setRequestProperty("User-Agent", USER_AGENT);

            JsonObject apiResponse = new JsonParser().parse(new InputStreamReader(connection.getInputStream())).getAsJsonObject();
            this.releaseInfo = new Gson().fromJson(apiResponse, GitHubReleaseInfo.class);

            for (GitHubAssetInfo assetInfo : this.releaseInfo.assets) {
                if (!assetInfo.name.contains(this.serverType.toString())) {
                    continue;
                }
                this.releaseInfo.platformAsset = assetInfo;

                if (this.currentVersion.compareTo(new PluginVersion(this.releaseInfo.tagName)) < 0) {
                    this.logger.warning(
                            "You are running an outdated version of Thimble! "
                            + "Please update to the latest version (" + this.releaseInfo.tagName + "): \n"
                            + "-> " + assetInfo.browserDownloadUrl
                    );
                } else {
                    this.upToDate = true;
                }
            }
        } catch (FileNotFoundException ex) {
            this.logger.log(Level.WARNING, "404 error: " + ex.getMessage());
        } catch (Exception ex) {
            this.logger.log(Level.WARNING, "Failed to get release info from api.github.com.", ex);
        } finally {
            this.checkingVersion = false;
            this.hasVersion = true;
            this.lastCheck = System.currentTimeMillis();
            if (isOwner) {
                this.versionLock.unlock();
            }
        }
    }

    public @NotNull CompletableFuture<@Nullable GitHubReleaseInfo> getReleaseInfo() {
        return CompletableFuture.supplyAsync(this::fetchReleaseInfo);
    }

    private @Nullable GitHubReleaseInfo fetchReleaseInfo() {
        if (this.hasVersion) {
            if (System.currentTimeMillis() - this.lastCheck > CACHE_TIME) {
                this.hasVersion = false;
            } else {
                return this.releaseInfo;
            }
        }

        this.versionLock.lock();
        try {
            if (!this.checkingVersion) {
                this.run();
            }
        } finally {
            this.versionLock.unlock();
        }
        return this.releaseInfo;
    }

    public boolean isUpToDate() {
        return this.upToDate;
    }
}
