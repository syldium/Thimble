package me.syldium.thimble.sponge.config;

import me.syldium.thimble.api.Location;
import me.syldium.thimble.common.config.SavedPlayer;
import me.syldium.thimble.sponge.adapter.SpongePlayerAdapter;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class SpongeSavedPlayer implements SavedPlayer<Player> {

    private static final transient long serialVersionUID = -1681012206529286329L;

    private final GameMode gameMode;
    private final Location location;
    private final double health, saturation, walkSpeed;
    private final int food, experience;

    public SpongeSavedPlayer(@NotNull SpongePlayerAdapter adapter, @NotNull Player player) {
        this.gameMode = player.gameMode().get();
        this.location = adapter.asAbstractLocation(player.getLocation(), player.getHeadRotation());
        this.health = player.get(Keys.HEALTH).get();
        this.food = player.get(Keys.FOOD_LEVEL).get();
        this.experience = player.get(Keys.TOTAL_EXPERIENCE).get();
        this.saturation = player.get(Keys.SATURATION).get();
        this.walkSpeed = player.get(Keys.WALKING_SPEED).get();
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
    public void restore(@NotNull Player player, boolean withLocation) {
        player.offer(Keys.GAME_MODE, this.gameMode);
        if (withLocation) {
            player.setLocation(new org.spongepowered.api.world.Location<>(
                    Sponge.getServer().getWorld(this.location.worldKey().value()).get(),
                    this.location.x(),
                    this.location.y(),
                    this.location.z()
            ));
        }
        player.offer(Keys.HEALTH, this.health);
        player.offer(Keys.FOOD_LEVEL, this.food);
        player.offer(Keys.TOTAL_EXPERIENCE, this.experience);
        player.offer(Keys.SATURATION, this.saturation);
        player.offer(Keys.WALKING_SPEED, this.walkSpeed);
    }
}
