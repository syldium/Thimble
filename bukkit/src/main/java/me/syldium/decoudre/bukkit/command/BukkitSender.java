package me.syldium.decoudre.bukkit.command;

import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.command.abstraction.AbstractSender;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class BukkitSender extends AbstractSender<CommandSender> {

    public BukkitSender(@NotNull DeCoudrePlugin plugin, @NotNull CommandSender handle, @NotNull Audience audience) {
        super(plugin, handle, audience);
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return this.handle.hasPermission(permission);
    }
}
