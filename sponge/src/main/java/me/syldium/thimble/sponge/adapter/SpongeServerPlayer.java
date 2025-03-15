package me.syldium.thimble.sponge.adapter;

import me.syldium.thimble.api.Location;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.player.AbstractPlayer;
import me.syldium.thimble.common.world.PoolBlock;
import me.syldium.thimble.sponge.util.SpongeAdapter;
import me.syldium.thimble.sponge.world.SpongePoolBlock;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.MatterType;
import org.spongepowered.api.data.type.MatterTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.server.ServerLocation;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SpongeServerPlayer extends AbstractPlayer<ServerPlayer> {

    public SpongeServerPlayer(@NotNull ThimblePlugin plugin, @NotNull ServerPlayer handle) {
        super(plugin, handle, (Audience) handle);
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return this.handle.hasPermission(permission);
    }

    @Override
    public @NotNull Location getLocation() {
        return SpongeAdapter.get().asAbstract(this.handle.serverLocation(), this.handle.headRotation().get());
    }

    @Override
    public boolean teleport(@NotNull Location location) {
        return this.handle.setLocationAndRotation(SpongeAdapter.get().asSponge(location), SpongeAdapter.get().asHeadRotation(location));
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> teleportAsync(@NotNull Location location) {
        return null;
    }

    @Override
    public @NotNull PoolBlock getFirstLiquidBlock() {
        ServerLocation location = this.handle.serverLocation();
        while (this.isLiquid(location.relativeTo(Direction.UP).block())) {
            location = location.relativeTo(Direction.UP);
        }
        return new SpongePoolBlock(location);
    }

    private boolean isLiquid(@NotNull BlockState blockState) {
        Optional<MatterType> matter = blockState.get(Keys.MATTER_TYPE);
        return matter.isPresent() && matter.get().equals(MatterTypes.LIQUID.get());
    }

    @Override
    public boolean isInWater() {
        return this.handle.wet().get();
    }

    @Override
    public void setMiniGameMode(boolean clearInventory) {

    }

    @Override
    public void spectate() {

    }

    @Override
    public boolean isVanished() {
        return false;
    }

    @Override
    public void sendExperienceChange(float percent, int level) {

    }

    @Override
    public java.util.@NotNull UUID uuid() {
        return this.handle.uniqueId();
    }

    @Override
    public @NotNull String name() {
        return this.handle.name();
    }

    @Override
    public @NotNull Component displayName() {
        return this.handle.displayName().get();
    }
}
