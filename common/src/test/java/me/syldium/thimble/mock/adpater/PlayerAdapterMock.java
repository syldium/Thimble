package me.syldium.thimble.mock.adpater;

import me.syldium.thimble.PluginMock;
import me.syldium.thimble.common.world.BlockData;
import me.syldium.thimble.mock.player.PlayerMock;
import me.syldium.thimble.mock.util.BlockDataMock;
import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.util.BlockVector;
import me.syldium.thimble.common.adapter.PlayerAdapter;
import me.syldium.thimble.common.player.InGamePlayer;
import me.syldium.thimble.common.player.Player;
import me.syldium.thimble.common.world.PoolBlock;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerAdapterMock implements PlayerAdapter<PlayerMock, Location> {

    private final List<BlockDataMock> blocks = List.of(BlockDataMock.ONE, BlockDataMock.TWO, BlockDataMock.THREE);
    private final PluginMock plugin;

    public PlayerAdapterMock(@NotNull PluginMock plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isDeCoudre(@NotNull PoolBlock abstracted) {
        return false;
    }

    @Override
    public @NotNull List<BlockDataMock> getAvailableBlocks() {
        return this.blocks;
    }

    @Override
    public void clearPool(@NotNull UUID worldUUID, @NotNull Map<BlockVector, BlockData> blocks) {
        this.plugin.getWorld().clear();
    }

    @Override
    public @NotNull Set<@NotNull BlockVector> getRemainingWaterBlocks(@NotNull UUID worldUUID, @NotNull BlockVector minimumPoint, @NotNull BlockVector maximumPoint) {
        return Collections.emptySet();
    }

    @Override
    public @NotNull Player asAbstractPlayer(@NotNull PlayerMock player) {
        return player;
    }

    @Override
    public @NotNull Location asPlatform(@NotNull Location location) {
        return location;
    }

    @Override
    public @NotNull Location asAbstractLocation(@NotNull Location location) {
        return location;
    }

    @Override
    public void openBlockSelectionInventory(@NotNull PlayerMock player, @NotNull InGamePlayer inGamePlayer) {

    }

    @Override
    public @NotNull PlayerMock asPlatform(@NotNull Player player) {
        return (PlayerMock) player;
    }
}
