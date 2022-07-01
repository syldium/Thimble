package me.syldium.thimble.bukkit.adventure;

import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public final class VanillaComponentSerializer {

    public static @NotNull Object serialize(@NotNull Component component) {
        return MinecraftComponentSerializer.get().serialize(component);
    }

    public static boolean isSupported() {
        return MinecraftComponentSerializer.isSupported();
    }

    @Override
    public String toString() {
        return "MinecraftComponentSerializer";
    }
}
