package me.syldium.decoudre.common.service;

import me.syldium.decoudre.api.Ranking;
import me.syldium.decoudre.api.player.DePlayerStats;
import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.config.MainConfig;
import me.syldium.decoudre.common.util.DriverShim;
import me.syldium.decoudre.common.dependency.DependencyResolver;
import me.syldium.decoudre.common.player.PlayerStats;
import me.syldium.decoudre.api.util.Leaderboard;
import me.syldium.decoudre.common.util.ResourceReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
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
        try {
            String queryPath = String.format(TABLES, type == Type.POSTGRE ? "postgre" : "mysql");
            this.getConnection().createStatement().execute(ResourceReader.readResource(queryPath));
        } catch (SQLException ex) {
            this.logger.log(Level.SEVERE, "Error during database setup. See the message below.", ex);
        }
    }

    public SqlDataService(@NotNull File file, @NotNull DependencyResolver dependencyResolver, @NotNull Logger logger) {
        this(String.format("jdbc:sqlite:%s", file.getAbsolutePath()), null, null, dependencyResolver, logger, Type.SQLITE);
    }

    public static @NotNull SqlDataService fromConfig(@NotNull DeCoudrePlugin plugin, @NotNull MainConfig config) {
        Type type = config.getDataStorageMethod();
        DependencyResolver manager = new DependencyResolver(plugin);
        if (type == Type.SQLITE) {
            return new SqlDataService(plugin.getFile("database.db"), manager, plugin.getLogger());
        }
        return new SqlDataService(config.getJdbcUrl(), config.getJdbcUsername(), config.getJdbcPassword(), manager, plugin.getLogger(), type);
    }

    @Override
    public @NotNull Optional<@NotNull DePlayerStats> getPlayerStatistics(@NotNull UUID uuid) {
        try (PreparedStatement statement = this.getConnection().prepareStatement("SELECT * FROM dac WHERE uuid = ?")) {
            if (this.type == Type.POSTGRE) {
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
    public @NotNull Optional<@NotNull DePlayerStats> getPlayerStatistics(@NotNull String name) {
        try (PreparedStatement statement = this.getConnection().prepareStatement("SELECT * FROM dac WHERE name = ?")) {
            statement.setString(1, name);
            return this.optionalFromResultSet(statement.executeQuery());
        } catch (SQLException ex) {
            this.logger.log(Level.SEVERE, "Error when fetching statistics from the database.", ex);
            return Optional.empty();
        }
    }

    @Override
    public @NotNull Leaderboard<@NotNull DePlayerStats> getLeaderboard(@NotNull Ranking ranking) {
        try (PreparedStatement statement = this.getConnection().prepareStatement("SELECT * FROM dac ORDER BY ? DESC LIMIT 10")) {
            statement.setString(1, ranking.name().toLowerCase(Locale.ROOT));
            ResultSet result = statement.executeQuery();

            Leaderboard<DePlayerStats> leaderboard = Leaderboard.of(ranking);
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
    public void savePlayerStatistics(@NotNull DePlayerStats statistics) {
        boolean exists = this.exists(statistics);
        String query = exists ?
                "UPDATE dac SET name = ?, wins = ?, losses = ?, jumps = ?, dacs = ? WHERE uuid = ?"
                : "INSERT INTO dac (uuid, name, wins, losses, jumps, dacs) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = this.getConnection().prepareStatement(query)) {
            if (this.type == Type.POSTGRE) {
                statement.setObject(exists ? 6 : 1, statistics.uuid());
            } else {
                statement.setString(exists ? 6 : 1, statistics.uuid().toString());
            }
            int i = exists ? 1 : 2;
            statement.setString(i++, statistics.name());
            statement.setInt(i++, statistics.getWins());
            statement.setInt(i++, statistics.getLosses());
            statement.setInt(i++, statistics.getJumps());
            statement.setInt(i, statistics.getDacs());
            statement.execute();
        } catch (SQLException ex) {
            this.logger.log(Level.SEVERE, "Error when saving statistics in the database.", ex);
        }
    }

    private boolean exists(@NotNull DePlayerStats statistics) {
        try (PreparedStatement statement = this.getConnection().prepareStatement("SELECT name FROM dac WHERE uuid = ?")) {
            if (this.type == Type.POSTGRE) {
                statement.setObject(1, statistics.uuid());
            } else {
                statement.setString(1, statistics.uuid().toString());
            }
            return statement.executeQuery().next();
        } catch (SQLException ex) {
            return false;
        }
    }

    private @NotNull Optional<@NotNull DePlayerStats> optionalFromResultSet(@NotNull ResultSet result) throws SQLException {
        if (result.next()) {
            return Optional.of(this.fromResultSet(result));
        }
        return Optional.empty();
    }

    private @NotNull DePlayerStats fromResultSet(@NotNull ResultSet result) throws SQLException {
        UUID uuid;
        if (this.type == Type.POSTGRE) {
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
                result.getInt("dacs")
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
                this.connection.close();
            } catch (SQLException ex) {
                this.logger.log(Level.SEVERE, "Error when closing the database connection.", ex);
            }
        }
    }
}
