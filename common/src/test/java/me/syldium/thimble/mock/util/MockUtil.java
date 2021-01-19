package me.syldium.thimble.mock.util;

import me.syldium.thimble.api.util.WorldKey;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class MockUtil {

    private MockUtil() throws IllegalAccessException {
        throw new IllegalAccessException(this.getClass().getSimpleName() + " cannot be instantiated.");
    }

    public static @NotNull WorldKey randomKey() {
        return new WorldKey(UUID.randomUUID().toString().substring(0, 16));
    }
}
