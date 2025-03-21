package me.syldium.thimble.common.dependency;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.nio.file.Paths;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public enum Dependency {

    GSON(
            "com.google.code.gson",
            "gson",
            "2.8.9",
            "05mSkYVd5JXJTHQ3YbirUXbP6r4oGlqw2OjUUyb9cD4="
    ),
    H2_ENGINE(
            "com.h2database",
            "h2",
            "2.3.232",
            "ja5i0i24mCw9yzgm7bnHJ8XTAgY6Z+731j2C3kAfB9M="
    ),
    MARIADB_DRIVER(
            "org.mariadb.jdbc",
            "mariadb-java-client",
            "3.5.1",
            "UKUMSjwTww371AWH962aSWGX0oXt4JSGQdnu5o/fIQY="
    ),
    MYSQL_DRIVER(
            "com.mysql",
            "mysql-connector-j",
            "9.1.0",
            "h3bi68RgcsmkfqWdmCmMQnO9nxansmtd+kdEU1qibGI="
    ),
    POSTGRESQL_DRIVER(
            "org.postgresql",
            "postgresql",
            "42.7.4",
            "GIl2ch6tjoYn622DidUA3MwMm+vYhSaKMEcYAnSmAx4="
    ),
    SQLITE_DRIVER(
            "org.xerial",
            "sqlite-jdbc",
            "3.47.2.0",
            "0B35LWLFBb6qPK0z5B+iiHbswRHdH428RL9yNM3Lp+M="
    );

    private final byte[] checksum;
    private final String path;

    Dependency(@NotNull String groupId, @NotNull String artifactId, @NotNull String version, @NotNull String checksum) {
        this.checksum = Base64.getDecoder().decode(checksum);
        this.path = groupId.replace('.', '/') + '/' + artifactId + '/' + version + '/' + artifactId + '-' + version + ".jar";
    }

    public byte[] getChecksum() {
        return this.checksum;
    }

    public @NotNull String getPath() {
        return this.path;
    }

    @TestOnly
    public static void main(String[] args) {
        DependencyResolver dependencyResolver = new DependencyResolver(Paths.get(""), Logger.getGlobal());

        ExecutorService pool = Executors.newCachedThreadPool();
        for (Dependency dependency : values()) {
            pool.submit(() -> dependencyResolver.downloadDependency(dependency));
        }

        pool.shutdown();
        try {
            pool.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
