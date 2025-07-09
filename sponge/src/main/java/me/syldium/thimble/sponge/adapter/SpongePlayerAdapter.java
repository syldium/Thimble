package me.syldium.thimble.sponge.adapter;

import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.util.BlockVector;
import me.syldium.thimble.api.util.WorldKey;
import me.syldium.thimble.common.adapter.PlayerAdapter;
import me.syldium.thimble.common.command.abstraction.Sender;
import me.syldium.thimble.common.player.InGamePlayer;
import me.syldium.thimble.common.player.Player;
import me.syldium.thimble.common.player.media.Scoreboard;
import me.syldium.thimble.common.world.BlockData;
import me.syldium.thimble.common.world.PoolBlock;
import me.syldium.thimble.sponge.util.SpongeAdapter;
import me.syldium.thimble.sponge.ThSpongePlugin;
import me.syldium.thimble.sponge.world.SpongeBlockData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.tag.BlockTypeTags;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class SpongePlayerAdapter implements PlayerAdapter<ServerPlayer, ServerLocation> {

    private final ThSpongePlugin plugin;
    private final Map<UUID, SpongeServerPlayer> players = new HashMap<>();
    private final SpongeAdapter locationAdapter;
    private final List<SpongeBlockData> blocks = new ArrayList<>();

    public SpongePlayerAdapter(@NotNull ThSpongePlugin plugin) {
        this.plugin = plugin;
        this.locationAdapter = new SpongeAdapter(plugin);
        final Iterable<BlockType> tagged = plugin.game().registry(RegistryTypes.BLOCK_TYPE).taggedValues(BlockTypeTags.WOOL)::iterator;
        for (BlockType blockType : tagged) {
            this.blocks.add(new SpongeBlockData(blockType.defaultState()));
        }
    }

    @Override
    public boolean isDeCoudre(@NotNull PoolBlock abstracted) {
        return false;
    }

    @Override
    public @NotNull List<SpongeBlockData> getAvailableBlocks() {
        return this.blocks;
    }

    @Override
    public @Nullable BlockData getThimbleBlock() {
        return null;
    }

    @Override
    public void clearPool(@NotNull WorldKey worldKey, @NotNull Map<BlockVector, BlockData> blocks) {

    }

    @Override
    public @NotNull Set<@NotNull BlockVector> getRemainingWaterBlocks(@NotNull WorldKey worldKey, @NotNull BlockVector minimumPoint, @NotNull BlockVector maximumPoint) {
        return Collections.emptySet();
    }

    @Override
    public @Nullable SpongeServerPlayer getPlayer(@NotNull UUID uuid) {
        SpongeServerPlayer p = this.players.get(uuid);
        if (p != null) {
            return p;
        }

        Optional<ServerPlayer> player = this.plugin.server().player(uuid);
        if (player.isEmpty()) {
            return null;
        }
        p = new SpongeServerPlayer(this.plugin, player.get());
        this.players.put(p.uuid(), p);
        return p;
    }

    @Override
    public @Nullable Player getPlayer(@NotNull String name) {
        return null;
    }

    @Override
    public @NotNull SpongeServerPlayer asAbstractPlayer(@NotNull ServerPlayer player) {
        SpongeServerPlayer p = this.players.get(player.uniqueId());
        if (p != null) {
            return p;
        }
        p = new SpongeServerPlayer(this.plugin, player);
        this.players.put(player.uniqueId(), p);
        return p;
    }

    @Override
    public @NotNull ServerLocation asPlatform(@NotNull Location location) {
        return this.locationAdapter.asSponge(location);
    }

    @Override
    public @NotNull Location asAbstractLocation(@NotNull ServerLocation location) {
        return this.locationAdapter.asAbstract(location);
    }

    public @NotNull Location asAbstractLocation(@NotNull ServerLocation location, @NotNull Vector3d headRotation) {
        return this.locationAdapter.asAbstract(location, headRotation);
    }

    @Override
    public void openBlockSelectionInventory(@NotNull ServerPlayer player, @NotNull InGamePlayer inGamePlayer) {

    }

    @Override
    public void setScoreboard(@Nullable Scoreboard scoreboard, @NotNull Player player) {

    }

    @Override
    public void hideScoreboard(@NotNull Scoreboard scoreboard, @NotNull Player player) {

    }

    public @NotNull Sender asAbstractSender(@NotNull CommandCause cause) {
        if (cause.cause().root() instanceof ServerPlayer) {
            return this.asAbstractPlayer((ServerPlayer) cause.cause().root());
        }
        return new SpongeCommandCause(this.plugin, cause);
    }
}
