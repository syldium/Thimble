package me.syldium.thimble.mock.util;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class MockUtil {

    private MockUtil() throws IllegalAccessException {
        throw new IllegalAccessException(this.getClass().getSimpleName() + " cannot be instantiated.");
    }

    public static @NotNull Key randomKey() {
        return Key.key(UUID.randomUUID().toString().substring(0, 16));
    }
}
