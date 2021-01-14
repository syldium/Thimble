package me.syldium.thimble.api.service;

import me.syldium.thimble.api.util.BlockPos;
import me.syldium.thimble.api.arena.ThimbleArena;
import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.player.ThimblePlayer;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * The manager of the arenas and the games.
 * <pre>
 * Bukkit.getServicesManager().load(GameService.class); // Bukkit
 * Sponge.getServiceManager().provide(GameService.class); // Sponge
 * </pre>
 */
public interface GameService {

    /**
     * Gets a player's game from his {@link UUID}.
     *
     * @param uuid The player's unique identifier.
     * @return The game, if any.
     */
    @NotNull Optional<@NotNull ThimbleGame> playerGame(@NotNull UUID uuid);

    /**
     * Gets a player's game from an {@link Identity}.
     *
     * @param identity The identified player.
     * @return The game, if any.
     */
    default @NotNull Optional<@NotNull ThimbleGame> playerGame(@NotNull Identity identity) {
        return this.playerGame(identity.uuid());
    }

    /**
     * Gets a player in game.
     *
     * @param uuid The player's unique identifier.
     * @return The player in game, if any.
     */
    @NotNull Optional<@NotNull ThimblePlayer> player(@NotNull UUID uuid);

    /**
     * Gets a player in game from an {@link Identified} player.
     *
     * @param identified The identified player.
     * @return The player in game, if any.
     */
    default @NotNull Optional<@NotNull ThimblePlayer> player(@NotNull Identified identified) {
        return this.player(identified.identity().uuid());
    }

    /**
     * Creates a new place to dive and make dés à coudre.
     *
     * @param name The pool name.
     * @return The new pool, or null if the name was already taken.
     */
    @Nullable ThimbleArena createArena(@NotNull String name);

    /**
     * Gets a {@link ThimbleArena}.
     *
     * @param name The arena name
     * @return The arena, if any.
     */
    @Contract(pure = true)
    @NotNull Optional<@NotNull ThimbleArena> arena(@NotNull String name);

    /**
     * Gets a set of the registered {@link ThimbleArena}.
     *
     * @return The arenas.
     */
    @Contract(pure = true)
    @NotNull @UnmodifiableView Set<@NotNull ThimbleArena> arenas();

    /**
     * Removes any reference to an already created arena.
     *
     * @param arena An arena.
     */
    void removeArena(@NotNull ThimbleArena arena);

    /**
     * Gets a {@link ThimbleArena} from a sign.
     *
     * @param position The sign position.
     * @return The arena, if any.
     */
    @Contract(pure = true)
    @NotNull Optional<@NotNull ThimbleArena> arenaFromSign(@NotNull BlockPos position);

    /**
     * Returns an unmodifiable set of all the signs leading to an arena.
     *
     * @param arena The arena.
     * @return All the signs.
     */
    @Contract(pure = true)
    @NotNull @UnmodifiableView Set<@NotNull BlockPos> arenaSigns(@NotNull ThimbleArena arena);

    /**
     * Returns an unmodifiable set of all signs with an action, e.g. choosing a block.
     *
     * @return All the signs.
     */
    @Contract(pure = true)
    @NotNull @UnmodifiableView Set<@NotNull BlockPos> actionSigns();
}
