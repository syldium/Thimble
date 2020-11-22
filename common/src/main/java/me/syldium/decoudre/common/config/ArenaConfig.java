package me.syldium.decoudre.common.config;

import me.syldium.decoudre.common.game.Arena;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public interface ArenaConfig {

    @NotNull Collection<@NotNull Arena> load();

    void save(@NotNull Set<@NotNull Arena> arenas);
}
