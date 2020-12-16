package me.syldium.thimble.common.player;

import me.syldium.thimble.api.Location;
import me.syldium.thimble.common.command.abstraction.Sender;
import me.syldium.thimble.common.world.PoolBlock;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Future;

public interface Player extends PlayerAudience, Identified, Identity, Sender {

    /**
     * Gets the player's current position.
     *
     * @return A new copy of Location containing the position of this player
     */
    @NotNull Location getLocation();

    /**
     * Teleports this entity to the given location.
     *
     * @param location New location to teleport this player to.
     * @return {@code true} if the teleport was successful.
     */
    @NotNull Future<@NotNull Boolean> teleport(@NotNull Location location);

    /**
     * Gets the surface block of a water/lava column.
     *
     * <p>If the player is not {@link #isInWater()}, the returned block will not be a liquid.</p>
     *
     * @return The first liquid block.
     */
    @NotNull PoolBlock getFirstLiquidBlock();

    /**
     * Check if the player is in water.
     *
     * @return Whether the player is in water.
     */
    boolean isInWater();
}
