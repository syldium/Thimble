package me.syldium.thimble.bukkit.util;

import me.syldium.thimble.common.util.Fireworks;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;

public class BukkitFireworks extends Fireworks {

    private final Location from;

    public BukkitFireworks(@NotNull Location from) {
        this.from = from;
    }

    @Override
    public void spawn(int count) {
        for (int i = 0; i < count; i++){
            Firework firework = (Firework) this.from.getWorld().spawnEntity(this.from, EntityType.FIREWORK);
            FireworkMeta meta = firework.getFireworkMeta();
            FireworkEffect effect = FireworkEffect.builder()
                    .flicker(this.random.nextBoolean())
                    .trail(this.random.nextBoolean())
                    .withColor(this.color())
                    .build();
            meta.addEffect(effect);
            meta.setPower(this.random.nextInt(2) + 1);
            firework.setFireworkMeta(meta);
        }
    }

    private @NotNull Color color() {
        return Color.fromRGB(this.randomRed(), this.randomGreen(), this.randomBlue());
    }
}
