package me.syldium.thimble.api.arena;

import net.kyori.adventure.util.Index;
import org.jetbrains.annotations.NotNull;

/**
 * List of game modes.
 */
public enum ThimbleGameMode {

    /**
     * A {@link ThimbleSingleGame}.
     */
    SINGLE("single"),

    /**
     * A {@link ThimbleConcurrentGame}.
     */
    CONCURRENT("concurrent");

    /**
     * The name map.
     */
    public static final Index<String, ThimbleGameMode> NAMES = Index.create(ThimbleGameMode.class, mode -> mode.name);

    private final String name;

    ThimbleGameMode(@NotNull String name) {
        this.name = name;
    }
}
