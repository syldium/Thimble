package me.syldium.thimble.common.service;

import me.syldium.thimble.api.Ranking;
import me.syldium.thimble.api.player.ThimblePlayerStats;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.config.MainConfig;
import me.syldium.thimble.api.util.Leaderboard;
import me.syldium.thimble.common.dependency.Dependency;
import org.jetbrains.annotations.NotNull;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public interface DataService {

    @NotNull Optional<@NotNull ThimblePlayerStats> getPlayerStatistics(@NotNull UUID uuid);

    @NotNull Optional<@NotNull ThimblePlayerStats> getPlayerStatistics(@NotNull String name);

    @NotNull Leaderboard<@NotNull ThimblePlayerStats> getLeaderboard(@NotNull Ranking ranking);

    void savePlayerStatistics(@NotNull ThimblePlayerStats statistics);

    void close();

    static @NotNull DataService fromConfig(@NotNull ThimblePlugin plugin, @NotNull MainConfig config) {
        return SqlDataService.fromConfig(plugin, config);
    }

    enum Type {
        SQLITE("sqlite", Dependency.SQLITE_DRIVER, "org.sqlite.JDBC"),
        MARIADB("mariadb", Dependency.MARIADB_DRIVER, "org.mariadb.jdbc.Driver"),
        MYSQL("mysql", Dependency.MYSQL_DRIVER, "com.mysql.jdbc.Driver"),
        POSTGRE("postgresql", Dependency.POSTGRESQL_DRIVER, "org.postgresql.Driver");

        private final String driverName;
        private final String driverClassName;
        private final Dependency driver;

        Type(@NotNull String driverName, @NotNull Dependency driver, @NotNull String driverClassName) {
            this.driverClassName = driverClassName;
            this.driverName = driverName;
            this.driver = driver;
        }

        public boolean isDriverAvailable() {
            try {
                DriverManager.getDriver("jdbc:" + this.driverName + "://host/db");
                return true;
            } catch (SQLException ex) {
                return false;
            }
        }

        public @NotNull String getDriverClassName() {
            return this.driverClassName;
        }

        public @NotNull Dependency getDriver() {
            return this.driver;
        }
    }
}
