package me.syldium.decoudre.common.game;

import me.syldium.decoudre.api.arena.DeConcurrentGame;
import me.syldium.decoudre.api.arena.DeState;
import me.syldium.decoudre.api.player.JumpVerdict;
import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.config.GameConfig;
import me.syldium.decoudre.common.player.InGamePlayer;
import me.syldium.decoudre.common.player.MessageKey;
import me.syldium.decoudre.common.player.Player;
import me.syldium.decoudre.common.world.PoolBlock;
import net.kyori.adventure.util.Ticks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ConcurrentGame extends Game implements DeConcurrentGame {

    private final boolean countFails;
    private final int jumpTicks;

    public ConcurrentGame(@NotNull DeCoudrePlugin plugin, @NotNull Arena arena) {
        super(plugin, arena);
        this.countFails = plugin.getMainConfig().doesCountFailsInConcurrent();
        this.jumpTicks = plugin.getMainConfig().getJumpTimeConcurrentMode() * Ticks.TICKS_PER_SECOND;
    }

    @Override
    protected void onCountdownEnd() {
        this.timer = this.jumpTicks;
        for (InGamePlayer inGamePlayer : this.players) {
            if (inGamePlayer.isSpectator()) continue;
            Player player = this.plugin.getPlayer(inGamePlayer.uuid());
            if (player == null) continue;
            player.teleport(this.arena.getJumpLocation());
        }
    }

    @Override
    protected void onTimerEnd() {
        this.end(this.getFirstPlayer());
    }

    @Override
    protected void tickGame() {
        this.jumperMedia.progress(this.players, this.timer, this.jumpTicks);
        for (InGamePlayer inGamePlayer : this.players) {
            if (inGamePlayer.isSpectator()) continue;
            Player player = this.plugin.getPlayer(inGamePlayer.uuid());
            if (player == null) continue;

            if (player.isInWater()) {
                PoolBlock block = player.getFirstLiquidBlock();
                block.setBlockData(inGamePlayer.getChosenBlock());
                this.blocks.add(block);
                this.onJump(player, inGamePlayer, JumpVerdict.LANDED);
            }
        }
    }

    @Override
    protected void onJump(@Nullable Player player, @NotNull InGamePlayer inGamePlayer, @NotNull JumpVerdict verdict) {
        if (verdict == JumpVerdict.MISSED) {
            if (this.countFails) {
                inGamePlayer.decrementLifes();
            }
        } else {
            inGamePlayer.incrementJumps();
            if (verdict == JumpVerdict.COMBO) {
                inGamePlayer.incrementLifes();
                inGamePlayer.incrementDacs();
            }
        }

        if (player != null) {
            this.sendJumpMessage(player, inGamePlayer, verdict);
            player.teleport(this.arena.getJumpLocation());
        }
    }

    @Override
    protected void sendJumpMessage(@NotNull Player player, @NotNull InGamePlayer inGamePlayer, @NotNull JumpVerdict verdict) {
        if (verdict == JumpVerdict.MISSED) {
            player.sendActionBar(MessageKey.ACTIONBAR_MISSED);
            player.playSound(GameConfig.getJumpFailedSound());
        } else if (verdict == JumpVerdict.COMBO) {
            player.sendActionBar(MessageKey.ACTIONBAR_COMBO);
            player.playSound(GameConfig.getJumpSucceedSound());
            this.plugin.spawnFireworks(player.getLocation());
        } else {
            player.sendActionBar(MessageKey.ACTIONBAR_SUCCESSFUL_JUMP);
        }
    }

    @Override
    public boolean isJumping(@NotNull UUID playerUUID) {
        if (this.state != DeState.PLAYING) {
            return false;
        }
        InGamePlayer player = this.players.get(playerUUID);
        return player != null && !player.isSpectator();
    }
}
