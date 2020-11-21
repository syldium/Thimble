package me.syldium.decoudre.common.player;

import me.syldium.decoudre.api.player.DePlayer;
import me.syldium.decoudre.api.player.DePlayerStats;
import me.syldium.decoudre.common.world.BlockData;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class InGamePlayer extends PlayerStats implements DePlayer {

    private final BlockData block;
    protected int lifes = 1;

    public InGamePlayer(@NotNull DePlayerStats stats, @NotNull BlockData block) {
        super(stats.uuid(), stats.name(), stats.getWins(), stats.getLosses(), stats.getJumps(), stats.getDacs());
        this.block = block;
    }

    public InGamePlayer(@NotNull UUID uuid, @NotNull String name, @NotNull BlockData block) {
        super(uuid, name);
        this.block = block;
    }

    @Override
    public int getLifes() {
        return this.lifes;
    }

    public void incrementLifes() {
        this.lifes++;
    }

    public void decrementLifes() {
        this.lifes--;
    }

    public @NotNull BlockData getChosenBlock() {
        return this.block;
    }
}
