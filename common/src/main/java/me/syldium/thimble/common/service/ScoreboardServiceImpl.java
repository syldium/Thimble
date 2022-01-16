package me.syldium.thimble.common.service;

import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.config.ConfigNode;
import me.syldium.thimble.common.player.ThimblePlaceholder;
import me.syldium.thimble.common.player.Player;
import me.syldium.thimble.common.player.media.Scoreboard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.placeholder.Placeholder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import static me.syldium.thimble.common.player.ThimblePlaceholder.TAG_END;
import static me.syldium.thimble.common.player.ThimblePlaceholder.TAG_START;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.minimessage.placeholder.Placeholder.component;
import static net.kyori.adventure.text.minimessage.placeholder.Placeholder.miniMessage;
import static net.kyori.adventure.text.minimessage.placeholder.PlaceholderResolver.placeholders;

@VisibleForTesting
public class ScoreboardServiceImpl implements ScoreboardService {

    private final PlaceholderService.Thimble placeholderService;
    private final List<Set<ThimblePlaceholder>> indexes;
    private final Map<ThimblePlaceholder, List<Integer>> placeholders;
    private final Map<ThimblePlaceholder, Component> emptyTexts;
    private final Map<UUID, Scoreboard> scoreboards;
    private final Function<UUID, String> uuidToString;
    private final Component title;
    private final List<String> lines;

    public ScoreboardServiceImpl(
            @NotNull Function<UUID, String> uuidToString,
            @NotNull PlaceholderService.Thimble placeholderService,
            @NotNull String title,
            @NotNull List<@NotNull String> lines,
            @Nullable ConfigNode emptyTexts
    ) {
        this.uuidToString = uuidToString;
        this.placeholderService = placeholderService;
        this.title = miniMessage().deserialize(title);
        this.lines = lines;

        if (lines.isEmpty()) {
            this.indexes = Collections.emptyList();
            this.placeholders = Collections.emptyMap();
            this.emptyTexts = Collections.emptyMap();
            this.scoreboards = Collections.emptyMap();
            return;
        }

        ThimblePlaceholder[] placeholders = ThimblePlaceholder.values();
        this.indexes = new ArrayList<>(lines.size());
        this.placeholders = new EnumMap<>(ThimblePlaceholder.class);
        this.emptyTexts = new EnumMap<>(ThimblePlaceholder.class);
        this.scoreboards = new IdentityHashMap<>();
        for (int i = 0; i < lines.size(); i++) {
            Set<ThimblePlaceholder> set = EnumSet.noneOf(ThimblePlaceholder.class);
            for (ThimblePlaceholder placeholder : placeholders) {
                if (lines.get(i).contains(TAG_START + placeholder.asString() + TAG_END)) {
                    set.add(placeholder);
                    this.placeholders.computeIfAbsent(placeholder, s -> new ArrayList<>(2)).add(i);
                }
            }
            this.indexes.add(set);
        }

        Component defNullText = miniMessage().deserialize(emptyTexts == null ? "null" : emptyTexts.getString("default", "null"));
        for (ThimblePlaceholder placeholder : placeholders) {
            String defText = emptyTexts == null ? null : emptyTexts.getString(placeholder.asString());
            this.emptyTexts.put(placeholder, defText == null ? defNullText : miniMessage().deserialize(defText));
        }
    }

    public ScoreboardServiceImpl(@NotNull ThimblePlugin plugin, @NotNull ConfigNode config) {
        this(plugin::getPlayerName, plugin.getMessageService(), config.getString("title", "<blue>Thimble</blue>"), config.getStringList("lines"), config.getNode("empty"));
    }

    public ScoreboardServiceImpl(@NotNull String title, @NotNull List<@NotNull String> lines) {
        this(UUID::toString, (player, text) -> text, title, lines, null);
    }

    @Override
    public void showScoreboard(@NotNull ThimblePlayer inGamePlayer, @NotNull Player player) {
        Scoreboard scoreboard = this.scoreboards.get(inGamePlayer.uuid());
        if (scoreboard == null) {
            scoreboard = Scoreboard.scoreboard(this.title, this.render(inGamePlayer));
            player.setScoreboard(scoreboard);
            this.scoreboards.put(inGamePlayer.uuid(), scoreboard);
        } else {
            scoreboard.lines(this.render(inGamePlayer));
        }
    }

    @Override
    public void updateScoreboard(@NotNull Iterable<@NotNull ? extends ThimblePlayer> inGamePlayers, @NotNull ThimblePlaceholder... placeholders) {
        Set<Integer> linesToUpdate = this.linesWithPlaceholders(placeholders);
        if (linesToUpdate.isEmpty()) {
            return;
        }

        for (ThimblePlayer player : inGamePlayers) {
            this.updateLines(player, linesToUpdate);
        }
    }

    @Override
    public void updateScoreboard(@NotNull ThimblePlayer player, @NotNull ThimblePlaceholder... placeholders) {
        this.updateLines(player, this.linesWithPlaceholders(placeholders));
    }

    @Override
    public void hideScoreboard(@NotNull ThimblePlayer inGamePlayer, @Nullable Player player) {
        Scoreboard scoreboard = this.scoreboards.remove(inGamePlayer.uuid());
        if (player != null && scoreboard != null) {
            player.hideScoreboard(scoreboard);
        }
    }

    @Override
    public @NotNull List<@NotNull Component> render(@NotNull ThimblePlayer player) {
        List<Component> view = new ArrayList<>(this.lines.size());
        for (int i = 0; i < this.indexes.size(); i++) {
            view.add(this.render(player, i));
        }
        return view;
    }

    /**
     * Renders the given line.
     *
     * @param player The player in a thimble game.
     * @param line The line index.
     * @return The rendered line.
     */
    private @NotNull Component render(@NotNull ThimblePlayer player, int line) {
        Set<ThimblePlaceholder> placeholders = this.indexes.get(line);
        Placeholder<?>[] placeholders1 = new Placeholder[placeholders.size()];
        int p = 0;
        for (ThimblePlaceholder placeholder : placeholders) {
            Object result = placeholder.apply(this.uuidToString, player, this.placeholders.get(placeholder).indexOf(line));
            placeholders1[p++] = result == null ?
                    component(placeholder.asString(), this.emptyTexts.get(placeholder))
                    : miniMessage(placeholder.asString(), String.valueOf(result));
        }
        return miniMessage().deserialize(this.placeholderService.setPlaceholders(player, this.lines.get(line)), placeholders(placeholders1));
    }

    /**
     * Returns the lines with these placeholders.
     *
     * @param placeholders The placeholders to look for.
     * @return The lines containing these placeholders.
     */
    private @NotNull Set<@NotNull Integer> linesWithPlaceholders(@NotNull ThimblePlaceholder... placeholders) {
        if (placeholders.length == 0) {
            return Collections.emptySet();
        }

        Set<Integer> linesToUpdate = new HashSet<>();
        for (ThimblePlaceholder placeholder : placeholders) {
            List<Integer> lines = this.placeholders.get(placeholder);
            if (lines != null) {
                linesToUpdate.addAll(lines);
            }
        }
        return linesToUpdate;
    }

    private void updateLines(@NotNull ThimblePlayer player, @NotNull Set<@NotNull Integer> linesToUpdate) {
        if (linesToUpdate.isEmpty()) {
            return;
        }

        Scoreboard scoreboard = this.scoreboards.get(player.uuid());
        if (scoreboard == null) {
            return;
        }
        for (int line : linesToUpdate) {
            scoreboard.updateLine(line, this.render(player, line));
        }
    }
}
