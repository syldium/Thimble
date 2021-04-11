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
    private int fails;
    private int thimbles;

    public PlayerStats(@NotNull UUID uuid, @NotNull String name) {
        this(uuid, name, 0, 0, 0, 0, 0);
    }

    public PlayerStats(@NotNull UUID uuid, @NotNull String name, int wins, int losses, int jumps, int fails, int thimbles) {
        this.uuid = uuid;
        this.name = name;
        this.wins = wins;
        this.losses = losses;
        this.jumps = jumps;
        this.fails = fails;
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
    public int wins() {
        return this.wins;
    }

    @Override
    public int losses() {
        return this.losses;
    }

    @Override
    public int jumps() {
        return this.jumps;
    }

    @Override
    public int failedJumps() {
        return this.fails;
    }

    @Override
    public int thimbles() {
        return this.thimbles;
    }

    @Override
    public boolean equalsPlayer(@NotNull ThimblePlayerStats o) {
        return this.uuid.equals(o.uuid());
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

    public void incrementFailedJumps() {
        this.fails++;
    }

    public void incrementThimbles() {
        this.thimbles++;
    }

    public boolean playerEquals(@NotNull PlayerStats o) {
        return this.uuid.equals(o.uuid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerStats)) return false;
        PlayerStats that = (PlayerStats) o;
        return this.wins == that.wins
                && this.losses == that.losses
                && this.jumps == that.jumps
                && this.fails == that.fails
                && this.thimbles == that.thimbles
                && this.uuid.equals(that.uuid)
                && this.name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uuid);
    }

    @Override
    public String toString() {
        return "PlayerStats{" +
                "name=" + this.name +
                ", wins=" + this.wins +
                ", losses=" + this.losses +
                ", jumps=" + this.jumps +
                ", fails=" + this.fails +
                ", thimbles=" + this.thimbles +
                '}';
    }
}
