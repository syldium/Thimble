package me.syldium.decoudre.common.service;

import me.syldium.decoudre.api.player.DePlayerStats;
import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.config.MainConfig;
import me.syldium.decoudre.common.player.PlayerStats;
import me.syldium.decoudre.common.util.ResourceReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SqlDataService implements DataService {

    private static final String TABLES = "sql/tables-%s.sql";

    private Connection connection;
    private final String url;
    private final String username;
    private final String password;
    private final Type type;
    private final Logger logger;

    public SqlDataService(@NotNull String url, @Nullable String username, @Nullable String password, @NotNull Logger logger, @NotNull Type type) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.logger = logger;

        this.type = type;
        try {
            String queryPath = String.format(TABLES, type == Type.POSTGRE ? "postgre" : "mysql");
            this.getConnection().createStatement().execute(ResourceReader.readResource(queryPath));
        } catch (SQLException ex) {
            this.logger.log(Level.SEVERE, "Error during database setup.", ex);
        }
    }

    public SqlDataService(@NotNull File file, @NotNull Logger logger) {
        this(String.format("jdbc:sqlite:%s", file.getAbsolutePath()), null, null, logger, Type.SQLITE);
    }

    public static @NotNull SqlDataService fromConfig(@NotNull DeCoudrePlugin plugin, @NotNull MainConfig config) {
        Type type = config.getDataStorageMethod();
        if (type == Type.SQLITE) {
            return new SqlDataService(plugin.getFile("database.db"), plugin.getLogger());
        }
        return new SqlDataService(config.getJdbcUrl(), config.getJdbcUsername(), config.getJdbcPassword(), plugin.getLogger(), type);
    }

    @Override
    public @NotNull Optional<@NotNull DePlayerStats> getPlayerStatistics(@NotNull UUID uuid) {
        try (PreparedStatement statement = this.getConnection().prepareStatement("SELECT * FROM dac WHERE uuid = ?")) {
            if (this.type == Type.POSTGRE) {
                statement.setObject(1, uuid);
            } else {
                statement.setString(1, uuid.toString());
            }
            return this.fromResultSet(statement.executeQuery());
        } catch (SQLException ex) {
            this.logger.log(Level.SEVERE, "Error when fetching statistics from the database.", ex);
            return Optional.empty();
        }
    }

    @Override
    public @NotNull Optional<@NotNull DePlayerStats> getPlayerStatistics(@NotNull String name) {
        try (PreparedStatement statement = this.getConnection().prepareStatement("SELECT * FROM dac WHERE name = ?")) {
            statement.setString(1, name);
            return this.fromResultSet(statement.executeQuery());
        } catch (SQLException ex) {
            this.logger.log(Level.SEVERE, "Error when fetching statistics from the database.", ex);
            return Optional.empty();
        }
    }

    @Override
    public void savePlayerStatistics(@NotNull DePlayerStats statistics) {
        try (PreparedStatement statement = this.getConnection().prepareStatement("REPLACE INTO dac (uuid, name, wins, losses, jumps, dacs) VALUES (?, ?, ?, ?, ?, ?)")) {
            if (this.type == Type.POSTGRE) {
                statement.setObject(1, statistics.uuid());
            } else {
                statement.setString(1, statistics.uuid().toString());
            }
            statement.setString(2, statistics.name());
            statement.setInt(3, statistics.getWins());
            statement.setInt(4, statistics.getLosses());
            statement.setInt(5, statistics.getJumps());
            statement.setInt(6, statistics.getDacs());
            statement.execute();
        } catch (SQLException ex) {
            this.logger.log(Level.SEVERE, "Error when saving statistics in the database.", ex);
        }
    }

    private @NotNull Optional<@NotNull DePlayerStats> fromResultSet(@NotNull ResultSet result) throws SQLException {
        if (result.next()) {
            UUID uuid;
            if (this.type == Type.POSTGRE) {
                uuid = result.getObject("uuid", UUID.class);
            } else {
                uuid = UUID.fromString(result.getString("uuid"));
            }

            return Optional.of(new PlayerStats(
                    uuid,
                    result.getString("name"),
                    result.getInt("wins"),
                    result.getInt("losses"),
                    result.getInt("jumps"),
                    result.getInt("dacs")
            ));
        }
        return Optional.empty();
    }

    private @NotNull Connection getConnection() throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            this.connection = DriverManager.getConnection(this.url, this.username, this.password);
            if (this.connection == null) {
                throw new SQLException("No database connection was established.");
            }
        }
        return this.connection;
    }

    @Override
    public void close() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException ex) {
                this.logger.log(Level.SEVERE, "Error when closing the database connection.", ex);
            }
        }
    }
}
