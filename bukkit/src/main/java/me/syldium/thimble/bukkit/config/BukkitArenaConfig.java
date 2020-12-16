package me.syldium.thimble.bukkit.config;

import me.syldium.thimble.api.util.BlockPos;
import me.syldium.thimble.api.util.BlockVector;
import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.arena.ThimbleGameMode;
import me.syldium.thimble.bukkit.ThBukkitPlugin;
import me.syldium.thimble.common.config.ArenaConfig;
import me.syldium.thimble.common.game.Arena;
import me.syldium.thimble.common.util.SignAction;
import me.syldium.thimble.common.util.EnumUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BukkitArenaConfig extends FileConfig implements ArenaConfig {

    public BukkitArenaConfig(@NotNull ThBukkitPlugin plugin, @NotNull File file) {
        super(plugin, file);
    }

    @Override
    public @NotNull List<Arena> load() {
        List<Arena> arenas = new LinkedList<>();
        ConfigurationSection arenasSection = this.configuration.getConfigurationSection("arenas");
        if (arenasSection == null) {
            return arenas;
        }

        for (String name : arenasSection.getKeys(false)) {
            Arena arena = new Arena(this.plugin, name);
            arenas.add(arena);
            ConfigurationSection section = arenasSection.getConfigurationSection(name);
            ConfigurationSection jumpSection = section.getConfigurationSection("jump-location");
            if (jumpSection != null) arena.setJumpLocation(this.getLocation(jumpSection));
            ConfigurationSection spawnSection = section.getConfigurationSection("spawn-location");
            if (spawnSection != null) arena.setSpawnLocation(this.getLocation(spawnSection));
            ConfigurationSection waitSection = section.getConfigurationSection("wait-location");
            if (waitSection != null) arena.setWaitLocation(this.getLocation(waitSection));

            BlockVector minPoint = this.unserializeBlockVector(section.getString("min-point"));
            if (minPoint != null) arena.setPoolMinPoint(minPoint);
            BlockVector maxPoint = this.unserializeBlockVector(section.getString("max-point"));
            if (maxPoint != null) arena.setPoolMaxPoint(maxPoint);

            this.plugin.getGameService().addSigns(
                    section.getStringList("signs").stream()
                            .map(this::unserializeBlockPos)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet()),
                    arena
            );
            arena.setGameMode(EnumUtil.valueOf(ThimbleGameMode.class, section.getString("gamemode"), ThimbleGameMode.SINGLE));

            arena.setMinPlayers(section.getInt("min-players", 2));
            arena.setMaxPlayers(section.getInt("max-players", 8));
        }
        return arenas;
    }

    @Override
    public @NotNull Map<BlockPos, SignAction> loadActionSigns() {
        Map<BlockPos, SignAction> map = new HashMap<>();
        ConfigurationSection signsSection = this.configuration.getConfigurationSection("signs");
        if (signsSection == null) {
            return map;
        }

        for (String key : signsSection.getKeys(false)) {
            SignAction action = EnumUtil.valueOf(SignAction.class, key, null);
            if (action == null) continue;

            for (String line : signsSection.getStringList(key)) {
                BlockPos position = this.unserializeBlockPos(line);
                if (position != null) {
                    map.put(position, action);
                }
            }
        }
        return map;
    }

    @Override
    public void save(@NotNull Set<Arena> arenas) {
        for (Arena arena : arenas) {
            ConfigurationSection section = this.configuration.createSection("arenas." + arena.getName());
            this.setLocation(section.createSection("jump-location"), arena.getJumpLocation());
            this.setLocation(section.createSection("spawn-location"), arena.getSpawnLocation());
            this.setLocation(section.createSection("wait-location"), arena.getWaitLocation());
            section.set("min-players", arena.getMinPlayers());
            section.set("max-players", arena.getMaxPlayers());
            section.set("gamemode", arena.getGameMode().name());
            section.set("min-point", this.serializeBlockVector(arena.getPoolMinPoint()));
            section.set("max-point", this.serializeBlockVector(arena.getPoolMaxPoint()));
            section.set("signs", arena.getSigns().stream().map(this::serializeBlockPos).collect(Collectors.toList()));
        }
        this.save();
    }

    @Override
    public void save(@NotNull Map<SignAction, Set<BlockPos>> actionSigns) {
        for (Map.Entry<SignAction, Set<BlockPos>> entry : actionSigns.entrySet()) {
            this.configuration.set(
                    "signs." + entry.getKey().name(),
                    entry.getValue().stream()
                            .map(this::serializeBlockPos)
                            .collect(Collectors.toList())
            );
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

    private @Nullable BlockVector unserializeBlockVector(@Nullable String raw) {
        if (raw == null) return null;
        String[] split = raw.split(":");
        try {
            return new BlockVector(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
            this.plugin.getLogger().warning("Unable to unserialize " + raw + " (" + ex.getMessage() + ")");
            return null;
        }
    }

    private @Nullable BlockPos unserializeBlockPos(@Nullable String raw) {
        if (raw == null) return null;
        String[] split = raw.split(":");
        try {
            return new BlockPos(UUID.fromString(split[3]), Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException ex) {
            this.plugin.getLogger().warning("Unable to unserialize " + raw + " (" + ex.getMessage() + ")");
            return null;
        }
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

    private @Nullable String serializeBlockVector(@Nullable BlockVector vector) {
        if (vector == null) return null;
        return vector.getX() + ":" + vector.getY() + ":" + vector.getZ();
    }

    private @Nullable String serializeBlockPos(@Nullable BlockPos position) {
        if (position == null) return null;
        return position.getX() + ":" + position.getY() + ":" + position.getZ() + ":" + position.getWorldUUID();
    }

}
