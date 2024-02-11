package me.syldium.thimble.game;

import me.syldium.thimble.PluginMock;
import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.util.WorldKey;
import me.syldium.thimble.common.game.Arena;
import me.syldium.thimble.mock.util.MockUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArenaTest {

    private @TempDir File dataFolder;

    @Test
    public void playerCount() {
        //noinspection ConstantConditions
        Arena arena = new Arena(null, "arena");
        arena.setMinPlayers(5);
        assertEquals(5, arena.minPlayers());
        arena.setMaxPlayers(11);
        assertEquals(11, arena.maxPlayers());
        assertThrows(IllegalArgumentException.class, () -> arena.setMinPlayers(0));
        assertThrows(IllegalArgumentException.class, () -> arena.setMinPlayers(12));
        assertThrows(IllegalArgumentException.class, () -> arena.setMaxPlayers(3));
    }

    @Test
    public void locations() {
        PluginMock plugin = new PluginMock(this.dataFolder);
        Arena arena = new Arena(plugin, "arena");
        assertThrows(IllegalStateException.class, () -> arena.addPlayer(UUID.randomUUID()));
        WorldKey world = MockUtil.randomKey();
        arena.setSpawnLocation(new Location(world, 400, 70, 70));
        arena.setWaitLocation(new Location(world, 400, 70, 70));
        assertThrows(IllegalStateException.class, () -> arena.addPlayer(UUID.randomUUID()));

        arena.setJumpLocation(new Location(world, 400, 70, 70));
        arena.addPlayer(plugin.addPlayer());
        arena.setJumpLocation(new Location(world, 10, 70, -20));
        assertThrows(IllegalStateException.class, () -> arena.setJumpLocation(null));
        assertEquals(new Location(world, 10, 70, -20), arena.jumpLocation());
        plugin.getScheduler().cancelAllTasks();
        plugin.getPlayerAdapter().removeAllPlayers();
    }
}
