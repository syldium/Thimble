package me.syldium.decoudre.sponge.util;

import me.syldium.decoudre.api.player.DePlayer;
import me.syldium.decoudre.common.player.InGamePlayer;
import me.syldium.decoudre.common.player.MessageKey;
import me.syldium.decoudre.sponge.DeSpongePlugin;
import me.syldium.decoudre.sponge.world.SpongeBlockData;
import net.kyori.adventure.text.serializer.spongeapi.SpongeComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.property.AcceptsItems;
import org.spongepowered.api.item.inventory.property.InventoryTitle;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class BlockSelectionInventory {

    private final DeSpongePlugin plugin;
    private final Set<UUID> inventories = new HashSet<>();
    private final InventoryTitle inventoryTitle;
    private final List<Enchantment> enchantements;

    public BlockSelectionInventory(@NotNull DeSpongePlugin plugin) {
        this.plugin = plugin;
        this.inventoryTitle = InventoryTitle.of(
                SpongeComponentSerializer.get().serialize(
                        plugin.getMessageService().formatMessage(MessageKey.INVENTORY_BLOCK_SELECTION)
                )
        );
        this.enchantements = Collections.singletonList(
                Enchantment.builder()
                        .type(EnchantmentTypes.LUCK_OF_THE_SEA)
                        .level(1)
                        .build()
        );

        plugin.registerListeners(this);
    }

    public void open(@NotNull Player player, @NotNull InGamePlayer inGamePlayer) {
        Inventory inventory = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .property(InventoryTitle.PROPERTY_NAME, this.inventoryTitle)
                .property(new AcceptsItems(Collections.emptyList()))
                .build(this.plugin);

        BlockState blockState = ((SpongeBlockData) inGamePlayer.getChosenBlock()).getHandle();

        for (BlockState state : this.plugin.getRegistry().getAllOf(BlockState.class)) {
            if (state.getType().equals(BlockTypes.WOOL)) {
                ItemStack itemStack = ItemStack.builder().fromBlockState(state).build();
                state.get(Keys.DYE_COLOR).ifPresent(dyeColor -> itemStack.offer(Keys.DYE_COLOR, dyeColor));

                if (state.equals(blockState)) {
                    itemStack.offer(Keys.ITEM_ENCHANTMENTS, this.enchantements);
                    itemStack.offer(Keys.HIDE_ENCHANTMENTS, true);
                }

                inventory.offer(itemStack);
            }
        }
        player.openInventory(inventory);
        this.inventories.add(player.getUniqueId());
    }

    @Listener
    public void onClick(ClickInventoryEvent.Primary event, @First Player player) {
        UUID playerUUID = player.getUniqueId();
        if (!this.inventories.contains(playerUUID) || !event.getSlot().isPresent()) return;

        Container inventory = event.getTargetInventory();

        Optional<DePlayer> optional = this.plugin.getGameService().getInGamePlayer(playerUUID);
        if (!optional.isPresent()) return;

        event.getCursorTransaction().setValid(false);

        InGamePlayer inGamePlayer = (InGamePlayer) optional.get();
        ItemStackSnapshot clickedItem = event.getCursorTransaction().getFinal();
        BlockState.Builder builder = BlockState.builder().blockType(BlockTypes.WOOL);
        clickedItem.get(Keys.DYE_COLOR).ifPresent(dyeColor -> builder.add(Keys.DYE_COLOR, dyeColor));
        inGamePlayer.setChosenBlock(new SpongeBlockData(builder.build()));

        ItemStack stack = clickedItem.createStack();
        stack.offer(Keys.ITEM_ENCHANTMENTS, this.enchantements);
        stack.offer(Keys.HIDE_ENCHANTMENTS, true);
        for (Inventory slot : inventory.slots()) {
            slot.peek().ifPresent(itemStack -> {
                itemStack.remove(Keys.ITEM_ENCHANTMENTS);
                slot.set(itemStack);
            });
        }
        inventory.offer(stack);
    }

    @Listener
    public void onClose(InteractInventoryEvent.Close event, @First Player player) {
        this.inventories.remove(player.getUniqueId());
    }

}
