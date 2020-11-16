package me.syldium.decoudre.common.command.abstraction;

import org.jetbrains.annotations.NotNull;

public enum Permission {

    PLAYER("decoudre.player"),
    ADMIN("decoudre.admin");

    private final String permission;

    Permission(@NotNull String permission) {
        this.permission = permission;
    }

    public @NotNull String getPermission() {
        return this.permission;
    }
}
