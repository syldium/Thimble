package me.syldium.thimble.sponge.config;

import me.syldium.thimble.common.config.SavedPlayer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SpongeSavedPlayer implements SavedPlayer<Player> {

    private static final transient long serialVersionUID = -1681012206529286330L;

    private final GameMode gameMode;
    private final double health, saturation, walkSpeed;
    private final int food, experience;

    public SpongeSavedPlayer(@NotNull File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream data = new ObjectInputStream(new FileInputStream(file))) {
            this.gameMode = (GameMode) data.readObject();
            this.health = data.readDouble();
            this.food = data.readInt();
            this.experience = data.readInt();
            this.saturation = data.readFloat();
            this.walkSpeed = data.readFloat();
        }
    }

    public SpongeSavedPlayer(@NotNull Player player) {
        this.gameMode = player.gameMode().get();
        this.health = player.get(Keys.HEALTH).get();
        this.food = player.get(Keys.FOOD_LEVEL).get();
        this.experience = player.get(Keys.TOTAL_EXPERIENCE).get();
        this.saturation = player.get(Keys.SATURATION).get();
        this.walkSpeed = player.get(Keys.WALKING_SPEED).get();
    }

    @Override
    public void save(@NotNull File file) {
        try (ObjectOutputStream data = new ObjectOutputStream(new FileOutputStream(file))) {
            data.writeObject(this.gameMode);
            data.writeDouble(this.health);
            data.writeInt(this.food);
            data.writeFloat(this.experience);
            data.writeDouble(this.saturation);
            data.writeDouble(this.walkSpeed);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void restore(@NotNull Player player) {
        player.offer(Keys.GAME_MODE, this.gameMode);
        player.offer(Keys.HEALTH, this.health);
        player.offer(Keys.FOOD_LEVEL, this.food);
        player.offer(Keys.TOTAL_EXPERIENCE, this.experience);
        player.offer(Keys.SATURATION, this.saturation);
        player.offer(Keys.WALKING_SPEED, this.walkSpeed);
    }
}
