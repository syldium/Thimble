package me.syldium.decoudre.common.game;

import me.syldium.decoudre.api.BlockVector;
import me.syldium.decoudre.api.arena.DeGame;
import me.syldium.decoudre.api.arena.DeState;
import me.syldium.decoudre.api.player.DePlayer;
import me.syldium.decoudre.api.player.JumpVerdict;
import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.adapter.BlockBalancer;
import me.syldium.decoudre.common.config.GameConfig;
import me.syldium.decoudre.common.player.InGamePlayer;
import me.syldium.decoudre.common.player.MessageKey;
import me.syldium.decoudre.common.player.Player;
import me.syldium.decoudre.common.player.media.TimedMedia;
import me.syldium.decoudre.common.util.PlayerMap;
import me.syldium.decoudre.common.util.Task;
import me.syldium.decoudre.common.world.BlockData;
import me.syldium.decoudre.common.world.Blocks;
import me.syldium.decoudre.common.world.PoolBlock;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.util.Ticks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

/**
 * Common implementation for both game modes.
 *
 * @see ConcurrentGame
 * @see SingleGame
 */
public abstract class Game implements DeGame, Runnable {

    private static final int TIMER_SOUND_THRESHOLD = Ticks.TICKS_PER_SECOND * 5;

    protected final DeCoudrePlugin plugin;
    protected final Arena arena;
    protected final Task task;

    protected DeState state = DeState.WAITING;
    protected final PlayerMap<InGamePlayer> players;
    protected final TimedMedia jumperMedia;

    protected final Set<BlockVector> remainingWaterBlocks;
    protected final List<PoolBlock> blocks = new ArrayList<>();

    protected int timer;
    protected final int countdownTicks;

    public Game(@NotNull DeCoudrePlugin plugin, @NotNull Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
        this.players = new PlayerMap<>(plugin);
        this.timer = plugin.getMainConfig().getCountdownTime() * Ticks.TICKS_PER_SECOND;
        this.task = plugin.startGameTask(this);
        this.jumperMedia = TimedMedia.from(plugin.getMainConfig(), "jump");

        this.countdownTicks = this.plugin.getMainConfig().getCountdownTime() * Ticks.TICKS_PER_SECOND;

        this.remainingWaterBlocks = this.arena.getPoolMinPoint() == null || this.arena.getPoolMaxPoint() == null ?
                Collections.emptySet()
                : this.plugin.getPlayerAdapter().getRemainingWaterBlocks(
                        arena.getJumpLocation().getWorldUUID(),
                        this.arena.getPoolMinPoint(),
                        this.arena.getPoolMaxPoint()
        );
    }

    @Override
    public void run() {
        switch (this.state) {
            case WAITING:
                if (this.canStart()) {
                    this.state = DeState.STARTING;
                } else {
                    this.players.sendActionBar(MessageKey.ACTIONBAR_WAITING);
                }
                return;
            case STARTING:
                this.tickCountdown();
                if (this.timer < 0) {
                    this.players.hide();
                    this.onCountdownEnd();
                    new BlockBalancer(this.players).balance(this.plugin.getPlayerAdapter().getAvailableBlocks());
                    this.state = DeState.PLAYING;
                }
                return;
            case PLAYING:
                this.tickGame();
                this.timer--;
                if (this.timer < 0) {
                    this.onTimerEnd();
                }
                return;
            default:
                this.end(null);
                return;
        }
    }

    protected void tickCountdown() {
        if (!this.canStart()) {
            this.state = DeState.STARTING;
            this.timer = this.plugin.getMainConfig().getCountdownTime() * Ticks.TICKS_PER_SECOND;
            this.players.sendActionBar(MessageKey.ACTIONBAR_NOT_ENOUGH_PLAYERS);
            return;
        }
        this.players.progress(this.timer, this.countdownTicks);
        this.timer--;
        if (this.timer <= TIMER_SOUND_THRESHOLD && this.timer % Ticks.TICKS_PER_SECOND == 0) {
            this.players.playSound(GameConfig.getTimerSound(this.timer));
        }
    }

    protected abstract void onCountdownEnd();

    protected abstract void onTimerEnd();

    protected abstract void tickGame();

    protected abstract void onJump(@Nullable Player player, @NotNull InGamePlayer inGamePlayer, @NotNull JumpVerdict verdict);

    protected @Nullable InGamePlayer getFirstPlayer() {
        Comparator<InGamePlayer> comparator = Comparator.comparingInt(InGamePlayer::getPoints);
        return this.players.stream()
                .filter(player -> !player.isSpectator())
                .max(comparator)
                .orElse(null);
    }

    @Override
    public boolean verdict(@NotNull UUID playerUUID, @NotNull JumpVerdict verdict) {
        InGamePlayer player = this.players.get(playerUUID);
        if (player == null) {
            throw new IllegalArgumentException("The player with the " + playerUUID + " uuid is not in the game.");
        }

        this.onJump(this.plugin.getPlayer(playerUUID), player, verdict);
        return true;
    }

