package me.syldium.decoudre.common.service;

import me.syldium.decoudre.api.Ranking;
import me.syldium.decoudre.api.player.DePlayerStats;
import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.config.MainConfig;
import me.syldium.decoudre.api.util.Leaderboard;
import me.syldium.decoudre.common.dependency.Dependency;
import org.jetbrains.annotations.NotNull;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public interface DataService {

    @NotNull Optional<@NotNull DePlayerStats> getPlayerStatistics(@NotNull UUID uuid);

    @NotNull Optional<@NotNull DePlayerStats> getPlayerStatistics(@NotNull String name);

    @NotNull Leaderboard<@NotNull DePlayerStats> getLeaderboard(@NotNull Ranking ranking);

    void savePlayerStatistics(@NotNull DePlayerStats statistics);

    void close();

    static @NotNull DataService fromConfig(@NotNull DeCoudrePlugin plugin, @NotNull MainConfig config) {
        return SqlDataService.fromConfig(plugin, config);
    }

    enum Type {
        SQLITE("sqlite", Dependency.SQLITE_DRIVER, "org.sqlite.JDBC"),
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
