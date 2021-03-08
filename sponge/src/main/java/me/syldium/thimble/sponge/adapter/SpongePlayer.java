package me.syldium.thimble.sponge.adapter;

import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.player.AbstractPlayer;
import me.syldium.thimble.common.world.PoolBlock;
import me.syldium.thimble.sponge.world.SpongePoolBlock;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.ExperienceHolderData;
import org.spongepowered.api.data.property.block.MatterProperty;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SpongePlayer extends AbstractPlayer<Player> {

    private final SpongePlayerAdapter platform;

    public SpongePlayer(@NotNull ThimblePlugin plugin, @NotNull Player handle, @NotNull Audience audience, @NotNull SpongePlayerAdapter platform) {
        super(plugin, handle, audience);
        this.platform = platform;
    }

    @Override
    public @NotNull me.syldium.thimble.api.Location getLocation() {
        return this.platform.asAbstractLocation(this.handle.getLocation(), this.handle.getHeadRotation());
    }

    @Override
    public boolean teleport(me.syldium.thimble.api.@NotNull Location location) {
        return this.handle.setLocationAndRotation(this.platform.asPlatform(location), this.platform.asHeadRotation(location));
    }

    @Override
    public @NotNull CompletableFuture<Boolean> teleportAsync(@NotNull me.syldium.thimble.api.Location location) {
        return CompletableFuture.completedFuture(this.teleport(location));
    }

    @Override
    public @NotNull PoolBlock getFirstLiquidBlock() {
        Location<World> location = this.handle.getLocation();
        while (this.isLiquid(location.getBlockRelative(Direction.UP).getBlock())) {
            location = location.getBlockRelative(Direction.UP);
        }
        return new SpongePoolBlock(location.getBlock(), location);
    }

    private boolean isLiquid(@NotNull BlockState blockState) {
        Optional<MatterProperty> matter = blockState.getProperty(MatterProperty.class);
        return matter.isPresent() && matter.get().getValue() == MatterProperty.Matter.LIQUID;
    }

    @Override
    public boolean isInWater() {
        return this.handle.getLocation().getBlock().getType().equals(BlockTypes.WATER);
    }

    @Override
    public void setMiniGameMode(boolean clearInventory) {
        if (clearInventory) {
            this.handle.getInventory().clear();
        }
        this.handle.offer(Keys.GAME_MODE, GameModes.ADVENTURE);
    }

    @Override
    public void spectate() {
        this.handle.offer(Keys.GAME_MODE, GameModes.SPECTATOR);
    }

    @Override
    public boolean isVanished() {
        return false;
    }

    @Override
    public void sendExperienceChange(float percent, int level) {
        Optional<ExperienceHolderData> optional = this.handle.get(ExperienceHolderData.class);
        if (optional.isPresent()) {
            ExperienceHolderData expHolder = optional.get();
            expHolder.set(Keys.EXPERIENCE_LEVEL, level);
            this.handle.offer(expHolder);
        }
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return this.handle.hasPermission(permission);
    }

    @Override
    public @NotNull String name() {
        return this.handle.getName();
    }

    @Override
    public @NotNull UUID uuid() {
        return this.handle.getUniqueId();
    }
}
