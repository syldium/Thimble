package me.syldium.thimble.mock.service;

import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.common.player.Placeholder;
import me.syldium.thimble.common.player.Player;
import me.syldium.thimble.common.service.ScoreboardService;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ScoreboardServiceMock implements ScoreboardService {
    @Override
    public void showScoreboard(@NotNull ThimblePlayer inGamePlayer, @NotNull Player player) {

    }

    @Override
    public void updateScoreboard(@NotNull Iterable<@NotNull ? extends ThimblePlayer> inGamePlayers, @NotNull Placeholder... placeholders) {

    }

    @Override
    public void updateScoreboard(@NotNull ThimblePlayer inGamePlayer, @NotNull Placeholder... placeholders) {

    }

    @Override
    public void hideScoreboard(@NotNull ThimblePlayer inGamePlayer, @Nullable Player player) {

    }

    @Override
    public @NotNull List<@NotNull Component> render(@NotNull ThimblePlayer player) {
        return Collections.emptyList();
    }
}
