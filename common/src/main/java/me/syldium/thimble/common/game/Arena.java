package me.syldium.thimble.common.game;

import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.arena.ThimbleState;
import me.syldium.thimble.api.util.BlockPos;
import me.syldium.thimble.api.util.BlockVector;
import me.syldium.thimble.api.arena.ThimbleArena;
import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.arena.ThimbleGameMode;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.player.Player;
import me.syldium.thimble.common.player.ThimblePlaceholder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public class Arena implements ThimbleArena, ComponentLike {

    private static final String UNSET_LOCATION_IN_GAME = "Cannot unset the %s location on a game that has already been started.";

    private final String rawName;
    private final Component name;
    private Location spawnLocation, jumpLocation, waitLocation;
    private BlockVector centerPoint, minimumPoint, maximumPoint;
    private int minPlayers = 1;
    private int maxPlayers = 8;

    private Game game;
    private final ThimblePlugin plugin;
    private ThimbleGameMode gameMode = ThimbleGameMode.SINGLE;

    public Arena(@NotNull ThimblePlugin plugin, @NotNull String name) {
        this.plugin = plugin;
        this.rawName = name;
        this.name = miniMessage().deserialize(name);
    }

    @Override
    public @NotNull String name() {
        return this.rawName;
    }

    @Override
    public @Nullable Location spawnLocation() {
        return this.spawnLocation;
    }

    @Override
    public ThimbleArena setSpawnLocation(@Nullable Location location) {
        if (this.game != null && location == null) {
            throw new IllegalStateException(String.format(UNSET_LOCATION_IN_GAME, "spawn"));
        }
        this.spawnLocation = location;
        return this;
    }

    @Override
    public @Nullable Location jumpLocation() {
        return this.jumpLocation;
    }

    @Override
    public ThimbleArena setJumpLocation(@Nullable Location location) {
        if (this.game != null && location == null) {
            throw new IllegalStateException(String.format(UNSET_LOCATION_IN_GAME, "jump"));
        }
        this.jumpLocation = location;
        return this;
    }

    @Override
    public @Nullable Location waitLocation() {
        return this.waitLocation;
    }

    @Override
    public ThimbleArena setWaitLocation(@Nullable Location location) {
        if (this.game != null && location == null) {
            throw new IllegalStateException(String.format(UNSET_LOCATION_IN_GAME, "wait"));
        }
        this.waitLocation = location;
        return this;
    }

    @Override
    public @Range(from = 1, to = Integer.MAX_VALUE) int minPlayers() {
        return this.minPlayers;
    }

    @Override
    public ThimbleArena setMinPlayers(int minimum) throws IllegalArgumentException {
        if (minimum < 1 || minimum > this.maxPlayers) {
            throw new IllegalArgumentException();
        }
        this.minPlayers = minimum;
        return this;
    }

    @Override
    public @Range(from = 1, to = Integer.MAX_VALUE) int maxPlayers() {
        return this.maxPlayers;
    }

    @Override
    public ThimbleArena setMaxPlayers(int maximum) throws IllegalArgumentException {
        if (maximum < 1 || maximum < this.minPlayers) {
            throw new IllegalArgumentException();
        }
        this.maxPlayers = maximum;
        if (this.game != null) {
            this.game.players.updateAllScoreboards(ThimblePlaceholder.CAPACITY);
        }
        return this;
    }

    @Override
    public @NotNull Optional<@NotNull ThimbleGame> game() {
        return Optional.ofNullable(this.game);
    }

    @Override
    public @NotNull ThimbleGameMode gameMode() {
        return this.gameMode;
    }

    @Override
    public ThimbleArena setGameMode(@NotNull ThimbleGameMode gameMode) {
        this.gameMode = Objects.requireNonNull(gameMode, "The game mode cannot be null.");
        return this;
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> addPlayer(@NotNull UUID uuid) {
        this.initGame();
        return this.game.addPlayer(uuid);
    }

    public @NotNull CompletableFuture<@NotNull Boolean> addPlayer(@NotNull Player player) {
        this.initGame();
        return this.game.addPlayer(player);
    }

    private void initGame() {
        if (this.game == null) {
            if (!this.isSetup()) {
                throw new IllegalStateException(String.format("The %s arena is not correctly configured.", this.name));
            }

            if (this.gameMode == ThimbleGameMode.CONCURRENT) {
                this.game = new ConcurrentGame(this.plugin, this);
            } else {
                this.game = new SingleGame(this.plugin, this);
            }
        }
    }

    @Override
    public boolean removePlayer(@NotNull UUID player, boolean teleport) {
        if (this.game == null) {
            return false;
        }
        return this.game.removePlayer(player, teleport);
    }

    @Override
    public @Nullable BlockVector poolMinPoint() {
        return this.minimumPoint;
    }

    @Override
    public ThimbleArena setPoolMinPoint(@Nullable BlockVector point) {
        this.minimumPoint = point;
        return this.updatePoolCenter();
    }

    @Override
    public @Nullable BlockVector poolMaxPoint() {
        return this.maximumPoint;
    }

    @Override
    public ThimbleArena setPoolMaxPoint(@Nullable BlockVector point) {
        this.maximumPoint = point;
        return this.updatePoolCenter();
    }

    @Override
    public @Nullable BlockVector poolCenterPoint() {
        return this.centerPoint;
    }

    @Override
    public boolean isLoaded() {
        return this.plugin.isLoaded(this.spawnLocation)
                && this.plugin.isLoaded(this.waitLocation)
                && this.plugin.isLoaded(this.jumpLocation);
    }

    private @NotNull ThimbleArena updatePoolCenter() {
        if (this.minimumPoint != null && this.maximumPoint != null) {
            this.centerPoint = new BlockVector(
                    (this.minimumPoint.x() + this.maximumPoint.x()) / 2,
                    this.maximumPoint.y(),
                    (this.minimumPoint.z() + this.maximumPoint.z()) / 2
            );
        } else {
            this.centerPoint = null;
        }
        return this;
    }

    @Override
    public @NotNull Set<@NotNull BlockPos> signs() {
        return this.plugin.getGameService().arenaSigns(this);
    }

    @Override
    public @NotNull Component asComponent() {
        return this.name;
    }

    void checkGame() {
        if (this.game != null && this.game.isEmpty()) {
            if (this.game.state.isNotStarted()) {
                this.plugin.getEventAdapter().callGameAbortedEvent(this.game, this.game.state == ThimbleState.STARTING, true);
            }
            this.game.cancel();
            this.game = null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Arena arena = (Arena) o;
        return this.name.equals(arena.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    @Override
    public String toString() {
        return "Arena{" +
                "name='" + this.name + '\'' +
                ", minPlayers=" + this.minPlayers +
                ", maxPlayers=" + this.maxPlayers +
                ", hasGame=" + (this.game != null) +
                '}';
    }
}
