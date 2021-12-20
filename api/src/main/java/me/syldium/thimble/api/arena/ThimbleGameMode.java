package me.syldium.thimble.api.arena;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.util.Index;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * List of game modes.
 */
public enum ThimbleGameMode implements ComponentLike {

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

    @Override
    public @NotNull Component asComponent() {
        return Component.text(this.name().toLowerCase(Locale.ROOT));
    }
}
