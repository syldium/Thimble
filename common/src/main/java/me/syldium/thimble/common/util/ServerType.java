package me.syldium.thimble.common.util;

import org.jetbrains.annotations.NotNull;

public enum ServerType {

    BUKKIT("Bukkit"),
    SPONGE("Sponge"),
    UNKNOWN("Unknown");

    private final String name;

    ServerType(@NotNull String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
