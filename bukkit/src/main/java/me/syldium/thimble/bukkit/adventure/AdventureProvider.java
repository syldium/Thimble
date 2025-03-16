package me.syldium.thimble.bukkit.adventure;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AdventureProvider {

    public @NotNull Audience sender(@NotNull CommandSender sender) {
        return sender;
    }

    public @NotNull Audience player(@NotNull Player player) {
        return player;
    }

    public @NotNull Inventory creatingInventory(@Nullable InventoryHolder owner, int size, @NotNull Component title) {
        return Bukkit.createInventory(owner, size, title);
    }

    public static @NotNull LegacyComponentSerializer legacy() {
        return LegacyComponentSerializer.legacySection();
    }

    public static @NotNull Sound.Emitter asEmitter(@NotNull Entity entity) {
        return entity;
    }

    public void close() {
        // no op on paper
    }

    @Override
    public String toString() {
        return "native";
    }
}
