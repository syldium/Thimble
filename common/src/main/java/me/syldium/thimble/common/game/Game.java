package me.syldium.thimble.common.game;

import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.player.ThimblePlayerStats;
import me.syldium.thimble.api.util.BlockVector;
import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.arena.ThimbleState;
import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.api.player.JumpVerdict;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.adapter.BlockBalancer;
import me.syldium.thimble.common.config.GameConfig;
import me.syldium.thimble.common.config.MainConfig;
import me.syldium.thimble.common.player.InGamePlayer;
import me.syldium.thimble.common.player.MessageKey;
import me.syldium.thimble.common.player.Player;
import me.syldium.thimble.common.player.media.TimedMedia;
import me.syldium.thimble.common.util.PlayerMap;
import me.syldium.thimble.common.util.Task;
import me.syldium.thimble.common.world.BlockData;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.util.Ticks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
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
public abstract class Game implements ThimbleGame, Runnable {

    private static final int TIMER_SOUND_THRESHOLD = Ticks.TICKS_PER_SECOND * 5;

    protected final ThimblePlugin plugin;
    protected final Arena arena;
    protected final Task task;

    protected ThimbleState state = ThimbleState.WAITING;
    protected final PlayerMap<InGamePlayer> players;
    protected final TimedMedia jumperMedia;

    protected Set<BlockVector> remainingWaterBlocks = null;
    protected final Map<BlockVector, BlockData> blocks = new HashMap<>();

    protected int timer, messageTimer;
    protected final int countdownTicks;
    protected final int fireworksThimble, fireworksEnd;
    protected final boolean spectatorMode, teleportSpawnAtEnd;

    public Game(@NotNull ThimblePlugin plugin, @NotNull Arena arena) {
        MainConfig config = plugin.getMainConfig();
        this.plugin = plugin;
        this.arena = arena;
        this.players = new PlayerMap<>(plugin);
        this.timer = config.getGameInt("countdown-time", 30) * Ticks.TICKS_PER_SECOND;
        this.messageTimer = 0;
        this.task = plugin.startGameTask(this);
        this.jumperMedia = TimedMedia.from(plugin.getMainConfig(), "jump");

        this.countdownTicks = this.timer;
        this.fireworksThimble = config.getGameNode().getInt("fireworks-thimble", 1);
        this.fireworksEnd = config.getGameNode().getInt("fireworks-end", 4);
        this.spectatorMode = config.getGameNode().getBool("spectator-mode", true);
        this.teleportSpawnAtEnd = config.getGameNode().getBool("teleport-at-end", false);
    }

    @Override
    public void run() {
        switch (this.state) {
            case WAITING:
                if (this.canStart() && !this.plugin.getEventAdapter().callGameChangeState(this, ThimbleState.STARTING)) {
                    this.state = ThimbleState.STARTING;
                } else if ((this.messageTimer++ & 0xF) == 0) {
                    this.players.sendActionBar(MessageKey.ACTIONBAR_WAITING);
                }
                return;
            case STARTING:
                this.tickCountdown();
                if (this.timer <= 0) {
                    if (this.plugin.getEventAdapter().callGameChangeState(this, ThimbleState.PLAYING)) {
                        this.timer = 0;
                    } else {
                        this.players.hide();
                        this.onCountdownEnd();
                        new BlockBalancer(this.players).balance(this.plugin.getPlayerAdapter().getAvailableBlocks());
                        this.state = ThimbleState.PLAYING;
                    }
                }
                return;
            case PLAYING:
                if (this.players.isEmpty()) {
                    this.plugin.getEventAdapter().callGameEndEvent(this, null);
                    this.state = ThimbleState.END;
                    return;
                }
                this.tickGame();
                if (--this.timer <= 0) {
                    this.onTimerEnd();
                }
                return;
            case END:
                if (--this.timer <= 0) {
                    this.closeArena();
                }
                return;
            default:
                this.end(null);
                return;
        }
    }

    protected void tickCountdown() {
        if (!this.canStart() && !this.plugin.getEventAdapter().callGameChangeState(this, ThimbleState.WAITING)) {
            if (this.players.isEmpty()) {
                this.arena.checkGame();
                return;
            }
            this.state = ThimbleState.WAITING;
            this.timer = this.countdownTicks;
            this.players.sendActionBar(MessageKey.ACTIONBAR_NOT_ENOUGH_PLAYERS);
            return;
        }
        this.players.progress(this.timer, this.countdownTicks);
        this.timer--;
        if (this.timer <= TIMER_SOUND_THRESHOLD && this.timer % Ticks.TICKS_PER_SECOND == 0) {
            this.players.playSound(GameConfig.getTimerSound(this.timer));
        }
    }

