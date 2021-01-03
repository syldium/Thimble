package me.syldium.thimble.common.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.Template;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public static @NotNull Template getPluginDescriptionTemplate(@NotNull String pluginName, @Nullable String pluginVersion) {
        return Template.of("plugin", getPluginDescriptionComponent(pluginName, pluginVersion));
    }
}
