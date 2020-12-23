package me.syldium.thimble.common.service;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@FunctionalInterface
public interface VanishService {

    VanishService DUMMY_SERVICE = uuid -> false;

    /**
     * Checks if a player is invisible.
     *
     * @param uuid The player's unique identifier.
     * @return {@code true} if the player is invisible, {@code false} otherwise.
     */
    boolean isVanished(@NotNull UUID uuid);
}
