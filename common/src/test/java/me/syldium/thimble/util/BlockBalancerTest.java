package me.syldium.thimble.util;

import me.syldium.thimble.common.adapter.BlockBalancer;
import me.syldium.thimble.common.player.InGamePlayer;
import me.syldium.thimble.common.world.BlockData;
import me.syldium.thimble.mock.util.BlockDataMock;
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
        excepted.put(BlockDataMock.ONE, Set.of(list.get(0), list.get(5)));
        excepted.put(BlockDataMock.THREE, Set.of(list.get(1), list.get(3), list.get(4)));
        excepted.put(BlockDataMock.FOUR, Collections.singleton(list.get(2)));
        excepted.put(BlockDataMock.FIVE, Collections.singleton(list.get(6)));
        assertEquals(excepted, new BlockBalancer(list).getInitialChoices());
    }

    @Test
    public void balance() {
        List<InGamePlayer> list = this.newPlayerList();
        new BlockBalancer(list).balance(Arrays.asList(BlockDataMock.values()));
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
        new BlockBalancer(list).balance(Arrays.asList(BlockDataMock.values()));
        Map<BlockData, Integer> map = new HashMap<>();
        for (InGamePlayer player : list) {
            if (map.merge(player.getChosenBlock(), 1, Integer::sum) > 2) {
                fail();
            }
        }
    }

    private @NotNull List<@NotNull InGamePlayer> newPlayerList() {
        List<InGamePlayer> list = new LinkedList<>();
        list.add(this.newInGamePlayer(BlockDataMock.ONE));
        list.add(this.newInGamePlayer(BlockDataMock.THREE));
        list.add(this.newInGamePlayer(BlockDataMock.FOUR));
        list.add(this.newInGamePlayer(BlockDataMock.THREE));
        list.add(this.newInGamePlayer(BlockDataMock.THREE));
        list.add(this.newInGamePlayer(BlockDataMock.ONE));
        list.add(this.newInGamePlayer(BlockDataMock.FIVE));
        return list;
    }

    private @NotNull List<@NotNull InGamePlayer> newLongPlayerList() {
        List<InGamePlayer> list = this.newPlayerList();
        list.add(this.newInGamePlayer(BlockDataMock.TWO));
        list.add(this.newInGamePlayer(BlockDataMock.SEVEN));
        list.add(this.newInGamePlayer(BlockDataMock.TWO));
        list.add(this.newInGamePlayer(BlockDataMock.FIVE));
        list.add(this.newInGamePlayer(BlockDataMock.SIX));
        return list;
    }

    private @NotNull InGamePlayer newInGamePlayer(@NotNull BlockDataMock blockData) {
        // noinspection ConstantConditions
        return new InGamePlayer(UUID.randomUUID(), "", blockData, null);
    }
}
