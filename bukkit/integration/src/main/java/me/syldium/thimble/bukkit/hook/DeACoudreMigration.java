package me.syldium.thimble.bukkit.hook;

import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.util.BlockVector;
import me.syldium.thimble.api.util.WorldKey;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.command.abstraction.ChildCommand;
import me.syldium.thimble.common.command.abstraction.CommandException;
import me.syldium.thimble.common.command.abstraction.Permission;
import me.syldium.thimble.common.command.abstraction.Sender;
import me.syldium.thimble.common.config.ConfigNode;
import me.syldium.thimble.common.game.Arena;
import me.syldium.thimble.common.player.MessageKey;
import me.syldium.thimble.common.player.PlayerStats;
import me.syldium.thimble.common.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * <p><a href="https://www.spigotmc.org/resources/d%C3%A9-%C3%A0-coudre.59231/">Dé à Coudre</a></p>
 *
 * <p>Recreates arenas and statistics from the configuration files.</p>
 */
class DeACoudreMigration extends ChildCommand {

    private final File pluginFolder;
    private final @Nullable String version;
    private final Function<@NotNull String, @Nullable UUID> getPlayerUniqueId;

    DeACoudreMigration(@Nullable Plugin plugin, @NotNull File pluginFolder) {
        super("deacoudre", null, Permission.migrate());
        this.pluginFolder = pluginFolder;
        this.version = plugin == null ? null : plugin.getDescription().getVersion();
        this.getPlayerUniqueId = this.resolveGetPlayerUniqueId();
    }

    @Override
    public @NotNull CommandResult execute(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @NotNull List<String> args) throws CommandException {
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(new File(this.pluginFolder, "config.yml"));
        ConfigNode gameNode = plugin.getMainConfig().getGameNode();
        gameNode.setValue("countdown-time", configuration.getInt("times.wait", 30));
        gameNode.setValue("jump-time-single", configuration.getInt("times.jump", 15));
        // TODO: Save and lose comments?

        ConfigurationSection dac = configuration.getConfigurationSection("dac");
        if (dac != null) {
            this.migrateArenas(sender, plugin, dac);
        }

        configuration = YamlConfiguration.loadConfiguration(new File(this.pluginFolder, "statistics.yml"));
        this.migrateStats(plugin, configuration);

        return CommandResult.success(MessageKey.FEEDBACK_MIGRATION, ComponentUtil.getPluginDescriptionTemplate("DeACoudre", this.version));
    }

    private void migrateArenas(@NotNull Sender sender, @NotNull ThimblePlugin plugin, @NotNull ConfigurationSection configuration) {
        for (Map.Entry<String, Object> entry : configuration.getValues(false).entrySet()) {
            if (!(entry.getValue() instanceof ConfigurationSection)) {
                continue;
            }
            ConfigurationSection section = (ConfigurationSection) entry.getValue();

            String name = requireNonNull(section.getString("name"), "arena name");
            Arena arena = plugin.getGameService().createArena(name);
            if (arena == null) {
                sender.sendMessage(
                        Component.text()
                                .color(NamedTextColor.YELLOW)
                                .content("Skipping the migration of the ")
                                .append(Component.text(name, NamedTextColor.GOLD))
                                .append(Component.text(" arena since it already exists."))
                );
                continue;
            }

            arena.setMinPlayers(section.getInt("min", 2));
            arena.setMaxPlayers(section.getInt("max", 8));
            this.setLoc(section.getConfigurationSection("diving"), arena::setJumpLocation);
            this.setLoc(section.getConfigurationSection("lobby"), arena::setSpawnLocation);
            this.setLoc(section.getConfigurationSection("pool"), arena::setWaitLocation);
            BlockVector a = this.getBlockVector(section.getConfigurationSection("region.a"));
            BlockVector b = this.getBlockVector(section.getConfigurationSection("region.b"));
            if (a != null && b != null) {
                arena.setPoolMinPoint(a.min(b));
                arena.setPoolMaxPoint(a.max(b));
            }
        }
    }

    private void migrateStats(@NotNull ThimblePlugin plugin, @NotNull FileConfiguration configuration) {
        for (Map.Entry<String, Object> entry : configuration.getValues(false).entrySet()) {
            String playerName = entry.getKey();
            if (!(entry.getValue() instanceof ConfigurationSection)) {
                continue;
            }
            ConfigurationSection section = (ConfigurationSection) entry.getValue();

            UUID uuid = this.getPlayerUniqueId.apply(playerName);
            if (uuid == null) {
                return;
            }

            PlayerStats stats = new PlayerStats(
                    uuid,
                    playerName,
                    section.getInt("wins"),
                    section.getInt("loses"),
                    section.getInt("jumps"),
                    section.getInt("fails"),
                    section.getInt("perfects")
            );
            plugin.getStatsService().savePlayerStatistics(stats);
        }
    }

    private void setLoc(@Nullable ConfigurationSection section, @NotNull Consumer<Location> consumer) {
        if (section == null) return;
        consumer.accept(new Location(
                new WorldKey(requireNonNull(section.getString("world"), "world")),
                section.getDouble("x"),
                section.getDouble("y"),
                section.getDouble("z"),
                (float) section.getDouble("pitch"),
                (float) section.getDouble("yaw")
        ));
    }

    private BlockVector getBlockVector(@Nullable ConfigurationSection section) {
        if (section == null) return null;
        return new BlockVector(section.getInt("x"), section.getInt("y"), section.getInt("z"));
    }

    @SuppressWarnings("deprecation")
    private @NotNull Function<String, UUID> resolveGetPlayerUniqueId() {
        try {
            Bukkit.class.getMethod("getPlayerUniqueId", String.class);
            return Bukkit::getPlayerUniqueId;
        } catch (NoSuchMethodException ex) {
            return playerName -> Bukkit.getOfflinePlayer(playerName).getUniqueId();
        }
    }
}
