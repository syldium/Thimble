package me.syldium.thimble.bukkit.adventure;

import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

import static me.syldium.thimble.common.ThimblePlugin.classExists;

public final class Adventures {

    private static final String AUDIENCE_NAME = "net.kyo".concat("ri.adventure.audience.Audience");
    private static final String MINI_MESSAGE_NAME = "net.kyo".concat("ri.adventure.text.minimessage.MiniMessage");

    private Adventures() {

    }

    public static @Nullable AdventureProvider create(@NotNull Plugin plugin) {
        if (!isSupported()) {
            Logger log = plugin.getLogger();
            log.severe("This version targets Paper-based servers starting from Minecraft 1.18.2.");
            log.severe("This server does not seem to meet these requirements, so the plugin will not work.");
            log.severe("For Bukkit/Spigot servers or versions lower than 1.18.2, please use the 'Bukkit' specific build and not the 'Paper' build.");
            return null;
        }
        return new AdventureProvider();
    }

    public static boolean isSupported() {
        return classExists(AUDIENCE_NAME) // If the server bundles Adventure,
                && classExists(MINI_MESSAGE_NAME) // MiniMessage...
                && Audience.class.isAssignableFrom(CommandSender.class); // and Audience is available
    }
}
