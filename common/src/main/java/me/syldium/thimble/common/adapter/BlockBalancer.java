package me.syldium.thimble.common.adapter;

import me.syldium.thimble.common.player.InGamePlayer;
import me.syldium.thimble.common.world.BlockData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class BlockBalancer {

    private final Map<BlockData, Set<InGamePlayer>> initialChoices = new HashMap<>();
    private final boolean needsBalance;

    public BlockBalancer(@NotNull Iterable<? extends InGamePlayer> iterable) {
        boolean needsBalance = false;
        for (InGamePlayer player : iterable) {
            Set<InGamePlayer> set = this.initialChoices.get(player.getChosenBlock());
            if (set == null) {
                set = new HashSet<>();
                this.initialChoices.put(player.getChosenBlock(), set);
            } else {
                needsBalance = true;
            }
            set.add(player);
        }
        this.needsBalance = needsBalance;
    }

    public void balance(@NotNull Collection<? extends BlockData> allBlocks) {
        if (allBlocks.isEmpty()) {
            throw new IllegalArgumentException("The collection of blocks that can be used to balance is empty!");
        }
        if (!this.needsBalance) {
            return;
        }

        List<BlockData> available = new ArrayList<>(allBlocks.size() - this.initialChoices.size());
        for (BlockData block : allBlocks) {
            if (!this.initialChoices.containsKey(block)) {
                available.add(block);
            }
        }

        Random random = new Random();
        for (Set<InGamePlayer> players : this.initialChoices.values()) {
            // If only one player has chosen this block.
            if (players.size() < 2) {
                continue;
            }

            // One player will keep his block.
            int index = random.nextInt(players.size());
            Iterator<InGamePlayer> iter = players.iterator();
            for (int i = 0; i < index; i++) {
                this.affectsANewBlock(random, iter.next(), available, allBlocks);
            }
            iter.next();
            iter.remove();
            while (iter.hasNext()) {
                this.affectsANewBlock(random, iter.next(), available, allBlocks);
            }
        }
    }

    private void affectsANewBlock(
            @NotNull Random random,
            @NotNull InGamePlayer player,
            @NotNull List<@NotNull BlockData> available,
            @NotNull Collection<? extends BlockData> all
    ) {
        if (available.isEmpty()) {
            available.addAll(all);
        }
        player.setChosenBlock(available.remove(random.nextInt(available.size())));
    }

    public @NotNull Map<@NotNull BlockData, @NotNull Set<@NotNull InGamePlayer>> getInitialChoices() {
        return this.initialChoices;
    }
}
