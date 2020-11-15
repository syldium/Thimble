package me.syldium.decoudre.api.service;

import me.syldium.decoudre.api.arena.DeArena;
import me.syldium.decoudre.api.arena.DeGame;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface GameService {

    /**
     * Get a player's game from his {@link UUID}.
     *
     * @param uuid The player's unique identifier
     * @return The game, if any
     */
    @NotNull Optional<@NotNull DeGame> getGame(@NotNull UUID uuid);

    /**
     * Create a new place to dive and make dés à coudre.
     *
     * @param name The pool name
     * @return The new pool, or null if the name was already taken
     */
    @Nullable DeArena createArena(@NotNull String name);

    /**
     * Get a swimming {@link DeArena}.
     *
     * @param name The arena name
     * @return The arena, if any
     */
    @NotNull Optional<@NotNull DeArena> getArena(@NotNull String name);

    /**
     * Get a set of the registered {@link DeArena}.
     *
     * @return The arenas
     */
    @NotNull @UnmodifiableView Set<@NotNull DeArena> getArenas();
}
