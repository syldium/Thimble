package me.syldium.thimble.common.game;

import me.syldium.thimble.api.arena.ThimbleSingleGame;
import me.syldium.thimble.api.arena.ThimbleState;
import me.syldium.thimble.api.player.JumpVerdict;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.player.InGamePlayer;
import me.syldium.thimble.common.player.MessageKey;
import me.syldium.thimble.common.player.Placeholder;
import me.syldium.thimble.common.player.Player;
import me.syldium.thimble.common.world.PoolBlock;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.util.Ticks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;

public class SingleGame extends Game implements ThimbleSingleGame {

    private final Queue<UUID> queue = new ArrayDeque<>();
    private UUID jumper;

    protected final int jumpTicks;

    public SingleGame(@NotNull ThimblePlugin plugin, @NotNull Arena arena) {
        super(plugin, arena);
        this.jumpTicks = plugin.getMainConfig().getGameInt("jump-time-single", 15) * Ticks.TICKS_PER_SECOND;
    }

    @Override
    public void onCountdownEnd() {
        List<InGamePlayer> playersList = new ArrayList<>(this.players.values());
        Collections.shuffle(playersList);
        for (InGamePlayer player : playersList) {
            if (!player.isSpectator() && !player.isVanished()) {
                this.queue.add(player.uuid());
            }
            if (this.queue.size() != 1) {
                Player p = this.plugin.getPlayer(player.uuid());
                if (p != null) {
                    p.teleport(this.arena.waitLocation());
                }
            }
        }
    }

    @Override
    public void onTimerEnd() {

    }

    @Override
    public void tickGame() {
        if (this.jumper == null) {
            this.timer = this.jumpTicks;
            if (this.queue.isEmpty()) {
                this.state = ThimbleState.END;
                return;
            }

            this.jumper = this.queue.poll();
            Player player = this.plugin.getPlayer(this.jumper);
            if (player == null) {
                this.onJump(null, this.players.get(this.jumper), JumpVerdict.MISSED);
            } else {
                player.teleport(this.arena.jumpLocation());
            }

            this.plugin.getScoreboardService().updateScoreboard(this.players, Placeholder.JUMPER, Placeholder.NEXT_JUMPER);
            return;
        }

        Player jumper = this.plugin.getPlayer(this.jumper);
        if (jumper == null) {
            this.onJump(null, this.players.get(this.jumper), JumpVerdict.MISSED);
            return;
        }

        this.jumperMedia.progress(jumper, this.timer, this.jumpTicks);
        if (jumper.isInWater()) {
            PoolBlock block = jumper.getFirstLiquidBlock();
            this.blocks.put(block.getPosition(), block.getBlockData());
            block.setBlockData(this.players.get(jumper).getChosenBlock());
            JumpVerdict verdict = this.plugin.getPlayerAdapter().isDeCoudre(block) ? JumpVerdict.THIMBLE : JumpVerdict.LANDED;
            this.onJump(jumper, this.players.get(jumper), verdict);

            if (this.remainingWaterBlocks.remove(block.getPosition()) && this.remainingWaterBlocks.isEmpty()) {
                this.players.sendMessage(MessageKey.CHAT_ARENA_FULL);
                this.end(this.getFirstPlayer());
            }
        }

        if (this.timer < 1) {
            this.onJump(jumper, this.players.get(jumper), JumpVerdict.MISSED);
        }
    }

    @Override
    public void onJump(@Nullable Player player, @NotNull InGamePlayer inGamePlayer, @NotNull JumpVerdict verdict0) {
        JumpVerdict verdict = this.plugin.getEventAdapter().callJumpVerdictEvent(inGamePlayer, verdict0);
        if (verdict == JumpVerdict.MISSED) {
            inGamePlayer.decrementPoints();
            inGamePlayer.incrementFailedJumps();
        } else {
            inGamePlayer.incrementJumps();
            this.playersWhoJumped.add(inGamePlayer.uuid());
            if (verdict == JumpVerdict.THIMBLE) {
                inGamePlayer.incrementPoints();
                inGamePlayer.incrementThimbles();
                this.players.sendMessage(inGamePlayer, MessageKey.CHAT_THIMBLE, Template.of("player", inGamePlayer.name()));
                this.plugin.getScoreboardService().updateScoreboard(this.players, Placeholder.THIMBLE);
            }
        }

        if (player != null) {
            this.jumperMedia.hide(player);
            this.sendJumpMessage(player, inGamePlayer, verdict);
            if (this.spectatorMode && inGamePlayer.isSpectator()) {
                player.spectate();
            } else {
                player.teleport(this.arena.waitLocation());
            }
            this.plugin.getScoreboardService().updateScoreboard(inGamePlayer, Placeholder.JUMPS, Placeholder.POINTS);
        }

        this.jumper = null;
        if (inGamePlayer.isSpectator()) {
            this.players.sendMessage(inGamePlayer, MessageKey.CHAT_ELIMINATED, Template.of("player", inGamePlayer.name()));
            if (this.queue.size() == 1) {
                this.end(this.players.get(this.queue.poll()));
            } else if (this.queue.isEmpty()) {
                this.end(inGamePlayer);
            }
        } else {
            this.queue.offer(inGamePlayer.uuid());
        }
    }

    @Override
    public @Nullable UUID currentJumper() {
        return this.jumper;
    }

    @Override
    public @Nullable UUID peekNextJumper() {
        return this.queue.peek();
    }

    @Override
    public @NotNull Queue<@NotNull UUID> jumperQueue() {
        return this.queue;
    }

    @Override
    public boolean isJumping(@NotNull UUID playerUUID) {
        return this.jumper != null && this.jumper.equals(playerUUID);
    }

    @Override
    public boolean removePlayer(@NotNull UUID player, boolean teleport) {
        boolean removed = super.removePlayer(player, teleport);
        this.queue.remove(player);
        if (removed && Objects.equals(this.jumper, player)) {
            this.jumper = null;
            Player p = this.plugin.getPlayer(player);
            if (p != null) {
                this.jumperMedia.hide(p);
            }
        }
        return removed;
    }

    @Override
    public String toString() {
        return "SingleGame{" +
                "arena=" + this.arena +
                ", state=" + this.state +
                ", players=" + this.players.values() +
                ", remainingWaterBlocks=" + this.remainingWaterBlocks.size() +
                ", timer=" + this.timer +
                ", queue=" + this.queue +
                ", jumper=" + this.jumper +
                '}';
    }
}
