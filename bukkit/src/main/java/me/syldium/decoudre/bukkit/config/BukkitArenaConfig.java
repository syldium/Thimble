package me.syldium.decoudre.bukkit.config;

import me.syldium.decoudre.api.Location;
import me.syldium.decoudre.common.config.ArenaConfig;
import me.syldium.decoudre.common.game.Arena;
import me.syldium.decoudre.bukkit.DeBukkitPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class BukkitArenaConfig extends FileConfig implements ArenaConfig {

    public BukkitArenaConfig(@NotNull DeBukkitPlugin plugin, @NotNull File file) {
        super(plugin, file);
    }

    @Override
    public @NotNull List<Arena> load() {
        List<Arena> arenas = new LinkedList<>();
        for (String name : this.configuration.getKeys(false)) {
            Arena arena = new Arena(this.plugin, name);
            arenas.add(arena);
            ConfigurationSection section = this.configuration.getConfigurationSection(name);
            this.set(section, "spawn-location", arena::setSpawnLocation);
            this.set(section, "jump-location", arena::setJumpLocation);
            arena.setMinPlayers(section.getInt("min-players", 2));
            arena.setMaxPlayers(section.getInt("max-players", 8));
        }
        return arenas;
    }

    @Override
    public void save(@NotNull Set<Arena> arenas) {
        for (Arena arena : arenas) {
            ConfigurationSection section = this.configuration.createSection(arena.getName());
            section.set("spawn-location", this.plugin.getPlayerAdapter().asPlatform(arena.getSpawnLocation()));
            section.set("jump-location", this.plugin.getPlayerAdapter().asPlatform(arena.getJumpLocation()));
            section.set("min-players", arena.getMinPlayers());
            section.set("max-players", arena.getMaxPlayers());
        }
        this.save();
    }

    private void set(@NotNull ConfigurationSection section, @NotNull String path, @NotNull Consumer<Location> setter) {
        org.bukkit.Location location = section.getLocation(path);
        if (location != null) {
            setter.accept(this.plugin.getPlayerAdapter().asAbstractLocation(location));
        }
    }
}
