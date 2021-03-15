package me.syldium.thimble.common.service;

import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.config.ConfigNode;
import me.syldium.thimble.common.player.Placeholder;
import me.syldium.thimble.common.player.Player;
import me.syldium.thimble.common.player.media.Scoreboard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
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

import static net.kyori.adventure.text.minimessage.Tokens.TAG_END;
import static net.kyori.adventure.text.minimessage.Tokens.TAG_START;

@VisibleForTesting
public class ScoreboardServiceImpl implements ScoreboardService {

    private final List<Set<Placeholder>> indexes;
    private final Map<Placeholder, List<Integer>> placeholders;
    private final Map<Placeholder, Component> emptyTexts;
    private final Map<UUID, Scoreboard> scoreboards;
    private final Function<UUID, String> uuidToString;
    private final Component title;
    private final List<String> lines;

    public ScoreboardServiceImpl(
            @NotNull Function<UUID, String> uuidToString,
            @NotNull String title,
            @NotNull List<@NotNull String> lines,
            @Nullable ConfigNode emptyTexts
    ) {
        this.uuidToString = uuidToString;
        this.title = MiniMessage.get().parse(title);
        this.lines = lines;

        if (lines.isEmpty()) {
            this.indexes = Collections.emptyList();
            this.placeholders = Collections.emptyMap();
            this.emptyTexts = Collections.emptyMap();
            this.scoreboards = Collections.emptyMap();
            return;
        }

        Placeholder[] placeholders = Placeholder.values();
        this.indexes = new ArrayList<>(lines.size());
        this.placeholders = new EnumMap<>(Placeholder.class);
        this.emptyTexts = new EnumMap<>(Placeholder.class);
        this.scoreboards = new IdentityHashMap<>();
        for (int i = 0; i < lines.size(); i++) {
            Set<Placeholder> set = EnumSet.noneOf(Placeholder.class);
            for (Placeholder placeholder : placeholders) {
                if (lines.get(i).contains(TAG_START + placeholder.asString() + TAG_END)) {
                    set.add(placeholder);
                    this.placeholders.computeIfAbsent(placeholder, s -> new ArrayList<>(2)).add(i);
                }
            }
            this.indexes.add(set);
        }

        Component defNullText = MiniMessage.get().parse(emptyTexts == null ? "null" : emptyTexts.getString("default", "null"));
        for (Placeholder placeholder : placeholders) {
            String defText = emptyTexts == null ? null : emptyTexts.getString(placeholder.asString());
            this.emptyTexts.put(placeholder, defText == null ? defNullText : MiniMessage.get().parse(defText));
        }
    }

    public ScoreboardServiceImpl(@NotNull ThimblePlugin plugin, @NotNull ConfigNode config) {
        this(plugin::getPlayerName, config.getString("title", "<blue>Thimble</blue>"), config.getStringList("lines"), config.getNode("empty"));
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
    public void updateScoreboard(@NotNull Iterable<@NotNull ? extends ThimblePlayer> inGamePlayers, @NotNull Placeholder... placeholders) {
        Set<Integer> linesToUpdate = this.linesWithPlaceholders(placeholders);
        if (linesToUpdate.isEmpty()) {
            return;
        }

        for (ThimblePlayer player : inGamePlayers) {
            this.updateLines(player, linesToUpdate);
        }
    }

    @Override
    public void updateScoreboard(@NotNull ThimblePlayer player, @NotNull Placeholder... placeholders) {
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
        Set<Placeholder> placeholders = this.indexes.get(line);
        Template[] templates = new Template[placeholders.size()];
        int p = 0;
        for (Placeholder placeholder : placeholders) {
            Object result = placeholder.apply(this.uuidToString, player, this.placeholders.get(placeholder).indexOf(line));
            templates[p++] = result == null ?
                    Template.of(placeholder.asString(), this.emptyTexts.get(placeholder))
                    : Template.of(placeholder.asString(), String.valueOf(result));
        }
        return MiniMessage.get().parse(this.lines.get(line), templates);
    }

    /**
     * Returns the lines with these placeholders.
     *
     * @param placeholders The placeholders to look for.
     * @return The lines containing these placeholders.
     */
    private @NotNull Set<@NotNull Integer> linesWithPlaceholders(@NotNull Placeholder... placeholders) {
        if (placeholders.length == 0) {
            return Collections.emptySet();
        }

        Set<Integer> linesToUpdate = new HashSet<>();
        for (Placeholder placeholder : placeholders) {
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
