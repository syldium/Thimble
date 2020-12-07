package me.syldium.decoudre.util;

import me.syldium.decoudre.common.adapter.BlockBalancer;
import me.syldium.decoudre.common.player.InGamePlayer;
import me.syldium.decoudre.common.world.BlockData;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class BlockBalancerTest {

    @Test
    public void count() {
        List<InGamePlayer> list = this.newPlayerList();

        Map<BlockData, Set<InGamePlayer>> excepted = new HashMap<>();
        excepted.put(TestableBlockData.ONE, Set.of(list.get(0), list.get(5)));
        excepted.put(TestableBlockData.THREE, Set.of(list.get(1), list.get(3), list.get(4)));
        excepted.put(TestableBlockData.FOUR, Collections.singleton(list.get(2)));
        excepted.put(TestableBlockData.FIVE, Collections.singleton(list.get(6)));
        assertEquals(excepted, new BlockBalancer(list).getInitialChoices());
    }

    @Test
    public void balance() {
        List<InGamePlayer> list = this.newPlayerList();
        new BlockBalancer(list).balance(Arrays.asList(TestableBlockData.values()));
        Set<BlockData> set = new HashSet<>();
        for (InGamePlayer player : list) {
            if (!set.add(player.getChosenBlock())) {
                fail();
            }
        }
    }

    @Test
    public void balanceOverflow() {
        List<InGamePlayer> list = this.newLongPlayerList();
        new BlockBalancer(list).balance(Arrays.asList(TestableBlockData.values()));
        Map<BlockData, Integer> map = new HashMap<>();
        for (InGamePlayer player : list) {
            if (map.merge(player.getChosenBlock(), 1, Integer::sum) > 2) {
                fail();
            }
        }
    }

    private @NotNull List<@NotNull InGamePlayer> newPlayerList() {
        List<InGamePlayer> list = new LinkedList<>();
        list.add(this.newInGamePlayer(TestableBlockData.ONE));
        list.add(this.newInGamePlayer(TestableBlockData.THREE));
        list.add(this.newInGamePlayer(TestableBlockData.FOUR));
        list.add(this.newInGamePlayer(TestableBlockData.THREE));
        list.add(this.newInGamePlayer(TestableBlockData.THREE));
        list.add(this.newInGamePlayer(TestableBlockData.ONE));
        list.add(this.newInGamePlayer(TestableBlockData.FIVE));
        return list;
    }

    private @NotNull List<@NotNull InGamePlayer> newLongPlayerList() {
        List<InGamePlayer> list = this.newPlayerList();
        list.add(this.newInGamePlayer(TestableBlockData.NINE));
        list.add(this.newInGamePlayer(TestableBlockData.SEVEN));
        list.add(this.newInGamePlayer(TestableBlockData.NINE));
        list.add(this.newInGamePlayer(TestableBlockData.EIGHT));
        list.add(this.newInGamePlayer(TestableBlockData.SIX));
        return list;
    }

    private @NotNull InGamePlayer newInGamePlayer(@NotNull TestableBlockData blockData) {
        // noinspection ConstantConditions
        return new InGamePlayer(UUID.randomUUID(), "", blockData, null);
    }
}
