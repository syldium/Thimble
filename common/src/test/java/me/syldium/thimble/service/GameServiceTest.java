package me.syldium.thimble.service;

import me.syldium.thimble.PluginMock;
import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.arena.ThimbleArena;
import me.syldium.thimble.api.arena.ThimbleState;
import me.syldium.thimble.api.service.GameService;
import me.syldium.thimble.api.util.WorldKey;
import me.syldium.thimble.common.game.Game;
import me.syldium.thimble.common.service.GameServiceImpl;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameServiceTest {

    private final PluginMock plugin;

    public GameServiceTest() throws IOException {
        this.plugin = new PluginMock();
    }

    @Test
    public void findBestArena() {
        final GameService gameService = new GameServiceImpl(this.plugin);
        final Random random = new Random();
        assertEquals(Optional.empty(), gameService.findAvailableArena(GameService.ArenaSelection.MOST_FILLED, 1, random));
        final ThimbleArena arena1 = gameService.createArena("arena1");
        assertEquals(Optional.empty(), gameService.findAvailableArena(GameService.ArenaSelection.MOST_FILLED, 1, random));
        fakeArenaSetup(arena1);
        assertEquals(Optional.of(arena1), gameService.findAvailableArena(GameService.ArenaSelection.MOST_FILLED, 1, random));
        arena1.addPlayer(this.plugin.addPlayer().uuid());
        assertEquals(Optional.of(arena1), gameService.findAvailableArena(GameService.ArenaSelection.MOST_FILLED, 1, random));

        final ThimbleArena arena2 = gameService.createArena("arena2");
        fakeArenaSetup(arena2);
        assertEquals(Optional.of(arena1), gameService.findAvailableArena(GameService.ArenaSelection.MOST_FILLED, 1, random));
        assertEquals(Optional.of(arena2), gameService.findAvailableArena(GameService.ArenaSelection.LEAST_FILLED, 1, random));

        ((Game) arena1.game().get()).setState(ThimbleState.PLAYING);
        assertEquals(Optional.of(arena2), gameService.findAvailableArena(GameService.ArenaSelection.MOST_FILLED, 1, random));
        assertEquals(Optional.of(arena2), gameService.findAvailableArena(GameService.ArenaSelection.LEAST_FILLED, 1, random));
    }

    private static void fakeArenaSetup(@NotNull ThimbleArena arena) {
        final Location location = new Location(new WorldKey("test", "test"), 0D, 0D, 0D);
        arena.setSpawnLocation(location);
        arena.setWaitLocation(location);
        arena.setJumpLocation(location);
    }
}
