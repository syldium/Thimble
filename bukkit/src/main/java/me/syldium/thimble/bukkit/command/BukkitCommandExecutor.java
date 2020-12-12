package me.syldium.thimble.bukkit.command;

import me.syldium.thimble.bukkit.ThBukkitPlugin;
import me.syldium.thimble.common.command.CommandManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BukkitCommandExecutor extends CommandManager implements TabExecutor {

    protected final ThBukkitPlugin plugin;
    protected final PluginCommand pluginCommand;

    public BukkitCommandExecutor(@NotNull ThBukkitPlugin plugin, @NotNull PluginCommand command) {
        this.plugin = plugin;
        this.pluginCommand = command;
        command.setExecutor(this);
        command.setTabCompleter(this);
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
}