    protected void sendJumpMessage(@NotNull Player player, @NotNull InGamePlayer inGamePlayer, @NotNull JumpVerdict verdict) {
        if (verdict == JumpVerdict.MISSED) {
            Template lifesTemplate = Template.of("lifes", String.valueOf(inGamePlayer.getPoints()));
            if (inGamePlayer.getPoints() > 1) {
                player.sendActionBar(MessageKey.ACTIONBAR_MISSED_LIFES, lifesTemplate);
            } else {
                player.sendActionBar(MessageKey.ACTIONBAR_MISSED_LIFE, lifesTemplate);
            }
            player.playSound(GameConfig.getJumpFailedSound());
        } else if (verdict == JumpVerdict.COMBO) {
            player.sendActionBar(MessageKey.ACTIONBAR_COMBO);
            player.playSound(GameConfig.getJumpSucceedSound());
            this.plugin.spawnFireworks(player.getLocation());
        } else {
            player.sendActionBar(MessageKey.ACTIONBAR_SUCCESSFUL_JUMP);
        }
    }

    protected void end(@Nullable InGamePlayer latest) {
        this.plugin.getEventAdapter().callGameEndEvent(this, latest);
        this.jumperMedia.hide(this.players);
        Player latestPlayer = latest == null ? null : this.plugin.getPlayer(latest.uuid());
        if (latestPlayer != null) {
            this.plugin.spawnFireworks(latestPlayer.getLocation()).spawn(4);
        }
        for (InGamePlayer player : this.players) {
            if (player.equals(latest)) {
                player.incrementWins();
            } else {
                player.incrementLosses();
            }
            this.plugin.getStatsService().savePlayerStatistics(player);
            this.plugin.getStatsService().updateLeaderboard(player);
            this.plugin.getGameService().setPlayerGame(player.uuid(), null);

            if (this.plugin.getMainConfig().doesTeleportAtEnd()) {
                Player p = this.plugin.getPlayer(player.uuid());
                if (p != null) {
                    p.teleport(this.arena.getSpawnLocation());
                }
            }
        }
        this.players.sendActionBar(MessageKey.ACTIONBAR_ENDED);
        this.players.clear();
        for (PoolBlock block : this.blocks) {
            block.setBlockData(Blocks.WATER);
        }
        this.arena.checkGame();
    }

    @Override
    public @NotNull Arena getArena() {
        return this.arena;
    }

    @Override
    public @NotNull DeState getState() {
        return this.state;
    }

    @Override
    public boolean acceptPlayers() {
        return this.state.acceptPlayers() && this.players.size() < this.arena.getMaxPlayers();
    }

    @Override
    public boolean canStart() {
        return this.state.acceptPlayers() && this.players.size() >= this.arena.getMinPlayers();
    }

    @Override
    public @NotNull Set<DePlayer> getAlivePlayers() {
        Set<DePlayer> players = new HashSet<>(Math.min(10, this.players.size()));
        for (DePlayer player : this.players) {
            if (player.getPoints() > 0 && !player.isSpectator()) {
                players.add(player);
            }
        }
        return Collections.unmodifiableSet(players);
    }

    @Override
    public @NotNull Set<DePlayer> getPlayers() {
        return Collections.unmodifiableSet(this.players.playerSet());
    }

    public @NotNull Set<InGamePlayer> getPlayers(@NotNull BlockData blockData) {
        Set<InGamePlayer> set = new HashSet<>();
        for (InGamePlayer player : this.players) {
            if (player.getChosenBlock().equals(blockData)) {
                set.add(player);
            }
        }
        return set;
    }

    public @Nullable DePlayer getPlayer(@NotNull UUID uuid) {
        return this.players.get(uuid);
    }

    @Override
    public @NotNull CompletableFuture<Boolean> addPlayer(@NotNull UUID uuid) {
        return this.addPlayer(Objects.requireNonNull(this.plugin.getPlayer(uuid), "Player is offline."));
    }

    public @NotNull CompletableFuture<Boolean> addPlayer(@NotNull Player player) {
        if (this.players.contains(player.uuid())) {
            return CompletableFuture.completedFuture(false);
        }
        if (this.plugin.getEventAdapter().callPlayerJoinArenaEvent(this, player)) {
            return CompletableFuture.completedFuture(false);
        }

        return this.plugin.getStatsService().getPlayerStatistics(player.uuid())
                .thenApply(optional -> {
                    BlockData block = this.plugin.getPlayerAdapter().getRandomBlock();
                    InGamePlayer inGamePlayer = optional
                            .map(stats -> new InGamePlayer(stats, block, this))
                            .orElseGet(() -> new InGamePlayer(player.uuid(), player.name(), block, this));
                    if (this.players.add(inGamePlayer)) {
                        this.plugin.getGameService().setPlayerGame(player.uuid(), this);
                        return true;
                    }
                    return false;
                }).exceptionally(throwable -> {
                    this.plugin.getLogger().log(Level.SEVERE, "Exception when adding the player to the arena.", throwable.getCause());
                    return null;
                });
    }

    @Override
    public boolean removePlayer(@NotNull UUID player) {
        if (!this.players.remove(player)) {
            return false;
        }
        this.plugin.getGameService().setPlayerGame(player, null);
        return true;
    }

    @Override
    public boolean isEmpty() {
        return this.players.isEmpty();
    }

    @Override
    public int size() {
        return this.players.size();
    }

    @Override
    public @NotNull Audience audience() {
        return this.players;
    }

    @Override
    public int getRemainingWaterBlocks() {
        if (this.arena.getPoolMinPoint() == null || this.arena.getPoolMaxPoint() == null) {
            throw new IllegalStateException("The pool dimensions have not been defined.");
        }
        return this.remainingWaterBlocks.size();
    }

    void cancel() {
        this.task.cancel();
    }
}
