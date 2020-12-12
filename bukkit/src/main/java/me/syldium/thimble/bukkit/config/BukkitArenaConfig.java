package me.syldium.thimble.bukkit.config;

import me.syldium.thimble.api.BlockVector;
import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.arena.ThimbleGameMode;
import me.syldium.thimble.bukkit.ThBukkitPlugin;
import me.syldium.thimble.common.config.ArenaConfig;
import me.syldium.thimble.common.game.Arena;
import me.syldium.thimble.common.util.EnumUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class BukkitArenaConfig extends FileConfig implements ArenaConfig {

    public BukkitArenaConfig(@NotNull ThBukkitPlugin plugin, @NotNull File file) {
        super(plugin, file);
    }

    @Override
    public @NotNull List<Arena> load() {
        List<Arena> arenas = new LinkedList<>();
        for (String name : this.configuration.getKeys(false)) {
            Arena arena = new Arena(this.plugin, name);
            arenas.add(arena);
            ConfigurationSection section = this.configuration.getConfigurationSection(name);
            ConfigurationSection jumpSection = section.getConfigurationSection("jump-location");
            if (jumpSection != null) arena.setJumpLocation(this.getLocation(jumpSection));
            ConfigurationSection spawnSection = section.getConfigurationSection("spawn-location");
            if (spawnSection != null) arena.setSpawnLocation(this.getLocation(spawnSection));
            ConfigurationSection minPointSection = section.getConfigurationSection("min-point");
            if (minPointSection != null) arena.setPoolMinPoint(this.getBlockVector(minPointSection));
            ConfigurationSection maxPointSection = section.getConfigurationSection("max-point");
            if (maxPointSection != null) arena.setPoolMaxPoint(this.getBlockVector(maxPointSection));

            arena.setGameMode(EnumUtil.valueOf(ThimbleGameMode.class, section.getString("gamemode"), ThimbleGameMode.SINGLE));

            arena.setMinPlayers(section.getInt("min-players", 2));
            arena.setMaxPlayers(section.getInt("max-players", 8));
        }
        return arenas;
    }

    @Override
    public void save(@NotNull Set<Arena> arenas) {
        for (Arena arena : arenas) {
            ConfigurationSection section = this.configuration.createSection(arena.getName());
            this.setLocation(section.createSection("jump-location"), arena.getJumpLocation());
            this.setLocation(section.createSection("spawn-location"), arena.getSpawnLocation());
            section.set("min-players", arena.getMinPlayers());
            section.set("max-players", arena.getMaxPlayers());
            section.set("gamemode", arena.getGameMode().name());
            this.setBlockVector(section.createSection("min-point"), arena.getPoolMinPoint());
            this.setBlockVector(section.createSection("max-point"), arena.getPoolMaxPoint());
        }
        this.save();
    }

    private @NotNull Location getLocation(@NotNull ConfigurationSection section) {
        return new Location(
                UUID.fromString(Objects.requireNonNull(section.getString("world"), "Serialized location doesn't have a world property.")),
                section.getDouble("x"),
                section.getDouble("y"),
                section.getDouble("z"),
                (float) section.getDouble("pitch"),
                (float) section.getDouble("yaw")
        );
    }

    private @NotNull BlockVector getBlockVector(@NotNull ConfigurationSection section) {
        return new BlockVector(section.getInt("x"), section.getInt("y"), section.getInt("z"));
    }

    private void setLocation(@NotNull ConfigurationSection section, @Nullable Location location) {
        if (location == null) return;
        section.set("world", location.getWorldUUID().toString());
        section.set("x", location.getX());
        section.set("y", location.getY());
        section.set("z", location.getZ());
        section.set("pitch", location.getPitch());
        section.set("yaw", location.getYaw());
    }

    private void setBlockVector(@NotNull ConfigurationSection section, @Nullable BlockVector vector) {
        if (vector == null) return;
        section.set("x", vector.getX());
        section.set("y", vector.getY());
        section.set("z", vector.getZ());
    }
}
