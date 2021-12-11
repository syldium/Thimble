package me.syldium.thimble.common.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.placeholder.Placeholder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.minimessage.placeholder.Placeholder.placeholder;

public final class ComponentUtil {

    private ComponentUtil() throws IllegalAccessException {
        throw new IllegalAccessException(this.getClass().getSimpleName() + " cannot be instantiated.");
    }

    public static @NotNull Component getPluginDescriptionComponent(@NotNull String pluginName, @Nullable String pluginVersion) {
        Component component = Component.text(pluginName, NamedTextColor.DARK_GREEN);
        if (pluginVersion == null) {
            return component;
        }
        return component.hoverEvent(HoverEvent.showText(Component.text("Version: ").append(Component.text(pluginVersion, NamedTextColor.GREEN))));
    }

    public static @NotNull Placeholder getPluginDescriptionTemplate(@NotNull String pluginName, @Nullable String pluginVersion) {
        return placeholder("plugin", getPluginDescriptionComponent(pluginName, pluginVersion));
    }
}
