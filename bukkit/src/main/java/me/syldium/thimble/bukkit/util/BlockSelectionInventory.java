package me.syldium.thimble.bukkit.util;

import com.google.common.base.Preconditions;
import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.bukkit.ThBukkitPlugin;
import me.syldium.thimble.bukkit.adapter.BukkitPlayerAdapter;
import me.syldium.thimble.bukkit.world.BukkitBlockData;
import me.syldium.thimble.common.player.InGamePlayer;
import me.syldium.thimble.common.player.MessageKey;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class BlockSelectionInventory implements Listener {

    private static final int INVENTORY_SIZE = 9 * 3;
    private static final int PER_PAGE = INVENTORY_SIZE - 2;
    private static final int PREV_SLOT = INVENTORY_SIZE - 9;
    private static final int NEXT_SLOT = INVENTORY_SIZE - 1;

    private final ThBukkitPlugin plugin;
    private final String inventoryTitle;
    private final String gameStarted;
    private final Map<UUID, Integer> inventories;
    private final int pages;

    public BlockSelectionInventory(@NotNull ThBukkitPlugin plugin, @NotNull BukkitPlayerAdapter adapter) {
        this.plugin = plugin;
        this.inventoryTitle = LegacyComponentSerializer.legacySection().serialize(
                plugin.getMessageService().formatMessage(MessageKey.INVENTORY_BLOCK_SELECTION)
        );
        this.gameStarted = LegacyComponentSerializer.legacySection().serialize(
                plugin.getMessageService().formatMessage(MessageKey.FEEDBACK_GAME_STARTED_GAME, NamedTextColor.RED)
        );
        this.inventories = BukkitUtil.newObject2IntMap();
        this.pages = (int) Math.ceil((float) adapter.getAvailableBlocks().size() / PER_PAGE);
        plugin.registerEvents(this);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Checks if it's an inventory created by this class.
        UUID playerUUID = event.getWhoClicked().getUniqueId();
        Integer page = this.inventories.get(playerUUID);
        if (page == null || event.getCurrentItem() == null) return;
        event.setCancelled(true);

        // Gets the player back
        Optional<ThimblePlayer> optional = this.plugin.getGameService().player(playerUUID);
        if (!optional.isPresent()) return;
        InGamePlayer player = (InGamePlayer) optional.get();

        // Makes the pagination
        if (event.getRawSlot() == PREV_SLOT) {
            if (page > 1) {
                this.open(event.getWhoClicked(), player, page - 1);
            }
            return;
        }
        if (event.getRawSlot() == NEXT_SLOT) {
            if (page < this.pages) {
                this.open(event.getWhoClicked(), player, page + 1);
            }
            return;
        }

        // Changes the player's block
        if (!player.getGame().state().isNotStarted()) {
            event.getWhoClicked().sendMessage(this.gameStarted);
            return;
        }
        BukkitBlockData previous = (BukkitBlockData) player.getChosenBlock();
        player.setChosenBlock(BukkitBlockData.build(event.getCurrentItem()));
        for (ItemStack itemStack : event.getInventory().getContents()) {
            if (itemStack == null) {
                continue;
            }

            if (previous.isSimilar(itemStack) || itemStack.equals(event.getCurrentItem())) {
                ItemMeta meta = itemStack.getItemMeta();
                if (previous.isSimilar(itemStack)) {
                    meta.setDisplayName(null);
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
    public void onInventoryClose(InventoryCloseEvent event) {
        this.inventories.remove(event.getPlayer().getUniqueId());
    }

    public void open(@NotNull HumanEntity human, @NotNull InGamePlayer inGamePlayer) {
        this.open(human, inGamePlayer, 1);
    }

    public void open(@NotNull HumanEntity human, @NotNull InGamePlayer inGamePlayer, int page) {
        Preconditions.checkArgument(page > 0 && page <= this.pages, "The page number is out of bounds.");

        Inventory inventory = Bukkit.createInventory(null, INVENTORY_SIZE, this.inventoryTitle);
        List<BukkitBlockData> wools = this.plugin.getPlayerAdapter().getAvailableBlocks();
        int start = (page - 1) * PER_PAGE;
        int end = Math.min(page * PER_PAGE, wools.size());

        int index = 0;
        for (BukkitBlockData blockData : wools.subList(start, end)) {
            ItemStack itemStack = blockData.itemStack();

            Set<InGamePlayer> players = inGamePlayer.getGame().getPlayers(blockData);
            String displayName = players.isEmpty() ?
                    null
                    : ChatColor.AQUA + players.stream().map(InGamePlayer::name).collect(Collectors.joining(", "));

            BukkitBlockData chosenBlockData = (BukkitBlockData) inGamePlayer.getChosenBlock();
            if (blockData.equals(chosenBlockData) || !players.isEmpty() && inGamePlayer.getGame().state().isStarted()) {
                // Adds an enchantment glint
                ItemMeta meta = itemStack.getItemMeta();
                meta.setDisplayName(displayName);
                meta.addEnchant(Enchantment.LUCK, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                itemStack.setItemMeta(meta);
            } else if (!players.isEmpty()) {
                // Adds player names
                ItemMeta meta = itemStack.getItemMeta();
                meta.setDisplayName(displayName);
                itemStack.setItemMeta(meta);
            }

            if (index == PREV_SLOT) {
                index++;
            }
            inventory.setItem(index++, itemStack);
        }

        if (page > 1) {
            inventory.setItem(PREV_SLOT, new ItemStack(Material.ARROW));
        }
        if (page < this.pages) {
            inventory.setItem(NEXT_SLOT, new ItemStack(Material.ARROW));
        }

        human.openInventory(inventory);
        this.inventories.put(human.getUniqueId(), page);
    }
}
