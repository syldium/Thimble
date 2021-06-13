package me.syldium.thimble.bukkit.util;

import org.jetbrains.annotations.Nullable;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;

import static net.kyori.adventure.text.serializer.craftbukkit.MinecraftReflection.findConstructor;

@FunctionalInterface
interface PacketInvoker {

    Object invoke() throws Throwable;

    static @Nullable PacketInvoker find(@Nullable Class<?> packetClass) {
        if (packetClass == null) return null;
        MethodHandle constructor = findConstructor(packetClass);
        if (constructor != null) return constructor::invoke;
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe unsafe = (Unsafe) theUnsafe.get(null);
            return () -> unsafe.allocateInstance(packetClass);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            return null;
        }
    }
}
