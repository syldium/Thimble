package me.syldium.thimble.api.service;

import me.syldium.thimble.api.arena.ThimbleArena;
import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.player.ThimblePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface GameService {

    /**
     * Gets a player's game from his {@link UUID}.
     *
     * @param uuid The player's unique identifier.
     * @return The game, if any.
     */
    @NotNull Optional<@NotNull ThimbleGame> getGame(@NotNull UUID uuid);

    /**
     * Gets a player in game.
     *
     * @param uuid The player's unique identifier.
     * @return The player in game, if any.
     */
    @NotNull Optional<@NotNull ThimblePlayer> getInGamePlayer(@NotNull UUID uuid);

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
    @NotNull Optional<@NotNull ThimbleArena> getArena(@NotNull String name);

    /**
     * Gets a set of the registered {@link ThimbleArena}.
     *
     * @return The arenas.
     */
    @NotNull @UnmodifiableView Set<@NotNull ThimbleArena> getArenas();

    /**
     * Removes any reference to an already created arena.
     *
     * @param arena An arena.
     */
    void removeArena(@NotNull ThimbleArena arena);
}
