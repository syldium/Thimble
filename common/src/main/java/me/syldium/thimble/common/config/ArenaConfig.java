package me.syldium.thimble.common.config;

import me.syldium.thimble.common.game.Arena;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public interface ArenaConfig {

    @NotNull Collection<@NotNull Arena> load();

    void save(@NotNull Set<@NotNull Arena> arenas);
}
