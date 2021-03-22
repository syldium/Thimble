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
            "2.8.6",
            "yPtIOQVNKAswM/gA0fWpfeLwKOuLoutFitKH5Tbz8l8="
    ),
    H2_ENGINE(
            "com.h2database",
            "h2",
            "1.4.200",
            "OtmsS2qunNnTrBxEdGXh7QYBm4UbiT3WqNdt222FvKY="
    ),
    MARIADB_DRIVER(
            "org.mariadb.jdbc",
            "mariadb-java-client",
            "2.7.2",
            "o/Z3bfCELPZefxWFFQEtUwfalJ9mBCKC4e5EdN0Z9Eg="
    ),
    MYSQL_DRIVER(
            "mysql",
            "mysql-connector-java",
            "8.0.23",
            "/31bQCr9OcEnh0cVBaM6MEEDsjjsG3pE6JNtMynadTU="
    ),
    POSTGRESQL_DRIVER(
            "org.postgresql",
            "postgresql",
            "42.2.19",
            "IydH+gkk2Iom36QrgSi2+hFAgC2AQSWJFZboyl8pEyI="
    ),
    SQLITE_DRIVER(
            "org.xerial",
            "sqlite-jdbc",
            "3.34.0",
            "YFl5yU5/4AQ38eENz6ZXoj8SXI600vDsF+P4RhOJTMM="
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
