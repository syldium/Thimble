package me.syldium.thimble.common.player;

import me.syldium.thimble.api.player.ThimblePlayerStats;
import net.kyori.adventure.identity.Identity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class PlayerStats implements ThimblePlayerStats, Identity {

    private final UUID uuid;
    private final String name;
    private int wins;
    private int losses;
    private int jumps;
    private int thimbles;

    public PlayerStats(@NotNull UUID uuid, @NotNull String name) {
        this(uuid, name, 0, 0, 0, 0);
    }

    public PlayerStats(@NotNull UUID uuid, @NotNull String name, int wins, int losses, int jumps, int thimbles) {
        this.uuid = uuid;
        this.name = name;
        this.wins = wins;
        this.losses = losses;
        this.jumps = jumps;
        this.thimbles = thimbles;
    }

    @Override
    public @NotNull UUID uuid() {
        return this.uuid;
    }

    @Override
    public @NotNull String name() {
        return this.name;
    }

    @Override
    public int getWins() {
        return this.wins;
    }

    @Override
    public int getLosses() {
        return this.losses;
    }

    @Override
    public int getJumps() {
        return this.jumps;
    }

    @Override
    public int getThimbles() {
        return this.thimbles;
    }

    public void incrementWins() {
        this.wins++;
    }

    public void incrementLosses() {
        this.losses++;
    }

    public void incrementJumps() {
        this.jumps++;
    }

    public void incrementThimbles() {
        this.thimbles++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerStats)) return false;
        PlayerStats that = (PlayerStats) o;
        return this.uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uuid);
    }

    @Override
    public String toString() {
        return "PlayerStats{" +
                "uuid=" + this.uuid +
                ", wins=" + this.wins +
                ", losses=" + this.losses +
                ", jumps=" + this.jumps +
                ", thimbles=" + this.thimbles +
                '}';
    }
}
