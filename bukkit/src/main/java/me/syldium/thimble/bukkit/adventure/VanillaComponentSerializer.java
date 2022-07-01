package me.syldium.thimble.bukkit.adventure;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

public final class VanillaComponentSerializer {

    private static MethodHandle AS_VANILLA;
    private static Throwable SETUP_EXCEPTION;

    static {
        try {
            final MethodHandles.Lookup lookup = MethodHandles.publicLookup();
            final Class<?> paperAdventure = Class.forName("io.papermc.paper.adventure.PaperAdventure");
            final Method asVanilla = paperAdventure.getMethod("asVanilla", Component.class);
            AS_VANILLA = lookup.unreflect(asVanilla);
        } catch (Throwable throwable) {
            SETUP_EXCEPTION = throwable;
        }
    }

    public static @NotNull Object serialize(@NotNull Component component) {
        try {
            return AS_VANILLA.invoke(component);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public static boolean isSupported() {
        return SETUP_EXCEPTION == null;
    }

    @Override
    public String toString() {
        return "PaperComponentSerializer";
    }
}
