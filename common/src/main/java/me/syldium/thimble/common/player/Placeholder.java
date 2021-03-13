package me.syldium.thimble.common.player;

import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.common.util.PlaceholderUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;

public enum Placeholder {

    ARENA(player -> player.game().arena().name()),
    JUMPER(PlaceholderUtil::currentJumper),
    JUMPS(ThimblePlayer::jumpsForGame),
    POINTS(ThimblePlayer::points),
    STATE(player -> player.game().state());

    private final ValueReplacer function;
    private final String key;

    Placeholder(@NotNull Function<@NotNull ThimblePlayer, @NotNull Object> function) {
        this((player, usageIndex) -> function.apply(player));
    }

    Placeholder(@NotNull ValueReplacer replacer) {
        this.function = replacer;
        this.key = this.name().toLowerCase(Locale.ROOT);
    }

    public @NotNull String apply(@NotNull Function<UUID, String> uuidToString, @NotNull ThimblePlayer player, int usageIndex) {
        Object value = this.function.apply(player, usageIndex);
        if (value instanceof UUID) {
            return uuidToString.apply((UUID) value);
        }
        return String.valueOf(value);
    }

    public @NotNull String asString() {
        return this.key;
    }

    @FunctionalInterface
    interface ValueReplacer {

        @NotNull Object apply(@NotNull ThimblePlayer player, int usageIndex);
    }
}
