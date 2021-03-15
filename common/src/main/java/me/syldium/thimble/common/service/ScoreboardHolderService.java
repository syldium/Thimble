package me.syldium.thimble.common.service;

import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.config.ConfigFile;
import me.syldium.thimble.common.config.ConfigNode;
import me.syldium.thimble.common.player.Placeholder;
import me.syldium.thimble.common.player.Player;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ScoreboardHolderService implements ScoreboardService {

    private final ThimblePlugin plugin;
    private final @Nullable ConfigNode config;
    private final @Nullable ScoreboardService defaultScoreboard;
    private final Map<String, ScoreboardService> arenaNameToScoreboard;

    public ScoreboardHolderService(@NotNull ThimblePlugin plugin) {
        this.plugin = plugin;

        ConfigFile config = plugin.getConfigManager().getScoreboardConfig();
        if (config == null || !config.getChildren().iterator().hasNext()) {
            this.config = null;
            this.defaultScoreboard = null;
            this.arenaNameToScoreboard = Collections.emptyMap();
            return;
        }

        this.config = config;
        ConfigNode defaultNode = config.getNode("default");
        this.defaultScoreboard = defaultNode == null ? null : new ScoreboardServiceImpl(plugin, defaultNode);
        this.arenaNameToScoreboard = new HashMap<>();
    }

    @Override
    public void showScoreboard(@NotNull ThimblePlayer inGamePlayer, @NotNull Player player) {
        ScoreboardService scoreboard = this.scoreboard(inGamePlayer);
        if (scoreboard != null) {
            scoreboard.showScoreboard(inGamePlayer, player);
        }
    }

    @Override
    public void updateScoreboard(@NotNull Iterable<@NotNull ? extends ThimblePlayer> inGamePlayers, @NotNull Placeholder... placeholders) {
        Iterator<? extends ThimblePlayer> iterator = inGamePlayers.iterator();
        if (!iterator.hasNext()) {
            return;
        }

        ScoreboardService scoreboard = this.scoreboard(iterator.next());
        if (scoreboard != null) {
            scoreboard.updateScoreboard(inGamePlayers, placeholders);
        }
    }

    @Override
    public void updateScoreboard(@NotNull ThimblePlayer inGamePlayer, @NotNull Placeholder... placeholders) {
        ScoreboardService scoreboard = this.scoreboard(inGamePlayer);
        if (scoreboard != null) {
            scoreboard.updateScoreboard(inGamePlayer, placeholders);
        }
    }

    @Override
    public void hideScoreboard(@NotNull ThimblePlayer inGamePlayer, @Nullable Player player) {
        ScoreboardService scoreboard = this.scoreboard(inGamePlayer);
        if (scoreboard != null) {
            scoreboard.hideScoreboard(inGamePlayer, player);
        }
    }

    @Override
    public @NotNull List<@NotNull Component> render(@NotNull ThimblePlayer player) {
        return this.defaultScoreboard == null ? Collections.emptyList() : this.defaultScoreboard.render(player);
    }

    private @Nullable ScoreboardService scoreboard(@NotNull ThimblePlayer player) {
        if (this.config == null) {
            return null;
        }
        return this.arenaNameToScoreboard.computeIfAbsent(player.game().arena().name(), this::buildService);
    }

    private @Nullable ScoreboardService buildService(@NotNull String scoreboardName) {
        // noinspection ConstantConditions
        ConfigNode node = this.config.getNode(scoreboardName);
        return node == null ? this.defaultScoreboard : new ScoreboardServiceImpl(this.plugin, node);
    }
}
