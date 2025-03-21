package me.syldium.thimble.sponge.config;

import me.syldium.thimble.api.Location;
import me.syldium.thimble.common.config.SavedPlayer;
import me.syldium.thimble.sponge.util.SpongeAdapter;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class SpongeSavedPlayer implements SavedPlayer<ServerPlayer> {

    private static final transient long serialVersionUID = -1681012206529286329L;

    private final GameMode gameMode;
    private final Location location;
    private final double health, saturation, walkSpeed;
    private final int food, experience;

    public SpongeSavedPlayer(@NotNull ServerPlayer player) {
        this.gameMode = player.gameMode().get();
        this.location = SpongeAdapter.get().asAbstract(player.serverLocation(), player.rotation());
        this.health = player.health().get();
        this.food = player.foodLevel().get();
        this.experience = player.experience().get();
        this.saturation = player.saturation().get();
        this.walkSpeed = player.walkingSpeed().get();
    }

    @Override
    public void save(@NotNull File file) {
        try (OutputStream stream = new FileOutputStream(file);
             ObjectOutputStream data = new ObjectOutputStream(stream)) {
            data.writeObject(this);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void restore(@NotNull ServerPlayer player, boolean restoreInventory, boolean withLocation) {
        player.offer(Keys.GAME_MODE, this.gameMode);
        if (withLocation) {
            player.setLocationAndRotation(SpongeAdapter.get().asSponge(this.location), SpongeAdapter.get().asHeadRotation(this.location));
        }
        player.offer(Keys.HEALTH, this.health);
        player.offer(Keys.FOOD_LEVEL, this.food);
        player.offer(Keys.EXPERIENCE, this.experience);
        player.offer(Keys.SATURATION, this.saturation);
        player.offer(Keys.WALKING_SPEED, this.walkSpeed);
    }
}
