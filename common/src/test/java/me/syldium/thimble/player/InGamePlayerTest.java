package me.syldium.thimble.player;

import me.syldium.thimble.common.player.InGamePlayer;
import me.syldium.thimble.common.player.PlayerStats;
import me.syldium.thimble.mock.player.PlayerMock;
import me.syldium.thimble.mock.util.BlockDataMock;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InGamePlayerTest {

    @Test
    @SuppressWarnings("ConstantConditions")
    public void usePlayerName() {
        UUID playerId = UUID.randomUUID();
        PlayerMock playerMock = new PlayerMock(null, "online", playerId);
        InGamePlayer player = new InGamePlayer(playerMock, new PlayerStats(playerId, "database"), BlockDataMock.ONE, null);
        assertEquals(playerMock.name(), player.name(), "The player's name should be used.");
    }
}
