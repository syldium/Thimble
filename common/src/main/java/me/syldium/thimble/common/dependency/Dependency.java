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
            "2.2.224",
            "udjxk1itqCpPbrWxdMbP4yCjdbWpy1pP5FbWI+blVJc="
    ),
    MARIADB_DRIVER(
            "org.mariadb.jdbc",
            "mariadb-java-client",
            "3.3.2",
            "KmfvPMHKSBlloOfy1BdNEm80ZNArQFWkQSYfrYwZZ2k="
    ),
    MYSQL_DRIVER(
            "com.mysql",
            "mysql-connector-j",
            "8.3.0",
            "lOf6gVNwzc7+2RXbf1P4hEX6wRD4w4GDkrmS7J7m0pU="
    ),
    POSTGRESQL_DRIVER(
            "org.postgresql",
            "postgresql",
            "42.7.1",
            "SbupwyANT2Suc5A9Vs4b0Jx0UX3+May0R0VQa0/O3lM="
    ),
    SQLITE_DRIVER(
            "org.xerial",
            "sqlite-jdbc",
            "3.45.1.0",
            "9fVAT6WmD54LFee+ouotE34lXwG6vQv8udr80uO/nNI="
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
