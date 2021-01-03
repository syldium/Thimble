package me.syldium.thimble.mock.util;

import me.syldium.thimble.PluginMock;
import me.syldium.thimble.api.util.BlockVector;
import me.syldium.thimble.common.world.BlockData;
import me.syldium.thimble.common.world.PoolBlock;
import org.jetbrains.annotations.NotNull;

public class BlockMock implements PoolBlock {

    private final PluginMock plugin;
    private final BlockVector position;

    public BlockMock(@NotNull PluginMock plugin, BlockVector position) {
        this.plugin = plugin;
        this.position = position;
    }

    @Override
    public void setBlockData(@NotNull BlockData blockData) {
        this.plugin.getWorld().put(this.position, (BlockDataMock) blockData);
    }

    @Override
    public @NotNull BlockDataMock getBlockData() {
        BlockDataMock data = this.plugin.getBlockData(this.position);
        if (data != null) {
            return data;
        }
        return BlockDataMock.AIR;
    }

    @Override
    public @NotNull BlockVector getPosition() {
        return this.position;
    }
}
