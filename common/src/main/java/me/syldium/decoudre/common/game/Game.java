package me.syldium.decoudre.common.game;

import me.syldium.decoudre.api.arena.DeGame;
import me.syldium.decoudre.api.arena.DeState;
import me.syldium.decoudre.api.player.DePlayer;
import me.syldium.decoudre.api.player.JumpVerdict;
import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.config.GameConfig;
import me.syldium.decoudre.common.player.InGamePlayer;
import me.syldium.decoudre.common.player.Message;
import me.syldium.decoudre.common.player.Player;
import me.syldium.decoudre.common.util.PlayerMap;
import me.syldium.decoudre.common.util.Task;
import me.syldium.decoudre.common.world.BlockData;
import me.syldium.decoudre.common.world.Blocks;
import me.syldium.decoudre.common.world.PoolBlock;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Game implements DeGame, Runnable {

    private final DeCoudrePlugin plugin;
    private final Arena arena;
    private final PlayerMap<InGamePlayer> players;
    private final Queue<UUID> queue = new ArrayDeque<>();
    private final List<PoolBlock> blocks = new ArrayList<>();
    private final Task task;
    private UUID jumper;

    private DeState state = DeState.WAITING;
    private int timer = 100;

    private final int yThreshold;

    public Game(@NotNull DeCoudrePlugin plugin, @NotNull Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
        this.players = new PlayerMap<>(plugin);
        this.yThreshold = arena.getJumpLocation().getBlockY() - 10;
        this.task = plugin.startGameTask(this);
    }

    @Override
    public void run() {
        switch (this.state) {
            case WAITING: {
                if (this.canStart()) {
                    this.state = DeState.STARTING;
                } else {
                    this.players.sendActionBar(Message.WAITING);
                }
                return;
            }
            case STARTING: {
                if (!this.canStart()) {
                    this.state = DeState.STARTING;
                    this.timer = 100;
                    this.players.sendActionBar(Message.NOT_ENOUGH_PLAYERS.format());
                    return;
                }
                int countdown = this.timer / 20;
                this.players.sendActionBar(Component.text(countdown, NamedTextColor.BLUE));
                this.timer--;
                if (this.timer < 1) {
                    this.queue.addAll(this.players.uuidSet());
                    this.state = DeState.PLAYING;
                }
                return;
            }
            case PLAYING: {
                if (this.jumper == null) {
                    if (this.queue.isEmpty()) {
                        this.state = DeState.END;
                        return;
                    }
                    Player player = this.plugin.getPlayer(this.queue.poll());
                    if (player != null) {
                        player.teleport(this.arena.getJumpLocation());
                        this.jumper = player.getUuid();
                    }
                    return;
                }
                Player jumper = this.plugin.getPlayer(this.jumper);
                if (jumper.isInWater()) {
                    PoolBlock block = jumper.getFirstLiquidBlock();
                    block.setBlockData(this.players.get(jumper).getChosenBlock());
                    this.blocks.add(block);
                    JumpVerdict verdict = this.plugin.getPlayerAdapter().isDeCoudre(block) ? JumpVerdict.COMBO : JumpVerdict.LANDED;
                    this.handleJump(jumper, this.players.get(jumper), verdict);
                } else if (jumper.getLocation().getBlockY() < this.yThreshold) {
                    for (PoolBlock block : jumper.getBlocksBelow()) {
                        if (!block.isPassable()) {
                            this.handleJump(jumper, this.players.get(jumper), JumpVerdict.MISSED);
                        }
                    }
                }
                return;
            }
            default:
                this.end(null);
                return;
        }
    }

    private void handleJump(@NotNull Player player, @NotNull InGamePlayer inGamePlayer, @NotNull JumpVerdict verdict) {
        if (verdict == JumpVerdict.MISSED) {
            inGamePlayer.decrementLifes();
            player.sendActionBar(Message.LIFES_LEFT.format(inGamePlayer.getLifes()));
            player.playSound(GameConfig.getJumpFailedSound());
        } else {
            inGamePlayer.incrementJumps();
            if (verdict == JumpVerdict.COMBO) {
                inGamePlayer.incrementLifes();
                inGamePlayer.incrementDacs();
                player.sendActionBar(Message.COMBO);
                player.playSound(GameConfig.getJumpSucceedSound());
            } else {
                player.sendActionBar(Message.SUCCESSFUL_JUMP);
            }
        }

        this.jumper = null;
        if (inGamePlayer.getLifes() > 0) {
            this.queue.offer(player.getUuid());
            player.teleport(this.arena.getSpawnLocation());
        } else if (this.queue.size() < 2) {
            this.end(inGamePlayer);
        }
    }

    private void end(@Nullable InGamePlayer latest) {
        for (InGamePlayer player : this.players) {
            if (player.equals(latest)) {
                player.incrementWins();
            } else {
                player.incrementLosses();
            }
            this.plugin.getStatsService().savePlayerStatistics(player);
            this.plugin.getGamesService().setPlayerGame(player.uuid(), null);
        }
        this.players.sendActionBar(Message.END_GAME);
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
            if (player.getLifes() > 0) {
                players.add(player);
            }
        }
        return Collections.unmodifiableSet(players);
    }

    @Override
    public @NotNull Set<DePlayer> getPlayers() {
        return Collections.unmodifiableSet(this.players.playerSet());
    }

    @Override
    public @NotNull CompletableFuture<Boolean> addPlayer(@NotNull UUID player) {
        if (this.players.contains(player)) {
            return CompletableFuture.completedFuture(false);
        }

        return this.plugin.getStatsService().getPlayerStatistics(player)
                .thenApply(optional -> {
                    BlockData block = this.plugin.getPlayerAdapter().getRandomWool();
                    InGamePlayer inGamePlayer = optional.map(stats -> new InGamePlayer(stats, block)).orElseGet(() -> new InGamePlayer(player, block));
                    if (this.players.add(inGamePlayer)) {
                        this.plugin.getGamesService().setPlayerGame(player, this);
                        return true;
                    }
                    return false;
                });
    }

    @Override
    public boolean removePlayer(@NotNull UUID player) {
        if (!this.players.remove(player)) {
            return false;
        }

        this.plugin.getGamesService().setPlayerGame(player, null);
        return true;
    }

    @Override
    public boolean isEmpty() {
        return this.players.isEmpty();
    }

    @Override
    public @Nullable UUID getCurrentJumper() {
        return this.jumper;
    }

    @Override
    public @Nullable UUID peekNextJumper() {
        return this.queue.peek();
    }

    void cancel() {
        this.task.cancel();
    }
}
