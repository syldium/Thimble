package me.syldium.thimble.mock.config;

import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.config.SavedPlayer;
import me.syldium.thimble.common.config.SavedPlayersManager;
import me.syldium.thimble.mock.player.PlayerMock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class SavedPlayersManagerMock extends SavedPlayersManager<PlayerMock> {

    public SavedPlayersManagerMock(@NotNull ThimblePlugin plugin) {
        super(plugin);
    }

    @Override
    protected @Nullable SavedPlayer<PlayerMock> load(@NotNull File file) {
        return null;
    }

    @Override
    protected @NotNull SavedPlayerMock create(@NotNull PlayerMock player) {
        return new SavedPlayerMock();
    }

    static class SavedPlayerMock implements SavedPlayer<PlayerMock> {

        @Override
        public void save(@NotNull File file) {

        }

        @Override
        public void restore(@NotNull PlayerMock player, boolean restoreInventory, boolean withLocation) {
            player.restoreInventory();
        }
    }
}
