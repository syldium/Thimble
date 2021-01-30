package me.syldium.thimble.common.service;

import me.syldium.thimble.api.Ranking;
import me.syldium.thimble.api.player.ThimblePlayerStats;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.config.MainConfig;
import me.syldium.thimble.common.util.DriverShim;
import me.syldium.thimble.common.dependency.DependencyResolver;
import me.syldium.thimble.common.player.PlayerStats;
import me.syldium.thimble.api.util.Leaderboard;
import me.syldium.thimble.common.util.ResourceReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SqlDataService implements DataService {

    private static final String TABLES = "sql/tables-%s.sql";

    private Connection connection;
    private PreparedStatement batch;
    private final String batchQuery;

    private final String url;
    private final String username;
    private final String password;
    private final Type type;
    private final Logger logger;

    public SqlDataService(
            @NotNull String url,
            @Nullable String username,
            @Nullable String password,
            @NotNull DependencyResolver dependencyResolver,
            @NotNull Logger logger,
            @NotNull Type type
    ) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.logger = logger;

        if (!type.isDriverAvailable()) {
            Path path = dependencyResolver.downloadDependency(type.getDriver());
            try {
                URLClassLoader classLoader = new URLClassLoader(new URL[]{path.toUri().toURL()});
                Driver driver = (Driver) Class.forName(type.getDriverClassName(), true, classLoader).getConstructor().newInstance();
                DriverManager.registerDriver(new DriverShim(driver));
            } catch (MalformedURLException | ReflectiveOperationException | SQLException ex) {
                this.logger.log(Level.SEVERE, "Unable to dynamically register the driver for the database.", ex);
            }
        }

        this.type = type;
        String queryPath = String.format(TABLES, type.hasUniqueIdType() ? "postgre" : "mysql");
        try (Statement statement = this.getConnection().createStatement()) {
            statement.execute(ResourceReader.readResource(queryPath));
        } catch (SQLException ex) {
            this.logger.log(Level.SEVERE, "Error during database setup. See the message below.", ex);
        }

        if (this.type == Type.MYSQL || this.type == Type.MARIADB) {
            // language=MySQL
            this.batchQuery = "INSERT INTO thimble_players (uuid, name, wins, losses, jumps, fails, thimbles) VALUES (?, ?, ?, ?, ?, ?, ?)"
                    + "ON DUPLICATE KEY UPDATE name = VALUES(name), wins = VALUES(wins), losses = VALUES(losses), jumps = VALUES(jumps), fails = VALUES(fails), thimbles = VALUES(thimbles)";
        } else if (this.type == Type.H2) {
            // language=H2
            this.batchQuery = "MERGE INTO thimble_players KEY (uuid) VALUES (?, ?, ?, ?, ?, ?, ?)";
        } else {
            // language=PostgreSQL
            this.batchQuery = "INSERT INTO thimble_players (uuid, name, wins, losses, jumps, fails, thimbles) VALUES (?, ?, ?, ?, ?, ?, ?)"
                    + "ON CONFLICT (uuid) DO UPDATE SET name = excluded.name, wins = excluded.wins, losses = excluded.losses, jumps = excluded.jumps, fails = excluded.fails, thimbles = excluded.thimbles";
        }
    }

    public SqlDataService(@NotNull File file, @NotNull DependencyResolver dependencyResolver, @NotNull Logger logger, @NotNull Type type) {
        this(String.format("jdbc:%s:%s", type.name().toLowerCase(Locale.ROOT), file.getAbsolutePath()), null, null, dependencyResolver, logger, type);
    }

    @TestOnly
    public SqlDataService() {
        this("jdbc:sqlite::memory:", null, null, new DependencyResolver(Paths.get(""), Logger.getLogger("Dependency")), Logger.getLogger("SqlDataService"), Type.SQLITE);
    }

    public static @NotNull SqlDataService fromConfig(@NotNull ThimblePlugin plugin, @NotNull MainConfig config) {
        Type type = config.getDataStorageMethod();
        DependencyResolver manager = new DependencyResolver(plugin);
        if (type == Type.H2 || type == Type.SQLITE) {
            return new SqlDataService(plugin.getFile(config.getDatabaseFilename(type == Type.H2)), manager, plugin.getLogger(), type);
        }
        return new SqlDataService(config.getJdbcUrl(), config.getJdbcUsername(), config.getJdbcPassword(), manager, plugin.getLogger(), type);
    }

    protected void startBatch() throws SQLException {
        this.batch = this.getConnection().prepareStatement(this.batchQuery);
    }

    protected void persist(@NotNull ThimblePlayerStats statistics) throws SQLException {
        if (this.type.hasUniqueIdType()) {
            this.batch.setObject(1, statistics.uuid());
        } else {
            this.batch.setString(1, statistics.uuid().toString());
        }
        this.batch.setString(2, statistics.name());
        this.batch.setInt(3, statistics.wins());
        this.batch.setInt(4, statistics.losses());
        this.batch.setInt(5, statistics.jumps());
        this.batch.setInt(6, statistics.failedJumps());
        this.batch.setInt(7, statistics.thimbles());
        this.batch.addBatch();
    }

    protected void finishBatch() throws SQLException {
        if (this.batch != null) {
            this.batch.executeBatch();
        }
        this.batch = null;
    }

    @Override
    public @NotNull Optional<@NotNull ThimblePlayerStats> getPlayerStatistics(@NotNull UUID uuid) {
        try (PreparedStatement statement = this.getConnection().prepareStatement("SELECT * FROM thimble_players WHERE uuid = ?")) {
            if (this.type.hasUniqueIdType()) {
                statement.setObject(1, uuid);
            } else {
                statement.setString(1, uuid.toString());
            }
            return this.optionalFromResultSet(statement.executeQuery());
        } catch (SQLException ex) {
            this.logger.log(Level.SEVERE, "Error when fetching statistics from the database.", ex);
            return Optional.empty();
        }
    }

    @Override
    public @NotNull Optional<@NotNull ThimblePlayerStats> getPlayerStatistics(@NotNull String name) {
        try (PreparedStatement statement = this.getConnection().prepareStatement("SELECT * FROM thimble_players WHERE name = ?")) {
            statement.setString(1, name);
            return this.optionalFromResultSet(statement.executeQuery());
        } catch (SQLException ex) {
            this.logger.log(Level.SEVERE, "Error when fetching statistics from the database.", ex);
            return Optional.empty();
        }
    }

    @Override
    public @NotNull Leaderboard getLeaderboard(@NotNull Ranking ranking) {
        try (PreparedStatement statement = this.getConnection().prepareStatement("SELECT * FROM thimble_players ORDER BY ? DESC LIMIT 10")) {
            statement.setString(1, ranking.name().toLowerCase(Locale.ROOT));
            ResultSet result = statement.executeQuery();

            Leaderboard leaderboard = Leaderboard.of(ranking);
            while (result.next()) {
                leaderboard.add(this.fromResultSet(result));
            }
            return leaderboard;
        } catch (SQLException ex) {
            this.logger.log(Level.SEVERE, "Error when fetching leaderboard from the database.", ex);
            return Leaderboard.of(ranking);
        }
    }

    @Override
    public void savePlayerStatistics(@NotNull ThimblePlayerStats statistics) {
        boolean exists = this.exists(statistics);
        String query = exists ?
                "UPDATE thimble_players SET name = ?, wins = ?, losses = ?, jumps = ?, fails = ?, thimbles = ? WHERE uuid = ?"
                : "INSERT INTO thimble_players (uuid, name, wins, losses, jumps, fails, thimbles) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = this.getConnection().prepareStatement(query)) {
            if (this.type.hasUniqueIdType()) {
                statement.setObject(exists ? 7 : 1, statistics.uuid());
            } else {
                statement.setString(exists ? 7 : 1, statistics.uuid().toString());
            }
            int i = exists ? 1 : 2;
            statement.setString(i++, statistics.name());
            statement.setInt(i++, statistics.wins());
            statement.setInt(i++, statistics.losses());
            statement.setInt(i++, statistics.jumps());
            statement.setInt(i++, statistics.failedJumps());
            statement.setInt(i, statistics.thimbles());
            statement.execute();
        } catch (SQLException ex) {
            this.logger.log(Level.SEVERE, "Error when saving statistics in the database.", ex);
        }
    }

    private boolean exists(@NotNull ThimblePlayerStats statistics) {
        try (PreparedStatement statement = this.getConnection().prepareStatement("SELECT name FROM thimble_players WHERE uuid = ?")) {
            if (this.type.hasUniqueIdType()) {
                statement.setObject(1, statistics.uuid());
            } else {
                statement.setString(1, statistics.uuid().toString());
            }
            return statement.executeQuery().next();
        } catch (SQLException ex) {
            return false;
        }
    }

    private @NotNull Optional<@NotNull ThimblePlayerStats> optionalFromResultSet(@NotNull ResultSet result) throws SQLException {
        if (result.next()) {
            return Optional.of(this.fromResultSet(result));
        }
        return Optional.empty();
    }

    private @NotNull ThimblePlayerStats fromResultSet(@NotNull ResultSet result) throws SQLException {
        UUID uuid;
        if (this.type.hasUniqueIdType()) {
            uuid = result.getObject("uuid", UUID.class);
        } else {
            uuid = UUID.fromString(result.getString("uuid"));
        }

        return new PlayerStats(
                uuid,
                result.getString("name"),
                result.getInt("wins"),
                result.getInt("losses"),
                result.getInt("jumps"),
                result.getInt("fails"),
                result.getInt("thimbles")
        );
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
                this.finishBatch();
                this.connection.close();
            } catch (SQLException ex) {
                this.logger.log(Level.SEVERE, "Error when closing the database connection.", ex);
            }
        }
    }
}
