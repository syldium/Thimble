package me.syldium.thimble.common.command.abstraction;

import me.syldium.thimble.common.player.MessageKey;
import org.jetbrains.annotations.NotNull;

public final class Permission {

    public static @NotNull Permission arenaSetup(@NotNull String property) {
        return new Permission("thimble.commands.arena." + property);
    }

    public static @NotNull Permission arenaSetup() {
        return new Permission("thimble.commands.arena");
    }

    public static @NotNull Permission migrate() {
        return new Permission("thimble.commands.migrate");
    }

    public static @NotNull Permission player(@NotNull String property) {
        return new Permission("thimble.commands.player." + property);
    }

    public static @NotNull Permission stats(@NotNull String property) {
        return new Permission("thimble.commands.stats." + property);
    }

    public static @NotNull Permission stats() {
        return new Permission("thimble.commands.stats");
    }

    public static @NotNull Permission version() {
        return new Permission("thimble.commands.version");
    }

    public static @NotNull Permission version(@NotNull String property) {
        return new Permission("thimble.commands.version." + property);
    }

    private final String permission;

    Permission(@NotNull String permission) {
        this.permission = permission;
    }

    public void verify(@NotNull Sender sender) {
        if (!sender.hasPermission(this.permission)) {
            throw new CommandException(MessageKey.FEEDBACK_UNKNOWN_COMMAND);
        }
    }

    public @NotNull String get() {
        return this.permission;
    }
}
