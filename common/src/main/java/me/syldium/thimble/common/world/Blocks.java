package me.syldium.thimble.common.world;

import org.jetbrains.annotations.NotNull;

public final class Blocks implements BlockData {

    public static final BlockData WATER = new Blocks("water");

    private final String name;

    Blocks(@NotNull String name) {
        this.name = name;
    }
}
