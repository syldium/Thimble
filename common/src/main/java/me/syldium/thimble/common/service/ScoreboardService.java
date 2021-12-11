package me.syldium.thimble.common.service;

import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.common.player.ThimblePlaceholder;
import me.syldium.thimble.common.player.Player;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * This service manages the players' scoreboards.
 */
public interface ScoreboardService {

    /**
     * Shows a custom scoreboard for this player.
     *
     * @param inGamePlayer The player in a thimble game.
     * @param player The connected player (should be the same player).
     */
    void showScoreboard(@NotNull ThimblePlayer inGamePlayer, @NotNull Player player);

    /**
     * Updates the lines containing these placeholders.
     *
     * @param inGamePlayers The players in the same arena.
     * @param placeholders Placeholders to update.
     */
    void updateScoreboard(@NotNull Iterable<@NotNull ? extends ThimblePlayer> inGamePlayers, @NotNull ThimblePlaceholder... placeholders);

    /**
     * Updates the lines containing these placeholders.
     *
     * @param inGamePlayer The player in a thimble game.
     * @param placeholders Placeholders to update.
     */
    void updateScoreboard(@NotNull ThimblePlayer inGamePlayer, @NotNull ThimblePlaceholder... placeholders);

    void hideScoreboard(@NotNull ThimblePlayer inGamePlayer, @Nullable Player player);

    /**
     * Renders the whole scoreboard.
     *
     * @param player The player in a thimble game.
     * @return The rendered lines.
     */
    @NotNull List<@NotNull Component> render(@NotNull ThimblePlayer player);
}
