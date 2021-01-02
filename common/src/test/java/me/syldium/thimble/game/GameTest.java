package me.syldium.thimble.game;

import me.syldium.thimble.PluginMock;
import me.syldium.thimble.api.arena.ThimbleGameMode;
import me.syldium.thimble.common.game.Game;
import me.syldium.thimble.mock.player.PlayerMock;
import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.arena.ThimbleState;
import me.syldium.thimble.common.game.Arena;
import net.kyori.adventure.util.Ticks;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class GameTest {

    private final PluginMock plugin;

    public GameTest() throws IOException {
        this.plugin = new PluginMock();
    }

    @AfterEach
    public void cancelTasks() {
        this.plugin.getScheduler().cancelAllTasks();
    }

    @Test
    public void start() {
        Arena arena = this.newArena(ThimbleGameMode.SINGLE);
        assertTrue(arena.getGame().isEmpty(), "No Game instance should exist.");
        PlayerMock player = this.plugin.addPlayer();
        assertNotEquals(arena.getSpawnLocation(), player.getLocation());
        arena.addPlayer(player);
        assertTrue(arena.getGame().isPresent(), "A game object should be created.");
        assertTrue(this.plugin.getGameService().getGame(player).isPresent());
        this.plugin.getScheduler().assertScheduled();
        this.plugin.getScheduler().nextTick();
        assertEquals(ThimbleState.WAITING, arena.getGame().get().getState());
        assertEquals(arena.getSpawnLocation(), player.getLocation());
    }

    @Test
    public void startingSingleMode() {
        Arena arena = this.newArena(ThimbleGameMode.SINGLE);
        List<PlayerMock> players = this.joinThreePlayers(arena);
        assertEquals(ThimbleState.WAITING, arena.getGame().get().getState());
        this.plugin.getScheduler().nextTick();
        assertEquals(ThimbleState.STARTING, arena.getGame().get().getState());
        this.plugin.getScheduler().nextTicks(Ticks.TICKS_PER_SECOND + 1);
        assertEquals(ThimbleState.PLAYING, arena.getGame().get().getState());
        this.plugin.getScheduler().nextTick();

        boolean oneIsJumping = false;
        for (PlayerMock player : players) {
            if (player.getLocation().equals(arena.getJumpLocation())) {
                if (oneIsJumping) {
                    fail("Only one player should be jumping.");
                }
                oneIsJumping = true;
            } else {
                assertEquals(arena.getWaitLocation(), player.getLocation());
            }
        }
        if (!oneIsJumping) {
            fail("One player should be jumping.");
        }
    }

    @Test
    public void startingConcurrentMode() {
        Arena arena = this.newArena(ThimbleGameMode.CONCURRENT);
        List<PlayerMock> players = this.joinThreePlayers(arena);
        Game game = (Game) arena.getGame().get();
        game.onCountdownEnd();
        for (PlayerMock player : players) {
            assertEquals(arena.getJumpLocation(), player.getLocation(), "All players should be at the jump location.");
        }
    }

    private @NotNull Arena newArena(@NotNull ThimbleGameMode gameMode) {
        Arena arena = new Arena(this.plugin, "test");
        UUID world = UUID.randomUUID();
        arena.setMinPlayers(2).setMaxPlayers(4)
            .setGameMode(gameMode)
            .setSpawnLocation(new Location(world, 100, 70, 100))
            .setWaitLocation(new Location(world, 100, 70, 150))
            .setJumpLocation(new Location(world, 100, 110, 150))
            .setPoolMinPoint(null).setPoolMaxPoint(null);
        return arena;
    }

    private @NotNull List<@NotNull PlayerMock> joinThreePlayers(@NotNull Arena arena) {
        List<PlayerMock> players = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            players.add(this.plugin.addPlayer());
            arena.addPlayer(players.get(i));
            assertEquals(arena.getSpawnLocation(), players.get(i).getLocation());
        }
        return players;
    }
}
