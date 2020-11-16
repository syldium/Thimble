package me.syldium.decoudre.common.player;

import me.syldium.decoudre.api.player.DePlayerStats;
import net.kyori.adventure.identity.Identity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class PlayerStats implements DePlayerStats, Identity {

    private final UUID uuid;
    private int wins;
    private int losses;
    private int jumps;
    private int dacs;

    public PlayerStats(@NotNull UUID uuid) {
        this(uuid, 0, 0, 0, 0);
    }

    public PlayerStats(@NotNull UUID uuid, int wins, int losses, int jumps, int dacs) {
        this.uuid = uuid;
        this.wins = wins;
        this.losses = losses;
        this.jumps = jumps;
        this.dacs = dacs;
    }

    @Override
    public @NotNull UUID uuid() {
        return this.uuid;
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
    public int getDacs() {
        return this.dacs;
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

    public void incrementDacs() {
        this.dacs++;
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
                ", dacs=" + this.dacs +
                '}';
    }
}
