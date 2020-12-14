package me.syldium.thimble.common.config;

import me.syldium.thimble.api.util.BlockPos;
import me.syldium.thimble.common.game.Arena;
import me.syldium.thimble.common.util.SignAction;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface ArenaConfig {

    @NotNull Collection<@NotNull Arena> load();

    @NotNull Map<@NotNull BlockPos, @NotNull SignAction> loadActionSigns();

    void save(@NotNull Set<@NotNull Arena> arenas);

    void save(@NotNull Map<SignAction, Set<BlockPos>> actionSigns);
}
