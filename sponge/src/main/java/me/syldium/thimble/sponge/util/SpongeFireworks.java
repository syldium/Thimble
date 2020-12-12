package me.syldium.thimble.sponge.util;

import me.syldium.thimble.common.util.Fireworks;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.projectile.Firework;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collections;

public class SpongeFireworks extends Fireworks {

    private final Location<World> from;

    public SpongeFireworks(@NotNull Location<World> from) {
        this.from = from;
    }

    @Override
    public void spawn(int count) {
        for (int i = 0; i < count; i++){
            Firework firework = (Firework) this.from.getExtent().createEntityNaturally(EntityTypes.FIREWORK, this.from.getPosition());
            FireworkEffect effect = FireworkEffect.builder()
                    .flicker(this.random.nextBoolean())
                    .trail(this.random.nextBoolean())
                    .color(this.color())
                    .build();
            firework.offer(Keys.FIREWORK_EFFECTS, Collections.singletonList(effect));
            firework.offer(Keys.FIREWORK_FLIGHT_MODIFIER, this.random.nextInt(2) + 1);
            this.from.getExtent().spawnEntity(firework);
        }
    }

    private @NotNull Color color() {
        return Color.ofRgb(this.randomRed(), this.randomGreen(), this.randomBlue());
    }
}
