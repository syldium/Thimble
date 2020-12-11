package me.syldium.decoudre.common.config;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;

public interface GameConfig {

    Sound DEFAULT_JUMP_FAILED_SOUND = Sound.sound(Key.key("block.basalt.break"), Sound.Source.PLAYER, 1f, 1f);
    Sound DEFAULT_JUMP_SUCCEED_SOUND = Sound.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.PLAYER, 1f, 1f);
    Key DEFAULT_TIMER_SOUND_KEY = Key.key("block.note_block.harp");

    static @NotNull Sound getJumpFailedSound() {
        return DEFAULT_JUMP_FAILED_SOUND;
    }

    static @NotNull Sound getJumpSucceedSound() {
        return DEFAULT_JUMP_SUCCEED_SOUND;
    }

    static Sound getTimerSound(int remainingTicks) {
        return Sound.sound(DEFAULT_TIMER_SOUND_KEY, Sound.Source.PLAYER, 1f, remainingTicks == 0 ? 1.5f : 1.0f);
    }
}
