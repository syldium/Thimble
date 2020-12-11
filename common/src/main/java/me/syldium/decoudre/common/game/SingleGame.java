package me.syldium.decoudre.common.game;

import me.syldium.decoudre.api.arena.DeSingleGame;
import me.syldium.decoudre.api.arena.DeState;
import me.syldium.decoudre.api.player.JumpVerdict;
import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.player.InGamePlayer;
import me.syldium.decoudre.common.player.Player;
import me.syldium.decoudre.common.world.PoolBlock;
import net.kyori.adventure.util.Ticks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;

public class SingleGame extends Game implements DeSingleGame {

    private final Queue<UUID> queue = new ArrayDeque<>();
    private UUID jumper;

    protected final int jumpTicks;

    public SingleGame(@NotNull DeCoudrePlugin plugin, @NotNull Arena arena) {
        super(plugin, arena);
        this.jumpTicks = plugin.getMainConfig().getJumpTimeSingleMode() * Ticks.TICKS_PER_SECOND;
    }

    @Override
    protected void onCountdownEnd() {
        for (InGamePlayer player : this.players) {
            if (!player.isSpectator()) {
                this.queue.add(player.uuid());
            }
        }
    }

    @Override
    protected void onTimerEnd() {

    }

    @Override
    protected void tickGame() {
        if (this.jumper == null) {
            this.timer = this.plugin.getMainConfig().getJumpTimeSingleMode() * Ticks.TICKS_PER_SECOND;
            if (this.queue.isEmpty()) {
                this.state = DeState.END;
                return;
            }
            Player player = this.plugin.getPlayer(this.queue.poll());
            if (player != null) {
                player.teleport(this.arena.getJumpLocation());
                this.jumper = player.uuid();
            }
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
            block.setBlockData(this.players.get(jumper).getChosenBlock());
            this.blocks.add(block);
            JumpVerdict verdict = this.plugin.getPlayerAdapter().isDeCoudre(block) ? JumpVerdict.COMBO : JumpVerdict.LANDED;
            this.onJump(jumper, this.players.get(jumper), verdict);

            if (this.remainingWaterBlocks.remove(block.getPosition()) && this.remainingWaterBlocks.isEmpty()) {
                this.end(this.getFirstPlayer());
            }
        }

        if (this.timer < 1) {
            this.onJump(jumper, this.players.get(jumper), JumpVerdict.MISSED);
        }
    }

    @Override
    protected void onJump(@Nullable Player player, @NotNull InGamePlayer inGamePlayer, @NotNull JumpVerdict verdict) {
        if (verdict == JumpVerdict.MISSED) {
            inGamePlayer.decrementLifes();
        } else {
            inGamePlayer.incrementJumps();
            if (verdict == JumpVerdict.COMBO) {
                inGamePlayer.incrementLifes();
                inGamePlayer.incrementDacs();
            }
        }

        if (player != null) {
            this.jumperMedia.hide(player);
            this.sendJumpMessage(player, inGamePlayer, verdict);
        }

        this.jumper = null;
        if (inGamePlayer.getPoints() > 0) {
            this.queue.offer(inGamePlayer.uuid());
        } else if (this.queue.size() < 2) {
            this.end(inGamePlayer);
        }
    }

    @Override
    public @Nullable UUID getCurrentJumper() {
        return this.jumper;
    }

    @Override
    public @Nullable UUID peekNextJumper() {
        return this.queue.peek();
    }

    @Override
    public boolean isJumping(@NotNull UUID playerUUID) {
        return this.jumper != null && this.jumper.equals(playerUUID);
    }

    @Override
    public boolean removePlayer(@NotNull UUID player) {
        boolean removed = super.removePlayer(player);
        if (removed && Objects.equals(this.jumper, player)) {
            this.jumper = null;
            Player p = this.plugin.getPlayer(player);
            if (p != null) {
                this.jumperMedia.hide(p);
            }
        }
        return removed;
    }
}