    @VisibleForTesting
    public abstract void onCountdownEnd();

    @VisibleForTesting
    public abstract void onTimerEnd();

    @VisibleForTesting
    public abstract void tickGame();

    @VisibleForTesting
    public abstract void onJump(@Nullable Player player, @NotNull InGamePlayer inGamePlayer, @NotNull JumpVerdict verdict);

    protected @Nullable InGamePlayer getFirstPlayer() {
        Comparator<InGamePlayer> comparator = Comparator.comparingInt(InGamePlayer::getPoints);
        return this.players.stream()
                .filter(player -> !player.isVanished())
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
        } else if (verdict == JumpVerdict.THIMBLE) {
            player.sendActionBar(MessageKey.ACTIONBAR_THIMBLE);
            player.playSound(GameConfig.getJumpSucceedSound());
            this.plugin.spawnFireworks(player.getLocation().up(2)).spawn(this.fireworksThimble);
        } else {
            player.sendActionBar(MessageKey.ACTIONBAR_SUCCESSFUL_JUMP);
        }
    }

    protected void end(@Nullable InGamePlayer latest) {
        this.plugin.getEventAdapter().callGameEndEvent(this, latest);
        this.state = ThimbleState.END;
        this.jumperMedia.hide(this.players);
        Location fireworksLocation = this.getFireworkLocation(latest == null ? null : this.plugin.getPlayer(latest.uuid()));
        if (fireworksLocation != null) {
            this.plugin.spawnFireworks(fireworksLocation).spawn(this.fireworksEnd);
        }

        for (InGamePlayer player : this.players) {
            if (player.isVanished()) continue;

            if (player.equals(latest)) {
                player.incrementWins();
            } else {
                player.incrementLosses();
            }

            this.plugin.getStatsService().savePlayerStatistics(player);
            this.plugin.getStatsService().updateLeaderboard(player);
        }

        this.timer = this.plugin.getMainConfig().getGameNode().getInt("end-time", 5) * Ticks.TICKS_PER_SECOND;
    }

    private void closeArena() {
        for (InGamePlayer player : this.players) {
            this.plugin.getGameService().setPlayerGame(player.uuid(), null);

            Player p = this.plugin.getPlayer(player.uuid());
            if (p != null) {
                this.plugin.getSavedPlayersManager().restore(p, !this.teleportSpawnAtEnd);
                if (this.teleportSpawnAtEnd) {
                    p.teleport(this.arena.getSpawnLocation());
                }
            } else {
                this.plugin.getSavedPlayersManager().getPending().add(player.uuid());
            }
        }

        this.players.sendActionBar(MessageKey.ACTIONBAR_ENDED);
        this.players.clear();
        this.plugin.getPlayerAdapter().clearPool(this.arena.getJumpLocation().getWorldUUID(), this.blocks);
        this.blocks.clear();
        this.arena.checkGame();
    }

    private @Nullable Location getFireworkLocation(@Nullable Player player) {
        if (this.arena.getPoolCenterPoint() != null) {
            return new Location(this.arena.getJumpLocation().getWorldUUID(), this.arena.getPoolCenterPoint());
        }
        return player == null ? null : player.getLocation();
    }

    @Override
    public @NotNull Arena getArena() {
        return this.arena;
    }

    @Override
    public @NotNull ThimbleState getState() {
        return this.state;
    }

    @Override
    public boolean acceptPlayer() {
        return this.state.isNotStarted() && this.players.size() < this.arena.getMaxPlayers();
    }

    @Override
    public boolean acceptPlayers(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count must be positive!");
        }
        return this.state.isNotStarted() && (this.players.size() + count) <= this.arena.getMaxPlayers();
    }

    @Override
    public boolean canStart() {
        return this.state.isNotStarted() && this.players.size() >= this.arena.getMinPlayers();
    }

    @Override
    public @NotNull Set<ThimblePlayer> getAlivePlayers() {
        Set<ThimblePlayer> players = new HashSet<>(Math.min(10, this.players.size()));
        for (ThimblePlayer player : this.players) {
            if (player.getPoints() > 0 && !player.isSpectator() && !player.isVanished()) {
                players.add(player);
            }
        }
        return Collections.unmodifiableSet(players);
    }

    @Override
    public @NotNull Set<ThimblePlayer> getPlayers() {
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

    public @Nullable InGamePlayer getPlayer(@NotNull UUID uuid) {
        return this.players.get(uuid);
    }

    @Override
    public @NotNull CompletableFuture<Boolean> addPlayer(@NotNull UUID uuid) {
        if (this.players.contains(uuid)) {
            return CompletableFuture.completedFuture(false);
        }
        Player player = this.plugin.getPlayer(uuid);
        if (player == null) {
            throw new IllegalArgumentException("Player is offline.");
        }
        if (this.plugin.getEventAdapter().callPlayerJoinArenaEvent(this, player)) {
            return CompletableFuture.completedFuture(false);
        }
        return this.getStatisticsToJoin(uuid);
    }

    public @NotNull CompletableFuture<Boolean> addPlayer(@NotNull Player player) {
        if (this.players.contains(player.uuid())) {
            return CompletableFuture.completedFuture(false);
        }
        if (this.plugin.getEventAdapter().callPlayerJoinArenaEvent(this, player)) {
            return CompletableFuture.completedFuture(false);
        }
        return this.getStatisticsToJoin(player.uuid());
    }

    private @NotNull CompletableFuture<Boolean> getStatisticsToJoin(@NotNull UUID uuid) {
        return this.plugin.getStatsService().getPlayerStatistics(uuid)
                .thenCompose(optional -> this.plugin.runSync(() -> this.addPlayer(uuid, optional)))
                .exceptionally(throwable -> {
                    this.plugin.getLogger().log(Level.SEVERE, "Exception when adding the player to the arena.", throwable.getCause());
                    return null;
                });
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private boolean addPlayer(@NotNull UUID uuid, @NotNull Optional<ThimblePlayerStats> statsOpt) {
        Player player = this.plugin.getPlayer(uuid);
        if (player == null) {
            return false;
        }

        BlockData block = this.plugin.getPlayerAdapter().getRandomBlock();
        InGamePlayer inGamePlayer = statsOpt
                .map(stats -> new InGamePlayer(player, stats, block, this))
                .orElseGet(() -> new InGamePlayer(player, block, this));

        if (this.players.add(inGamePlayer)) {
            this.plugin.getGameService().setPlayerGame(player.uuid(), this);

            this.players.sendMessage(
                MessageKey.CHAT_JOINED,
                p -> {
                    if (!p.uuid().equals(player.uuid())) {
                        return false;
                    }
                    return player.isVanished() && p.isVanished() || !player.isVanished();
                },
                Template.of("player", player.name())
            );

            if (!player.isVanished()) {
                this.plugin.getSavedPlayersManager().save(player);
                player.setMiniGameMode();
            }
            if (this.players.realSize() > 1) {
                player.teleport(this.arena.getSpawnLocation());
            } else {
                player.teleportAsync(this.arena.getSpawnLocation());
            }
            if (this.remainingWaterBlocks == null && this.players.size() >= this.arena.getMinPlayers()) {
                this.searchRemainingBlocks();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean removePlayer(@NotNull UUID player, boolean teleport) {
        InGamePlayer inGamePlayer = this.players.remove(player);
        if (inGamePlayer == null) {
            return false;
        }

        this.plugin.getGameService().setPlayerGame(player, null);
        Player p = this.plugin.getPlayer(player);
        if (p != null) {
            this.players.sendMessage(MessageKey.CHAT_LEFT, Template.of("player", p.name()));
            this.plugin.getSavedPlayersManager().restore(p, teleport && !this.teleportSpawnAtEnd);
            if (teleport && this.teleportSpawnAtEnd) {
                p.teleport(this.arena.getSpawnLocation());
            }
        }
        this.arena.checkGame();
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
    public int realSize() {
        return this.players.realSize();
    }

    @Override
    public @NotNull Audience audience() {
        return this.players;
    }

    @Override
    public @NotNull Set<BlockVector> getRemainingWaterBlocks() {
        if (this.arena.getPoolMinPoint() == null || this.arena.getPoolMaxPoint() == null) {
            throw new IllegalStateException("The pool dimensions have not been defined.");
        }
        if (this.remainingWaterBlocks == null) {
            this.searchRemainingBlocks();
        }
        return this.remainingWaterBlocks;
    }

    public void spectate(@NotNull ThimblePlayer inGamePlayer, @NotNull UUID playerUniqueId) {
        Player player = this.plugin.getPlayer(playerUniqueId);
        if (player == null) {
            return;
        }

        if (this.spectatorMode && inGamePlayer.isSpectator()) {
            player.spectate();
        }
        player.teleport(this.arena.getWaitLocation());
    }

    private void searchRemainingBlocks() {
        this.remainingWaterBlocks = this.arena.getPoolMinPoint() == null || this.arena.getPoolMaxPoint() == null ?
                Collections.emptySet()
                : this.plugin.getPlayerAdapter().getRemainingWaterBlocks(
                this.arena.getJumpLocation().getWorldUUID(),
                this.arena.getPoolMinPoint(),
                this.arena.getPoolMaxPoint()
        );
    }

    void cancel() {
        this.task.cancel();
    }
}
