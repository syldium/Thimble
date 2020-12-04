package me.syldium.decoudre.common.command.abstraction;

import org.jetbrains.annotations.NotNull;

public final class Permission {

    private static final Permission ARENA_SETUP = new Permission("decoudre.arena");

    public static @NotNull Permission arenaSetup(@NotNull String property) {
        return new Permission("decoudre.arena." + property);
    }

    public static @NotNull Permission arenaSetup() {
        return ARENA_SETUP;
    }

    public static @NotNull Permission player(@NotNull String property) {
        return new Permission("decoudre.player." + property);
    }

    private final String permission;

    Permission(@NotNull String permission) {
        this.permission = permission;
    }

    public @NotNull String getPermission() {
        return this.permission;
    }
}
