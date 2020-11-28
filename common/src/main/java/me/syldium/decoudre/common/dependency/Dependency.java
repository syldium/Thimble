package me.syldium.decoudre.common.dependency;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.nio.file.Paths;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public enum Dependency {

    MYSQL_DRIVER(
            "mysql",
            "mysql-connector-java",
            "8.0.22",
            "UBne+9EjFilel6bojyqbB/EYNFpOmCcQu6Iy5JmyL08="
    ),
    POSTGRESQL_DRIVER(
            "org.postgresql",
            "postgresql",
            "42.2.18",
            "DIkZefHrL+REMtoRTQl2C1Bj2tnmaawKxrC2v7kbs7o="
    ),
    SQLITE_DRIVER(
            "org.xerial",
            "sqlite-jdbc",
            "3.32.3.2",
            "7CL07opEtcliARxo7Ocu5TeojrvnytcNP4fIFq6lM1c="
    );

    private final byte[] checksum;
    private final String path;

    Dependency(@NotNull String groupId, @NotNull String artifactId, @NotNull String version, @NotNull String checksum) {
        this.checksum = Base64.getDecoder().decode(checksum);
        this.path = groupId.replace('.', '/') + '/' + artifactId + '/' + version + '/' + artifactId + '-' + version + ".jar";
    }

    public @NotNull byte[] getChecksum() {
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
