package me.syldium.thimble.service;

import me.syldium.thimble.api.player.ThimblePlayerStats;
import me.syldium.thimble.common.player.PlayerStats;
import me.syldium.thimble.common.service.DataService;
import me.syldium.thimble.common.service.SqlDataService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SqliteDataServiceTest {

    @Test
    public void saveFetch() {
        DataService service = new SqlDataService();
        PlayerStats stats = new PlayerStats(UUID.randomUUID(), "test");
        assertTrue(service.getPlayerStatistics(stats.uuid()).isEmpty());
        stats.incrementJumps();
        service.savePlayerStatistics(stats);
        assertStatsEquals(stats, service.getPlayerStatistics(stats.uuid()));
        assertStatsEquals(stats, service.getPlayerStatistics(stats.name()));
        stats.incrementFailedJumps();
        assertStatsNotEquals(stats, service.getPlayerStatistics(stats.uuid()));
        service.close();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static void assertStatsEquals(@NotNull PlayerStats excepted, @NotNull Optional<ThimblePlayerStats> actualOpt) {
        assertTrue(actualOpt.isPresent());
        assertEquals(excepted, actualOpt.get());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static void assertStatsNotEquals(@NotNull PlayerStats excepted, @NotNull Optional<ThimblePlayerStats> actualOpt) {
        assertTrue(actualOpt.isPresent());
        assertNotEquals(excepted, actualOpt.get());
    }
}
