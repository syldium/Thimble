package me.syldium.thimble.common.game;

import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.util.BlockPos;
import me.syldium.thimble.api.util.BlockVector;
import me.syldium.thimble.api.arena.ThimbleArena;
import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.arena.ThimbleGameMode;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.player.Player;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class Arena implements ThimbleArena {

    private static final Pattern VALID_NAME = Pattern.compile("\\w+");
    private static final String UNSET_LOCATION_IN_GAME = "Cannot unset the %s location on a game that has already been started.";

    private final String name;
    private Location spawnLocation, jumpLocation, waitLocation;
    private BlockVector minimumPoint, maximumPoint;
    private int minPlayers = 1;
    private int maxPlayers = 8;

    private Game game;
    private final ThimblePlugin plugin;
    private ThimbleGameMode gameMode = ThimbleGameMode.SINGLE;

    public Arena(@NotNull ThimblePlugin plugin, @NotNull String name) {
        if (!VALID_NAME.matcher(name).matches()) {
            throw new IllegalArgumentException("Invalid arena name. Must be [A-Za-z0-9_]: " + name);
        }
        this.plugin = plugin;
        this.name = name;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public @Nullable Location getSpawnLocation() {
        return this.spawnLocation;
    }

    @Override
    public void setSpawnLocation(@Nullable Location location) {
        if (this.game != null && location == null) {
            throw new IllegalStateException(String.format(UNSET_LOCATION_IN_GAME, "spawn"));
        }
        this.spawnLocation = location;
    }

    @Override
    public @Nullable Location getJumpLocation() {
        return this.jumpLocation;
    }

    @Override
    public void setJumpLocation(@Nullable Location location) {
        if (this.game != null && location == null) {
            throw new IllegalStateException(String.format(UNSET_LOCATION_IN_GAME, "jump"));
        }
        this.jumpLocation = location;
    }

    @Override
    public @Nullable Location getWaitLocation() {
        return this.waitLocation;
    }

    @Override
    public void setWaitLocation(@Nullable Location location) {
        if (this.game != null && location == null) {
            throw new IllegalStateException(String.format(UNSET_LOCATION_IN_GAME, "wait"));
        }
        this.waitLocation = location;
    }

    @Override
    public @Range(from = 1, to = Integer.MAX_VALUE) int getMinPlayers() {
        return this.minPlayers;
    }

    @Override
    public void setMinPlayers(int minimum) throws IllegalArgumentException {
        if (minimum < 1 || minimum > this.maxPlayers) {
            throw new IllegalArgumentException();
        }
        this.minPlayers = minimum;
    }

    @Override
    public @Range(from = 1, to = Integer.MAX_VALUE) int getMaxPlayers() {
        return this.maxPlayers;
    }

    @Override
    public void setMaxPlayers(int maximum) throws IllegalArgumentException {
        if (maximum < 1 || maximum < this.minPlayers) {
            throw new IllegalArgumentException();
        }
        this.maxPlayers = maximum;
    }

    @Override
    public @NotNull Optional<@NotNull ThimbleGame> getGame() {
        return Optional.ofNullable(this.game);
    }

    @Override
    public @NotNull ThimbleGameMode getGameMode() {
        return this.gameMode;
    }

    @Override
    public void setGameMode(@NotNull ThimbleGameMode gameMode) {
        this.gameMode = Objects.requireNonNull(gameMode, "The game mode cannot be null.");
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
    public boolean removePlayer(@NotNull UUID player) {
        return this.game.removePlayer(player);
    }

    @Override
    public @Nullable BlockVector getPoolMinPoint() {
        return this.minimumPoint;
    }

    @Override
    public void setPoolMinPoint(@NotNull BlockVector point) {
        this.minimumPoint = point;
    }

    @Override
    public @Nullable BlockVector getPoolMaxPoint() {
        return this.maximumPoint;
    }

    @Override
    public void setPoolMaxPoint(@NotNull BlockVector point) {
        this.maximumPoint = point;
    }

    @Override
    public @NotNull Set<@NotNull BlockPos> getSigns() {
        return this.plugin.getGameService().getArenaSigns(this);
    }

    @Override
    public @NotNull Component asComponent() {
        return Component.text(this.name);
    }

    void checkGame() {
        if (this.game != null && this.game.isEmpty()) {
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
