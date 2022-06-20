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
            "2.1.214",
            "1iPNwPYdIYz1SajQnxw5H/kQlhFrIuJHVHX85PvnK9A="
    ),
    MARIADB_DRIVER(
            "org.mariadb.jdbc",
            "mariadb-java-client",
            "3.0.5",
            "6VMtm8oHwSY8MrALSVvT7H8reTIFE8q9divznqCeaNk="
    ),
    MYSQL_DRIVER(
            "mysql",
            "mysql-connector-java",
            "8.0.29",
            "1OMtKmAmtazAAwC3OobCj7kmga6WKbIQSO5nAUyRHbY="
    ),
    POSTGRESQL_DRIVER(
            "org.postgresql",
            "postgresql",
            "42.4.0",
            "/iW5wKLFlFhQTsiIYoU99SLuh/igJWSDXVN8Ka5MsSU="
    ),
    SQLITE_DRIVER(
            "org.xerial",
            "sqlite-jdbc",
            "3.36.0.3",
            "rzozdjkeGGoP7WPs1BS3Kogr9FJme0kKC+Or+FtjfT8="
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
