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
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

public class SpongePlayer extends AbstractPlayer<Player> {

    private final SpongePlayerAdapter platform;
    private ExperienceHolderData lastExpHolderData;

    public SpongePlayer(@NotNull ThimblePlugin plugin, @NotNull Player handle, @NotNull Audience audience, @NotNull SpongePlayerAdapter platform) {
        super(plugin, handle, audience);
        this.platform = platform;
    }

    @Override
    public @NotNull me.syldium.thimble.api.Location getLocation() {
        return this.platform.asAbstractLocation(this.getHandle().getLocation(), this.getHandle().getHeadRotation());
    }

    @Override
    public boolean teleport(@NotNull me.syldium.thimble.api.Location location) {
        return this.getHandle().setLocationAndRotation(this.platform.asPlatform(location), this.platform.asHeadRotation(location));
    }

    @Override
    public @NotNull PoolBlock getFirstLiquidBlock() {
        Location<World> location = this.getHandle().getLocation();
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
        return this.getHandle().getLocation().getBlock().getType().equals(BlockTypes.WATER);
    }

    @Override
    public void sendExperienceChange(float percent, int level) {
        Optional<ExperienceHolderData> optional = this.getHandle().get(ExperienceHolderData.class);
        if (optional.isPresent()) {
            ExperienceHolderData expHolder = optional.get();
            this.lastExpHolderData = expHolder.copy();
            expHolder.set(Keys.EXPERIENCE_LEVEL, level);
            this.getHandle().offer(expHolder);
        }
    }

    @Override
    public void sendRealExperience() {
        if (this.lastExpHolderData != null) {
            this.getHandle().offer(this.lastExpHolderData);
        }
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return this.getHandle().hasPermission(permission);
    }

    @Override
    public @NotNull String name() {
        return this.getHandle().getName();
    }

    @Override
    public @NotNull UUID uuid() {
        return this.getHandle().getUniqueId();
    }
}
