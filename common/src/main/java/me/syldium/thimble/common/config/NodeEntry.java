package me.syldium.thimble.common.config;

import org.jetbrains.annotations.NotNull;

public final class NodeEntry {

    private final String key;
    private final ConfigNode node;

    public NodeEntry(@NotNull @NodePath String key, @NotNull ConfigNode node) {
        this.key = key;
        this.node = node;
    }

    public @NotNull @NodePath String key() {
        return this.key;
    }

    public @NotNull ConfigNode node() {
        return this.node;
    }
}
