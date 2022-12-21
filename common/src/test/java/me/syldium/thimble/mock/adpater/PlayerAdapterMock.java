package me.syldium.thimble.mock.adpater;

import me.syldium.thimble.PluginMock;
import me.syldium.thimble.api.util.WorldKey;
import me.syldium.thimble.common.player.media.Scoreboard;
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
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerAdapterMock implements PlayerAdapter<PlayerMock, Location> {

    private final List<BlockDataMock> blocks = List.of(BlockDataMock.ONE, BlockDataMock.TWO, BlockDataMock.THREE);
    private final Map<UUID, PlayerMock> players = new HashMap<>();
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
    public @Nullable BlockData getThimbleBlock() {
        return null;
    }

    @Override
    public void clearPool(@NotNull WorldKey worldKey, @NotNull Map<BlockVector, BlockData> blocks) {
        this.plugin.getWorld().clear();
    }

    @Override
    public @NotNull Set<@NotNull BlockVector> getRemainingWaterBlocks(@NotNull WorldKey worldKey, @NotNull BlockVector minimumPoint, @NotNull BlockVector maximumPoint) {
        return Collections.emptySet();
    }

    @Override
    public @Nullable PlayerMock getPlayer(@NotNull UUID uuid) {
        return this.players.get(uuid);
    }

    public @NotNull PlayerMock addPlayer() {
        UUID uuid = UUID.randomUUID();
        PlayerMock player = new PlayerMock(this.plugin, uuid.toString().substring(0, 16), uuid);
        this.players.put(uuid, player);
        return player;
    }

    public void removeAllPlayers() {
        this.players.clear();
    }

    public @NotNull Collection<@NotNull PlayerMock> getPlayers() {
        return this.players.values();
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
    public void setScoreboard(@Nullable Scoreboard scoreboard, @NotNull Player player) {

    }

    @Override
    public void hideScoreboard(@NotNull Scoreboard scoreboard, @NotNull Player player) {

    }

    @Override
    public @NotNull PlayerMock asPlatform(@NotNull Player player) {
        return (PlayerMock) player;
    }
}
