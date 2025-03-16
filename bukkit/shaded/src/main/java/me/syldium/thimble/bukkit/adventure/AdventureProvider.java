package me.syldium.thimble.bukkit.adventure;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AdventureProvider {

    private final BukkitAudiences audiences;

    public AdventureProvider(@NotNull Plugin plugin) {
        this.audiences = BukkitAudiences.create(plugin);
    }

    public @NotNull Audience sender(@NotNull CommandSender sender) {
        return this.audiences.sender(sender);
    }

    public @NotNull Audience player(@NotNull Player player) {
        return this.audiences.player(player);
    }

    public @NotNull Inventory creatingInventory(@Nullable InventoryHolder owner, int size, @NotNull Component title) {
        return Bukkit.createInventory(owner, size, legacy().serialize(title));
    }

    public static @NotNull LegacyComponentSerializer legacy() {
        return BukkitComponentSerializer.legacy();
    }

    public static @NotNull Sound.Emitter asEmitter(@NotNull Entity entity) {
        return BukkitAudiences.asEmitter(entity);
    }

    public void close() {
        this.audiences.close();
    }

    @Override
    public String toString() {
        return "shaded";
    }
}
