package me.syldium.thimble.bukkit.command;

import me.syldium.thimble.bukkit.ThBukkitPlugin;
import me.syldium.thimble.common.command.CommandManager;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import static net.kyori.adventure.text.serializer.craftbukkit.MinecraftReflection.findField;

public class BukkitCommandExecutor extends CommandManager implements TabExecutor {

    protected final ThBukkitPlugin plugin;
    protected final PluginCommand pluginCommand;

    public BukkitCommandExecutor(@NotNull ThBukkitPlugin plugin, @NotNull PluginCommand command, @NotNull List<@NotNull String> aliases) {
        this.plugin = plugin;
        this.pluginCommand = command;
        command.setExecutor(this);
        command.setTabCompleter(this);

        if (aliases.isEmpty()) {
            return;
        }
        CommandMap commandMap = this.getCommandMap(plugin.getBootstrap().getServer());
        if (commandMap == null) {
            return;
        }

        for (String alias : aliases) {
            commandMap.register(alias, plugin.getBootstrap().getName(), command);
        }
        command.getAliases().addAll(aliases);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> arguments = new ArrayList<>(Arrays.asList(args));
        this.runCommand(this.plugin, this.plugin.getPlayerAdapter().asAbstractSender(sender), label, arguments);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return this.tabCompleteCommand(this.plugin, this.plugin.getPlayerAdapter().asAbstractSender(sender), new ArrayList<>(Arrays.asList(args)));
    }

    private @Nullable CommandMap getCommandMap(@NotNull Server server) {
        try {
            return server.getCommandMap();
        } catch (NoSuchMethodError err) {
            Field field = findField(server.getClass(), "commandMap", CommandMap.class);
            try {
                return field == null ? null : (CommandMap) field.get(server);
            } catch (IllegalAccessException ex) {
                this.plugin.getLogger().log(Level.WARNING, "Can't register aliases.", ex);
            }
        }
        return null;
    }
}
