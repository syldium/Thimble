package me.syldium.thimble.service;

import me.syldium.thimble.api.Ranking;
import me.syldium.thimble.api.player.ThimblePlayerStats;
import me.syldium.thimble.common.player.PlayerStats;
import me.syldium.thimble.api.util.Leaderboard;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class LeaderboardTest {

    private final Leaderboard<ThimblePlayerStats> leaderboard;
    private long uuids = 0;

    public LeaderboardTest() {
        this.leaderboard = Leaderboard.of(Ranking.WINS);
        for (int wins : new int[]{40, 37, 32, 25, 23, 11, 9, 8, 4, 3}) {
            this.leaderboard.add(this.newPlayerStats(wins));
        }
    }

    @Test
    public void newEntryInEmptyLeaderboard() {
        Leaderboard<ThimblePlayerStats> leaderboard = Leaderboard.of(Ranking.WINS);
        PlayerStats stats = this.newPlayerStats(4);
        leaderboard.add(stats);
        assertEquals(stats, leaderboard.get(0));
        assertEquals(1, leaderboard.size());
    }

    @Test
    public void limitLeaderboard() {
        Leaderboard<ThimblePlayerStats> leaderboard = Leaderboard.of(Ranking.WINS);
        for (int wins = 20; wins > 0; wins--) {
            leaderboard.add(this.newPlayerStats(wins));
        }

        int wins = 20;
        for (ThimblePlayerStats stats : leaderboard) {
            assertEquals(wins--, stats.wins());
        }
        assertEquals(10, leaderboard.size());
    }

    @Test
    public void addInLeaderboard() {
        Leaderboard<ThimblePlayerStats> leaderboard = new Leaderboard<>(this.leaderboard);
        leaderboard.add(this.newPlayerStats(2));
        assertEquals(this.leaderboard, leaderboard);

        ThimblePlayerStats stats = this.newPlayerStats(29);
        leaderboard.add(stats);
        assertNotEquals(this.leaderboard, leaderboard);
        assertEquals(stats, leaderboard.get(3));
        assertEquals(this.leaderboard.get(3), leaderboard.get(4));
        assertEquals(Arrays.asList(40, 37, 32, 29, 25, 23, 11, 9, 8, 4), leaderboard.scores());
        assertFalse(leaderboard.containsScore(3));
    }

    @Test
    public void sortLeaderboard() {
        // Should be inserted at index = 5
        Leaderboard<ThimblePlayerStats> leaderboard = new Leaderboard<>(this.leaderboard);
        PlayerStats stats = this.newPlayerStats(16);
        leaderboard.add(stats);
        assertNotEquals(this.leaderboard, leaderboard);
        assertEquals(Arrays.asList(40, 37, 32, 25, 23, 16, 11, 9, 8, 4), leaderboard.scores());
        assertEquals(stats, leaderboard.get(5));

        // Index shouldn't change
        stats = this.newPlayerStats(stats);
        stats.incrementWins();
        leaderboard.add(stats);
        assertEquals(Arrays.asList(40, 37, 32, 25, 23, 17, 11, 9, 8, 4), leaderboard.scores());
        stats = this.newPlayerStats(stats);
        for (int i = 0; i < 6; i++) {
            stats.incrementWins();
        }
        leaderboard.add(stats);
        assertEquals(Arrays.asList(40, 37, 32, 25, 23, 23, 11, 9, 8, 4), leaderboard.scores());
        assertNotEquals(stats, leaderboard.get(4));
        assertEquals(stats, leaderboard.get(5));

        // Elements at index = 4 and at index 5 should be reversed
        stats = this.newPlayerStats(stats);
        stats.incrementWins();
        leaderboard.add(stats);
        assertEquals(stats, leaderboard.get(4));
        assertNotEquals(stats, leaderboard.get(5));
    }

    public @NotNull PlayerStats newPlayerStats(int wins) {
        return new PlayerStats(new UUID(this.uuids++, 0), "test" + this.uuids, wins, 0, 0, 0, 0);
    }

    public @NotNull PlayerStats newPlayerStats(@NotNull ThimblePlayerStats stats) {
        return new PlayerStats(stats.uuid(), stats.name(), stats.wins(), stats.losses(), stats.jumps(), stats.failedJumps(), stats.thimbles());
    }
}
