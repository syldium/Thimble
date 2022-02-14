package me.syldium.thimble.common.game;

import me.syldium.thimble.api.arena.ThimbleConcurrentGame;
import me.syldium.thimble.api.arena.ThimbleState;
import me.syldium.thimble.api.player.JumpVerdict;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.player.InGamePlayer;
import me.syldium.thimble.common.player.MessageKey;
import me.syldium.thimble.common.player.ThimblePlaceholder;
import me.syldium.thimble.common.player.Player;
import me.syldium.thimble.common.util.Task;
import me.syldium.thimble.common.world.PoolBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.component;

public class ConcurrentGame extends Game implements ThimbleConcurrentGame {

    private final boolean countFails;
    private final int jumpTicks, thimblePoints;

    public ConcurrentGame(@NotNull ThimblePlugin plugin, @NotNull Arena arena) {
        super(plugin, arena);
        this.countFails = plugin.getMainConfig().getGameNode().getBool("count-fails-concurrent", true);
        this.jumpTicks = plugin.getMainConfig().getGameNode().getInt("jump-time-concurrent", 40) * Task.GAME_TICKS_PER_SECOND;
        this.thimblePoints = plugin.getMainConfig().getGameNode().getInt("thimble-points-concurrent", 1);
    }

    @Override
    public void onCountdownEnd() {
        this.timer = this.jumpTicks;
        for (InGamePlayer inGamePlayer : this.players) {
            if (inGamePlayer.isVanished()) continue;
            Player player = this.plugin.getPlayer(inGamePlayer.uuid());
            if (player == null) continue;
            player.teleport(this.arena.jumpLocation());
        }
    }

    @Override
    public void onTimerEnd() {
        this.end(this.getFirstPlayer());
    }

    @Override
    public void tickGame() {
        this.jumperMedia.progress(this.players, this.timer, this.jumpTicks);
        for (InGamePlayer inGamePlayer : this.players) {
            if (inGamePlayer.isSpectator()) continue;
            Player player = this.plugin.getPlayer(inGamePlayer.uuid());
            if (player == null) continue;

            if (player.isInWater()) {
                PoolBlock block = player.getFirstLiquidBlock();
                this.blocks.put(block.getPosition(), block.getBlockData());
                block.setBlockData(inGamePlayer.getChosenBlock());
                JumpVerdict verdict = this.plugin.getPlayerAdapter().isDeCoudre(block) ? JumpVerdict.THIMBLE : JumpVerdict.LANDED;
                this.onJump(player, inGamePlayer, verdict);

                if (this.remainingWaterBlocks.remove(block.getPosition()) && this.remainingWaterBlocks.isEmpty()) {
                    this.players.sendMessage(MessageKey.CHAT_ARENA_FULL);
                    this.end(this.getFirstPlayer());
                }
            }
        }
    }

    @Override
    public void onJump(@Nullable Player player, @NotNull InGamePlayer inGamePlayer, @NotNull JumpVerdict verdict0) {
        JumpVerdict verdict = this.plugin.getEventAdapter().callJumpVerdictEvent(inGamePlayer, verdict0);
        if (verdict == JumpVerdict.MISSED) {
            if (this.countFails) {
                inGamePlayer.decrementPoints();
            }
            inGamePlayer.incrementFailedJumps();
        } else {
            inGamePlayer.incrementJumps();
            inGamePlayer.incrementPoints();
            this.playersWhoJumped.add(inGamePlayer.uuid());
            if (verdict == JumpVerdict.THIMBLE) {
                inGamePlayer.incrementPoints(this.thimblePoints);
                inGamePlayer.incrementThimbles();
                this.players.sendMessage(inGamePlayer, MessageKey.CHAT_THIMBLE, component("player", inGamePlayer.displayName()));
                this.plugin.getScoreboardService().updateScoreboard(inGamePlayer, ThimblePlaceholder.THIMBLE);
            }
        }

        if (inGamePlayer.isSpectator()) {
            // Check for remaining players
            boolean latest = true;
            for (InGamePlayer p : this.players) {
                if (!p.isSpectator() && !p.isVanished()) {
                    latest = false;
                    break;
                }
            }
            if (latest) {
                this.end(this.getFirstPlayer());
            }
        }

        if (player != null) {
            this.sendJumpMessage(player, inGamePlayer, verdict);
            if (inGamePlayer.isSpectator()) {
                this.players.sendMessage(inGamePlayer, MessageKey.CHAT_ELIMINATED, component("player", inGamePlayer.displayName()));
                if (this.spectatorMode) {
                    player.spectate();
                } else {
                    player.teleport(this.arena.waitLocation());
                }
            } else {
                player.teleport(this.arena.jumpLocation());
            }
            this.plugin.getScoreboardService().updateScoreboard(inGamePlayer, ThimblePlaceholder.JUMPS, ThimblePlaceholder.POINTS);
        }
    }

    @Override
    protected void sendJumpMessage(@NotNull Player player, @NotNull InGamePlayer inGamePlayer, @NotNull JumpVerdict verdict) {
        if (verdict == JumpVerdict.MISSED) {
            player.sendActionBar(MessageKey.ACTIONBAR_MISSED);
            player.playSound(this.plugin.getMainConfig().getJumpFailedSound());
        } else if (verdict == JumpVerdict.THIMBLE) {
            player.sendActionBar(MessageKey.ACTIONBAR_THIMBLE);
            player.playSound(this.plugin.getMainConfig().getThimbleSound());
            this.plugin.spawnFireworks(player.getLocation().up(2)).spawn(this.fireworksThimble);
        } else {
            player.sendActionBar(MessageKey.ACTIONBAR_SUCCESSFUL_JUMP);
            player.playSound(this.plugin.getMainConfig().getJumpSucceedSound());
        }
    }

    @Override
    public boolean isJumping(@NotNull UUID playerUUID) {
        if (this.state != ThimbleState.PLAYING) {
            return false;
        }
        InGamePlayer player = this.players.get(playerUUID);
        return player != null && !player.isSpectator() && !player.isVanished();
    }

    @Override
    public boolean removePlayer(@NotNull UUID player, boolean teleport) {
        boolean removed = super.removePlayer(player, teleport);
        if (removed) {
            Player p = this.plugin.getPlayer(player);
            if (p != null) {
                this.jumperMedia.hide(p);
            }
        }
        return removed;
    }
}
