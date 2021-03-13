package me.syldium.thimble.bukkit.util;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static java.lang.invoke.MethodType.methodType;
import static net.kyori.adventure.text.serializer.craftbukkit.MinecraftReflection.findCraftClass;
import static net.kyori.adventure.text.serializer.craftbukkit.MinecraftReflection.findNmsClass;
import static net.kyori.adventure.text.serializer.craftbukkit.MinecraftReflection.lookup;

public final class PacketUtil {

    private static final MethodHandle CRAFT_PLAYER_GET_HANDLE;
    private static final MethodHandle ENTITY_PLAYER_GET_CONNECTION;
    private static final MethodHandle PLAYER_CONNECTION_SEND_PACKET;
    private static final boolean SUPPORTED;

    static {
        Class<?> craftPlayerClass = findCraftClass("entity.CraftPlayer", Player.class);
        Class<?> packetClass = findNmsClass("Packet");

        MethodHandle craftPlayerGetHandle = null;
        MethodHandle entityPlayerGetConnection = null;
        MethodHandle playerConnectionSendPacket = null;
        if (craftPlayerClass != null && packetClass != null) {
            try {
                Method getHandleMethod = craftPlayerClass.getMethod("getHandle");
                Class<?> entityPlayerClass = getHandleMethod.getReturnType();
                craftPlayerGetHandle = lookup().unreflect(getHandleMethod);
                Field playerConnectionField = entityPlayerClass.getField("playerConnection");
                entityPlayerGetConnection = lookup().unreflectGetter(playerConnectionField);
                Class<?> playerConnectionClass = playerConnectionField.getType();
                playerConnectionSendPacket = lookup().findVirtual(playerConnectionClass, "sendPacket", methodType(void.class, packetClass));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        CRAFT_PLAYER_GET_HANDLE = craftPlayerGetHandle;
        ENTITY_PLAYER_GET_CONNECTION = entityPlayerGetConnection;
        PLAYER_CONNECTION_SEND_PACKET = playerConnectionSendPacket;
        SUPPORTED = craftPlayerGetHandle != null && entityPlayerGetConnection != null && playerConnectionSendPacket != null;
    }

    private PacketUtil() throws IllegalAccessException {
        throw new IllegalAccessException(this.getClass().getSimpleName() + " cannot be instantiated.");
    }

    public static @Nullable Object getPlayerConnection(@NotNull Player player) {
        try {
            return ENTITY_PLAYER_GET_CONNECTION.invoke(CRAFT_PLAYER_GET_HANDLE.invoke(player));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    public static void sendPacket(@NotNull Player player, @Nullable Object packet) {
        try {
            sendPacket(getPlayerConnection(player), packet);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static void sendPacket(@Nullable Object playerConnection, @Nullable Object packet) {
        if (packet == null) return;

        try {
            PLAYER_CONNECTION_SEND_PACKET.invoke(playerConnection, packet);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static boolean isSupported() {
        return SUPPORTED;
    }
}
