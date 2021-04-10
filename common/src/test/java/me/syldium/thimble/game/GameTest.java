package me.syldium.thimble.game;

import me.syldium.thimble.PluginMock;
import me.syldium.thimble.api.arena.ThimbleGameMode;
import me.syldium.thimble.api.player.JumpVerdict;
import me.syldium.thimble.api.util.BlockVector;
import me.syldium.thimble.api.util.WorldKey;
import me.syldium.thimble.common.game.Game;
import me.syldium.thimble.common.game.SingleGame;
import me.syldium.thimble.common.player.InGamePlayer;
import me.syldium.thimble.common.player.Player;
import me.syldium.thimble.mock.player.PlayerMock;
import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.arena.ThimbleState;
import me.syldium.thimble.common.game.Arena;
import me.syldium.thimble.mock.util.BlockDataMock;
import me.syldium.thimble.mock.util.MockUtil;
import net.kyori.adventure.util.Ticks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        this.plugin.getWorld().clear();
        this.plugin.getPlayerAdapter().removeAllPlayers();
    }

    @Test
    public void start() {
        Arena arena = this.newArena(ThimbleGameMode.SINGLE);
        assertTrue(arena.game().isEmpty(), "No Game instance should exist.");
        PlayerMock player = this.plugin.addPlayer();
        assertNotEquals(arena.spawnLocation(), player.getLocation());
        arena.addPlayer(player);
        assertTrue(arena.game().isPresent(), "A game object should be created.");
        assertTrue(this.plugin.getGameService().playerGame(player).isPresent());
        this.plugin.getScheduler().assertScheduled();
        this.plugin.getScheduler().nextTick();
        assertEquals(ThimbleState.WAITING, arena.game().get().state());
        assertEquals(arena.spawnLocation(), player.getLocation());
    }

    @Test
    public void startingSingleMode() {
        Arena arena = this.newArena(ThimbleGameMode.SINGLE);
        List<PlayerMock> players = this.joinThreePlayers(arena);
        assertEquals(ThimbleState.WAITING, arena.game().get().state());
        this.plugin.getScheduler().nextTick();
        assertEquals(ThimbleState.STARTING, arena.game().get().state());
        this.plugin.getScheduler().nextSecond();
        assertEquals(ThimbleState.PLAYING, arena.game().get().state());
        this.plugin.getScheduler().nextTick();

        boolean oneIsJumping = false;
        for (PlayerMock player : players) {
            if (player.getLocation().equals(arena.jumpLocation())) {
                if (oneIsJumping) {
                    fail("Only one player should be jumping.");
                }
                oneIsJumping = true;
            } else {
                assertEquals(arena.waitLocation(), player.getLocation());
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
        Game game = (Game) arena.game().get();
        game.onCountdownEnd();
        for (PlayerMock player : players) {
            assertEquals(arena.jumpLocation(), player.getLocation(), "All players should be at the jump location.");
        }
    }

    @Test
    public void singleMode() {
        Arena arena = this.newArena(ThimbleGameMode.SINGLE);
        this.joinThreePlayers(arena);
        SingleGame game = (SingleGame) arena.game().get();
        this.plugin.getScheduler().nextTicks(Ticks.TICKS_PER_SECOND + 2);
        UUID jumperUniqueId = game.currentJumper();
        assertNotNull(jumperUniqueId);

        // The first player fails.
        game.verdict(jumperUniqueId, JumpVerdict.MISSED);
        assertFalse(game.alivePlayers().stream().anyMatch(p -> p.uuid().equals(jumperUniqueId)));
        this.plugin.getScheduler().nextTick();
        List<UUID> playersWhoJumped = new LinkedList<>();
        playersWhoJumped.add(jumperUniqueId);

        // The others pass their jump.
        UUID uuid = this.assertLanded(game, 21);
        assertFalse(playersWhoJumped.contains(uuid));
        playersWhoJumped.add(uuid);
        uuid = this.assertLanded(game, 23);
        assertFalse(playersWhoJumped.contains(uuid));
        playersWhoJumped.add(uuid);

        // End of the game.
        this.plugin.getScheduler().nextTick();
        assertEquals(2, game.alivePlayers().size());
        assertEquals(playersWhoJumped.get(1), game.currentJumper());
        game.verdict(playersWhoJumped.get(1), JumpVerdict.MISSED);
        assertEquals(ThimbleState.END, game.state());
        this.plugin.getScheduler().assertScheduled();
        this.plugin.getScheduler().nextSecond();
        this.plugin.getScheduler().assertNothingScheduled();
        assertTrue(this.plugin.getWorld().isEmpty());
    }

    @Test
    public void vanishedPlayer() {
        Arena arena = this.newArena(ThimbleGameMode.SINGLE);
        arena.addPlayer(this.plugin.addPlayer());
        arena.addPlayer(this.plugin.addPlayer());
        PlayerMock vanishedPlayer = this.plugin.addPlayer();
        vanishedPlayer.setVanished(true);
        arena.addPlayer(vanishedPlayer);

        // The player should not have any influence on the game start.
        arena.setMinPlayers(3);
        this.plugin.getScheduler().nextTick();
        SingleGame game = (SingleGame) arena.game().get();
        assertEquals(ThimbleState.WAITING, game.state());
        arena.setMinPlayers(2);

        this.plugin.getScheduler().nextTick();
        assertEquals(ThimbleState.STARTING, game.state());
        assertEquals(2, game.size(), "The vanished player should not be counted in the players count.");
        assertEquals(3, game.realSize());

        // The vanished player is teleported, but must not be in the queue...
        this.plugin.getScheduler().nextTicks(Ticks.TICKS_PER_SECOND + 2);
        Set<Location> locations = Set.of(arena.jumpLocation(), arena.waitLocation());
        for (PlayerMock player : this.plugin.getPlayers()) {
            if (player.equals(vanishedPlayer)) {
                assertTrue(Set.of(arena.spawnLocation(), arena.waitLocation()).contains(player.getLocation()));
            } else {
                assertTrue(locations.contains(player.getLocation()));
            }
        }
        assertNotEquals(game.currentJumper(), vanishedPlayer.uuid());
        assertNotEquals(game.peekNextJumper(), vanishedPlayer.uuid());
        game.verdict(JumpVerdict.LANDED);
        this.plugin.getScheduler().nextTick();
        assertNotEquals(game.peekNextJumper(), vanishedPlayer.uuid());

        // ...and must not prevent the game from ending.
        game.verdict(JumpVerdict.MISSED);
        this.plugin.getScheduler().nextTick();
        assertEquals(ThimbleState.END, game.state());
        this.plugin.getScheduler().nextSecond();
        this.plugin.getScheduler().assertNothingScheduled();
    }

    @Test
    public void leaveArena_shouldBeRemovedFromQueue() {
        Arena arena = this.newArena(ThimbleGameMode.SINGLE);
        List<PlayerMock> players = this.joinThreePlayers(arena);
        SingleGame game = (SingleGame) arena.game().get();
        game.onCountdownEnd();
        game.setState(ThimbleState.PLAYING);
        assertEquals(players.size(), game.jumperQueue().size());
        this.plugin.getScheduler().nextTick();
        assertEquals(players.size() - 1, game.jumperQueue().size());

        // One player in the queue leave
        PlayerMock leavingPlayer = players.get(0).uuid().equals(game.currentJumper()) ? players.get(1) : players.get(0);
        assertTrue(game.jumperQueue().contains(leavingPlayer.uuid()));
        game.removePlayer(leavingPlayer.uuid());
        assertFalse(game.jumperQueue().contains(leavingPlayer.uuid()), "The player should no longer be in the queue");

        // Simulate a few jumps to make sure nothing breaks
        game.verdict(JumpVerdict.LANDED);
        assertNotEquals(leavingPlayer.uuid(), game.currentJumper());
        this.plugin.getScheduler().nextTick();
        game.verdict(JumpVerdict.LANDED);
        assertNotEquals(leavingPlayer.uuid(), game.currentJumper());
    }

    @Test
    public void leaveArenaAtCountdown_shouldEndTask() {
        Arena arena = this.newArena(ThimbleGameMode.CONCURRENT);
        assertFalse(arena.game().isPresent(), "The game should not be initialized.");
        UUID player1 = this.plugin.addPlayer().uuid();
        UUID player2 = this.plugin.addPlayer().uuid();
        arena.addPlayer(player1);
        assertTrue(arena.game().isPresent(), "The game should be initialized.");
        arena.addPlayer(player2);
        this.plugin.getScheduler().nextTick();

        arena.removePlayer(player2, false);
        assertTrue(arena.game().isPresent(), "The instance of the game should still exist.");
        assertEquals(ThimbleState.STARTING, arena.game().get().state());
        arena.removePlayer(player1, false);
        assertFalse(arena.game().isPresent(), "The game should no longer be referenced.");
        this.plugin.getScheduler().assertNothingScheduled();
    }

    @Test
    public void leaveArena_shouldEnd() {
        Arena arena = this.newArena(ThimbleGameMode.SINGLE);
        List<PlayerMock> players = this.joinThreePlayers(arena);
        Game game = (Game) arena.game().get();
        game.onCountdownEnd();
        game.setState(ThimbleState.PLAYING);
        arena.removePlayer(players.get(0).uuid(), false);
        arena.removePlayer(players.get(1).uuid(), false);
        assertEquals(ThimbleState.END, arena.game().get().state());
    }

    @Test
    public void leaveArena_shouldEnd_oneSpectator() {
        Arena arena = this.newArena(ThimbleGameMode.SINGLE);
        List<PlayerMock> players = this.joinThreePlayers(arena);
        SingleGame game = (SingleGame) arena.game().get();
        game.onCountdownEnd();
        game.setState(ThimbleState.PLAYING);
        this.plugin.getScheduler().nextTick();

        UUID jumper = game.currentJumper();
        assertNotNull(jumper, "The game should start with one player jumping.");
        game.verdict(jumper, JumpVerdict.MISSED);

        // One of the players still alive leaves.
        arena.removePlayer(players.get(0).uuid().equals(jumper) ? players.get(1).uuid() : players.get(0).uuid(), false);
        assertEquals(ThimbleState.END, arena.game().get().state());
    }

    private @NotNull Arena newArena(@NotNull ThimbleGameMode gameMode) {
        Arena arena = new Arena(this.plugin, "test");
        WorldKey world = MockUtil.randomKey();
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
            assertEquals(arena.spawnLocation(), players.get(i).getLocation());
        }
        return players;
    }

    /**
     * Verifies that the player makes a successful jump while in the water.
     *
     * @param game The player's game.
     * @param down The height of the water block where the player will land.
     * @return The player who jumped.
     */
    private @NotNull UUID assertLanded(@NotNull SingleGame game, @Range(from = 1, to = Integer.MAX_VALUE) int down) {
        UUID jumperUniqueId = game.currentJumper();
        assertNotNull(jumperUniqueId, "A player must jump.");
        InGamePlayer inGamePlayer = game.getPlayer(jumperUniqueId);
        assertNotNull(inGamePlayer, "The player who jumps must have an instance of InGamePlayer.");
        Player player = requireNonNull(this.plugin.getPlayer(jumperUniqueId), "player");
        int prevJumps = inGamePlayer.jumps();

        // Sets the player's location.
        assertEquals(game.arena().jumpLocation(), player.getLocation(), "The player must be at the jump location.");
        Location location = game.arena().jumpLocation().down(down);
        BlockVector pos = location.asBlockPosition();
        this.plugin.getWorld().put(pos, BlockDataMock.WATER);
        player.teleport(location);

        // The player must have scored.
        this.plugin.getScheduler().nextTick();
        assertEquals(game.arena().waitLocation(), player.getLocation(), "The player should be teleported at the waiting location.");
        assertEquals(prevJumps + 1, inGamePlayer.jumps(), "The jump counter should be updated.");
        assertEquals(inGamePlayer.getChosenBlock(), this.plugin.getBlockData(pos), "The player's block should be placed.");
        assertFalse(inGamePlayer.isSpectator(), "The player must not be a spectator.");
        assertTrue(game.alivePlayers().stream().anyMatch(p -> p.uuid().equals(jumperUniqueId)), "The player should be in the set of alive players.");
        this.plugin.getScheduler().nextTick();

        return jumperUniqueId;
    }
}
