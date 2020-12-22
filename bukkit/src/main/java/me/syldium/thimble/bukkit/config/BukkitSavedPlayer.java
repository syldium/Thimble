package me.syldium.thimble.bukkit.config;

import me.syldium.thimble.common.config.SavedPlayer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class BukkitSavedPlayer implements SavedPlayer<Player> {

    private static final transient long serialVersionUID = -1681012206529286330L;

    private final GameMode gameMode;
    private final ItemStack[] inventory;
    private final Collection<PotionEffect> effects;
    private final double health;
    private final int food, oxygen;
    private final float experience, saturation, walkSpeed;

    public BukkitSavedPlayer(@NotNull File file) throws IOException, ClassNotFoundException {
        try (BukkitObjectInputStream data = new BukkitObjectInputStream(new FileInputStream(file))) {
            this.gameMode = (GameMode) data.readObject();

            int invSize = data.readInt();
            this.inventory = new ItemStack[invSize];
            for (int i = 0; i < invSize; i++) {
                this.inventory[i] = (ItemStack) data.readObject();
            }

            int effSize = data.readInt();
            this.effects = new ArrayList<>(effSize);
            for (int i = 0; i < effSize; i++) {
                this.effects.add((PotionEffect) data.readObject());
            }

            this.health = data.readDouble();
            this.food = data.readInt();
            this.oxygen = data.readInt();
            this.experience = data.readFloat();
            this.saturation = data.readFloat();
            this.walkSpeed = data.readFloat();
        }
    }

    public BukkitSavedPlayer(@NotNull Player player) {
        ItemStack[] playerInventory = player.getInventory().getContents();
        this.gameMode = player.getGameMode();
        this.inventory = Arrays.copyOf(playerInventory, playerInventory.length);
        this.effects = player.getActivePotionEffects();
        this.health = player.getHealth();
        this.food = player.getFoodLevel();
        this.oxygen = player.getRemainingAir();
        this.experience = player.getExp();
        this.saturation = player.getSaturation();
        this.walkSpeed = player.getWalkSpeed();
    }

    @Override
    public void save(@NotNull File file) {
        try (BukkitObjectOutputStream data = new BukkitObjectOutputStream(new FileOutputStream(file))) {
            data.writeObject(this.gameMode);

            data.writeInt(this.inventory.length);
            for (ItemStack itemStack : this.inventory) {
                data.writeObject(itemStack);
            }

            data.writeInt(this.effects.size());
            for (PotionEffect effect : this.effects) {
                data.writeObject(effect);
            }

            data.writeDouble(this.health);
            data.writeInt(this.food);
            data.writeInt(this.oxygen);
            data.writeFloat(this.experience);
            data.writeFloat(this.saturation);
            data.writeFloat(this.walkSpeed);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void restore(@NotNull Player player) {
        player.setGameMode(this.gameMode);
        player.getInventory().setContents(this.inventory);
        player.addPotionEffects(this.effects);
        player.setHealth(this.health);
        player.setFoodLevel(this.food);
        player.setSaturation(this.saturation);
        player.setExp(this.experience);
        player.setRemainingAir(this.oxygen);
        player.setWalkSpeed(this.walkSpeed);
    }
}
