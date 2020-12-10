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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class Game implements DeGame, Runnable {

    private static final int TIMER_SOUND_THRESHOLD = Ticks.TICKS_PER_SECOND * 5;

    private final DeCoudrePlugin plugin;
    private final Arena arena;
    private final Task task;

    private DeState state = DeState.WAITING;
    private final PlayerMap<InGamePlayer> players;
    private final Queue<UUID> queue = new ArrayDeque<>();
    private final TimedMedia jumperMedia;
    private UUID jumper;

    private final Set<BlockVector> remainingWaterBlocks;
    private final List<PoolBlock> blocks = new ArrayList<>();

    private int timer;
    private final int countdownTicks;
    private final int jumpTicks;

    public Game(@NotNull DeCoudrePlugin plugin, @NotNull Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
        this.players = new PlayerMap<>(plugin);
        this.timer = plugin.getMainConfig().getCountdownTime() * Ticks.TICKS_PER_SECOND;
        this.task = plugin.startGameTask(this);
        this.jumperMedia = TimedMedia.from(plugin.getMainConfig(), "jump");

        this.countdownTicks = this.plugin.getMainConfig().getCountdownTime() * Ticks.TICKS_PER_SECOND;
        this.jumpTicks = this.plugin.getMainConfig().getJumpTime() * Ticks.TICKS_PER_SECOND;

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
            case WAITING: {
                if (this.canStart()) {
                    this.state = DeState.STARTING;
                } else {
                    this.players.sendActionBar(MessageKey.ACTIONBAR_WAITING);
                }
                return;
            }
            case STARTING: {
                if (!this.canStart()) {
                    this.state = DeState.STARTING;
                    this.timer = this.plugin.getMainConfig().getCountdownTime() * Ticks.TICKS_PER_SECOND;
                    this.players.sendActionBar(MessageKey.ACTIONBAR_NOT_ENOUGH_PLAYERS);
                    return;
                }
                this.players.progress((float) this.timer / this.countdownTicks, (int) Math.ceil((float) this.timer / Ticks.TICKS_PER_SECOND));
                this.timer--;
                if (this.timer <= TIMER_SOUND_THRESHOLD && this.timer % Ticks.TICKS_PER_SECOND == 0) {
                    this.players.playSound(GameConfig.getTimerSound(this.timer / Ticks.TICKS_PER_SECOND));
                }
                if (this.timer < 0) {
                    for (InGamePlayer player : this.players) {
                        if (!player.isSpectator()) {
                            this.queue.add(player.uuid());
                        }
                    }
                    this.players.hide();
                    new BlockBalancer(this.players).balance(this.plugin.getPlayerAdapter().getAvailableBlocks());
                    this.state = DeState.PLAYING;
                }
                return;
            }
            case PLAYING: {
                if (this.jumper == null) {
                    this.timer = this.plugin.getMainConfig().getJumpTime() * Ticks.TICKS_PER_SECOND;
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
                this.timer--;

                Player jumper = this.plugin.getPlayer(this.jumper);
                if (jumper == null) {
                    this.handleJump(null, this.players.get(this.jumper), JumpVerdict.MISSED);
                    return;
                }

                this.jumperMedia.progress(jumper, (float) this.timer / this.jumpTicks, (int) Math.ceil((float) this.timer / Ticks.TICKS_PER_SECOND));
                if (jumper.isInWater()) {
                    PoolBlock block = jumper.getFirstLiquidBlock();
                    block.setBlockData(this.players.get(jumper).getChosenBlock());
                    this.blocks.add(block);
                    JumpVerdict verdict = this.plugin.getPlayerAdapter().isDeCoudre(block) ? JumpVerdict.COMBO : JumpVerdict.LANDED;
                    this.handleJump(jumper, this.players.get(jumper), verdict);

                    if (this.remainingWaterBlocks.remove(block.getPosition()) && this.remainingWaterBlocks.isEmpty()) {
                        Comparator<InGamePlayer> comparator = Comparator.comparingInt(InGamePlayer::getLifes);
                        this.end(this.players.stream()
                                .filter(player -> !player.isSpectator())
                                .max(comparator)
                                .orElse(null)
                        );
                    }
                }

                if (this.timer < 1) {
                    this.handleJump(jumper, this.players.get(jumper), JumpVerdict.MISSED);
                }
                return;
            }
            default:
                this.end(null);
                return;
        }
    }

    private void handleJump(@Nullable Player player, @NotNull InGamePlayer inGamePlayer, @NotNull JumpVerdict verdict) {
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
            this.handleJumpWithPlayer(player, inGamePlayer, verdict);
        }

        this.jumper = null;
        if (inGamePlayer.getLifes() > 0) {
            this.queue.offer(inGamePlayer.uuid());
        } else if (this.queue.size() < 2) {
            this.end(inGamePlayer);
        }
    }

    private void handleJumpWithPlayer(@NotNull Player player, @NotNull InGamePlayer inGamePlayer, @NotNull JumpVerdict verdict) {
        if (verdict == JumpVerdict.MISSED) {
            Template lifesTemplate = Template.of("lifes", String.valueOf(inGamePlayer.getLifes()));
            if (inGamePlayer.getLifes() > 1) {
                player.sendActionBar(MessageKey.ACTIONBAR_MISSED_PLURAL, lifesTemplate);
            } else {
                player.sendActionBar(MessageKey.ACTIONBAR_MISSED_SINGULAR, lifesTemplate);
            }
            player.playSound(GameConfig.getJumpFailedSound());
        } else if (verdict == JumpVerdict.COMBO) {
            player.sendActionBar(MessageKey.ACTIONBAR_COMBO);
            player.playSound(GameConfig.getJumpSucceedSound());
        } else {
            player.sendActionBar(MessageKey.ACTIONBAR_SUCCESSFUL_JUMP);
        }
        player.teleport(this.arena.getSpawnLocation());
    }

    private void end(@Nullable InGamePlayer latest) {
        this.plugin.getEventAdapter().callGameEndEvent(this, latest);
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
            if (player.getLifes() > 0 && !player.isSpectator()) {
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

        if (Objects.equals(this.jumper, player)) {
            this.jumper = null;
            Player p = this.plugin.getPlayer(player);
            if (p != null) {
                this.jumperMedia.hide(p);
            }
        }
        this.plugin.getGameService().setPlayerGame(player, null);
        return true;
    }

    @Override
    public boolean verdict(@NotNull JumpVerdict verdict) {
        if (this.jumper == null) {
            throw new IllegalStateException("No players are currently jumping.");
        }

        this.handleJump(this.plugin.getPlayer(this.jumper), this.players.get(this.jumper), verdict);
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
    public @Nullable UUID getCurrentJumper() {
        return this.jumper;
    }

    @Override
    public @Nullable UUID peekNextJumper() {
        return this.queue.peek();
    }

    @Override
    public @NotNull Audience audience() {
        return this.players;
    }

    void cancel() {
        this.task.cancel();
    }
}
