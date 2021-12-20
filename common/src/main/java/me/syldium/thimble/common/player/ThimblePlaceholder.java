package me.syldium.thimble.common.player;

import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.common.config.NodePath;
import me.syldium.thimble.common.util.PlaceholderUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;

public enum ThimblePlaceholder {

    ARENA(player -> player.game().arena().name()),
    JUMPER(PlaceholderUtil::currentJumper),
    JUMPS(ThimblePlayer::jumpsForGame),
    NEXT_JUMPER(PlaceholderUtil::nextJumper),
    POINTS(ThimblePlayer::points),
    STATE(player -> player.game().state()),
    THIMBLE(ThimblePlayer::thimbleForGame);

    public static final char TAG_START = '<';
    public static final char TAG_END = '>';

    private final ValueFunction function;
    private final String key;

    ThimblePlaceholder(@NotNull Function<@NotNull ThimblePlayer, @NotNull Object> function) {
        this((player, usageIndex) -> function.apply(player));
    }

    ThimblePlaceholder(@NotNull ValueFunction function) {
        this.function = function;
        this.key = this.name().toLowerCase(Locale.ROOT);
    }

    public @Nullable Object apply(@NotNull Function<UUID, String> uuidToString, @NotNull ThimblePlayer player, int usageIndex) {
        Object value = this.function.apply(player, usageIndex);
        if (value instanceof UUID) {
            return uuidToString.apply((UUID) value);
        }
        return value;
    }

    public @NotNull @NodePath String asString() {
        return this.key;
    }

    @FunctionalInterface
    interface ValueFunction {

        @Nullable Object apply(@NotNull ThimblePlayer player, int usageIndex);
    }
}
