package me.syldium.decoudre.bukkit.util;

import me.syldium.decoudre.api.player.DePlayer;
import me.syldium.decoudre.bukkit.DeBukkitPlugin;
import me.syldium.decoudre.bukkit.world.BukkitBlockData;
import me.syldium.decoudre.common.player.InGamePlayer;
import me.syldium.decoudre.common.player.MessageKey;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static me.syldium.decoudre.bukkit.adapter.BukkitPlayerAdapter.WOOLS_TYPES;

public final class BlockSelectionInventory implements Listener {

    private static final int INVENTORY_SIZE = 9 * 3;

    private final DeBukkitPlugin plugin;
    private final String inventoryTitle;
    private final Set<UUID> inventories = new HashSet<>();

    public BlockSelectionInventory(@NotNull DeBukkitPlugin plugin) {
        this.plugin = plugin;
        this.inventoryTitle = LegacyComponentSerializer.legacySection().serialize(plugin.getMessageService().formatMessage(MessageKey.INVENTORY_BLOCK_SELECTION));
        plugin.registerEvents(this);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        UUID playerUUID = event.getWhoClicked().getUniqueId();
        if (!this.inventories.contains(playerUUID) || event.getCurrentItem() == null) return;
        event.setCancelled(true);

        Optional<DePlayer> optional = this.plugin.getGameService().getInGamePlayer(playerUUID);
        if (!optional.isPresent()) return;

        InGamePlayer player = (InGamePlayer) optional.get();
        Material previous = ((BukkitBlockData) player.getChosenBlock()).getHandle().getMaterial();
        player.setChosenBlock(new BukkitBlockData(event.getCurrentItem().getType().createBlockData()));
        for (ItemStack itemStack : event.getInventory().getContents()) {
            // noinspection ConstantConditions
            if (itemStack == null) {
                continue;
            }

            if (itemStack.getType() == previous || itemStack.equals(event.getCurrentItem())) {
                ItemMeta meta = itemStack.getItemMeta();
                if (itemStack.getType() == previous) {
                    meta.removeEnchant(Enchantment.LUCK);
                } else {
                    meta.addEnchant(Enchantment.LUCK, 1, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
                itemStack.setItemMeta(meta);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryCloseEvent event) {
        this.inventories.remove(event.getPlayer().getUniqueId());
    }

    public void open(@NotNull Player player, @NotNull InGamePlayer inGamePlayer) {
        Inventory inventory = Bukkit.createInventory(null, INVENTORY_SIZE, this.inventoryTitle);
        for (Material material : WOOLS_TYPES) {
            ItemStack itemStack = new ItemStack(material);
            if (itemStack.getType() == ((BukkitBlockData) inGamePlayer.getChosenBlock()).getHandle().getMaterial()) {
                ItemMeta meta = itemStack.getItemMeta();
                meta.addEnchant(Enchantment.LUCK, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                itemStack.setItemMeta(meta);
            }
            inventory.addItem(itemStack);
        }
        player.openInventory(inventory);
        this.inventories.add(player.getUniqueId());
    }
}
