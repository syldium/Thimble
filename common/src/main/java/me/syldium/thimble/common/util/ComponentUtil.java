package me.syldium.thimble.common.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.component;

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

    public static @NotNull TagResolver getPluginDescriptionTemplate(@NotNull String pluginName, @Nullable String pluginVersion) {
        return component("plugin", getPluginDescriptionComponent(pluginName, pluginVersion));
    }
}
