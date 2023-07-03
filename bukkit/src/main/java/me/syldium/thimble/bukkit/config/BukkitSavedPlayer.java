package me.syldium.thimble.bukkit.config;

import me.syldium.thimble.common.config.SavedPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;

public class BukkitSavedPlayer implements SavedPlayer<Player> {

    private static final transient long serialVersionUID = -1681012206529286329L;

    private final GameMode gameMode;
    private final Location location;
    private final ItemStack[] inventory;
    private final Collection<PotionEffect> effects;
    private final double health;
    private final int food, oxygen;
    private final float experience, saturation, walkSpeed;

    public BukkitSavedPlayer(@NotNull Player player) {
        ItemStack[] playerInventory = player.getInventory().getContents();
        this.gameMode = player.getGameMode();
        this.location = player.getLocation();
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
        try (OutputStream stream = new FileOutputStream(file);
             BukkitObjectOutputStream data = new BukkitObjectOutputStream(stream)) {
            data.writeObject(this);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void restore(@NotNull Player player, boolean restoreInventory, boolean withLocation) {
        player.setGameMode(this.gameMode);
        if (withLocation) player.teleport(this.location);
        if (restoreInventory) player.getInventory().setContents(this.inventory);
        player.addPotionEffects(this.effects);
        player.setHealth(Math.min(this.health, player.getMaxHealth()));
        player.setFoodLevel(this.food);
        player.setSaturation(this.saturation);
        player.setExp(this.experience);
        player.setRemainingAir(this.oxygen);
        player.setWalkSpeed(this.walkSpeed);
    }
}
