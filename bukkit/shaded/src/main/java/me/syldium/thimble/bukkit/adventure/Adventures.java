package me.syldium.thimble.bukkit.adventure;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Adventures {

    private Adventures() {

    }

    public static @Nullable AdventureProvider create(@NotNull Plugin plugin) {
        return new AdventureProvider(plugin);
    }
}
