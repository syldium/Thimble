package me.syldium.thimble.common.player;

import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.api.player.ThimblePlayerStats;
import me.syldium.thimble.common.game.Game;
import me.syldium.thimble.common.world.BlockData;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class InGamePlayer extends PlayerStats implements ThimblePlayer {

    private final Game game;
    private BlockData block;
    protected int lifes = 1;

    public InGamePlayer(@NotNull ThimblePlayerStats stats, @NotNull BlockData block, @NotNull Game game) {
        super(stats.uuid(), stats.name(), stats.getWins(), stats.getLosses(), stats.getJumps(), stats.getFailedJumps(), stats.getThimbles());
        this.block = block;
        this.game = game;
    }

    public InGamePlayer(@NotNull UUID uuid, @NotNull String name, @NotNull BlockData block, @NotNull Game game) {
        super(uuid, name);
        this.block = block;
        this.game = game;
    }

    @Override
    public int getPoints() {
        return this.lifes;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public @NotNull Game getGame() {
        return this.game;
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

    public void setChosenBlock(@NotNull BlockData block) {
        this.block = block;
    }
}
