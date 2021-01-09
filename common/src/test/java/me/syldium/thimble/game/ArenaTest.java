package me.syldium.thimble.game;

import me.syldium.thimble.PluginMock;
import me.syldium.thimble.api.Location;
import me.syldium.thimble.common.game.Arena;
import me.syldium.thimble.mock.util.MockUtil;
import net.kyori.adventure.key.Key;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArenaTest {

    @Test
    public void playerCount() {
        //noinspection ConstantConditions
        Arena arena = new Arena(null, "arena");
        arena.setMinPlayers(5);
        assertEquals(5, arena.getMinPlayers());
        arena.setMaxPlayers(11);
        assertEquals(11, arena.getMaxPlayers());
        assertThrows(IllegalArgumentException.class, () -> arena.setMinPlayers(0));
        assertThrows(IllegalArgumentException.class, () -> arena.setMinPlayers(12));
        assertThrows(IllegalArgumentException.class, () -> arena.setMaxPlayers(3));
    }

    @Test
    public void locations() throws IOException {
        PluginMock plugin = new PluginMock();
        Arena arena = new Arena(plugin, "arena");
        assertThrows(IllegalStateException.class, () -> arena.addPlayer(UUID.randomUUID()));
        Key world = MockUtil.randomKey();
        arena.setSpawnLocation(new Location(world, 400, 70, 70));
        arena.setWaitLocation(new Location(world, 400, 70, 70));
        assertThrows(IllegalStateException.class, () -> arena.addPlayer(UUID.randomUUID()));

        arena.setJumpLocation(new Location(world, 400, 70, 70));
        arena.addPlayer(plugin.addPlayer());
        arena.setJumpLocation(new Location(world, 10, 70, -20));
        assertThrows(IllegalStateException.class, () -> arena.setJumpLocation(null));
        assertEquals(new Location(world, 10, 70, -20), arena.getJumpLocation());
        plugin.getScheduler().cancelAllTasks();
        plugin.getPlayerAdapter().removeAllPlayers();
    }
}
