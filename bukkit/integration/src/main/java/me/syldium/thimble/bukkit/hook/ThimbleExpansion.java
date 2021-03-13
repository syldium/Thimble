package me.syldium.thimble.bukkit.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.syldium.thimble.api.Ranking;
import me.syldium.thimble.api.util.RankingPosition;
import me.syldium.thimble.api.player.ThimblePlayerStats;
import me.syldium.thimble.api.service.StatsService;
import me.syldium.thimble.common.player.AbstractPlayer;
import me.syldium.thimble.common.player.Player;
import me.syldium.thimble.common.service.PlaceholderService;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * <p><a href="https://github.com/PlaceholderAPI/PlaceholderAPI">PlaceholderAPI</a></p>
 *
 * <p>Registers placeholders for the leaderboard, see the README.</p>
 */
class ThimbleExpansion extends PlaceholderExpansion implements PlaceholderService {

    private static final Pattern DELIMITER = Pattern.compile("_");

    private final StatsService service;
    private final List<String> placeholders;

    ThimbleExpansion(@NotNull StatsService service, @NotNull Consumer<@NotNull PlaceholderService> consumer) {
        this.service = service;
        this.register();
        this.placeholders = new ArrayList<>(Ranking.values().length * 2);
        for (Ranking ranking : Ranking.values()) {
            String placeholder = this.getIdentifier() + "_lb_" + ranking.name().toLowerCase(Locale.ROOT);
            this.placeholders.add(placeholder);
            this.placeholders.add(placeholder + "_name");
        }
        consumer.accept(this);
    }

    @Override
    public @Nullable String onRequest(@NotNull OfflinePlayer player, @NotNull String params) {
        String[] tokens = DELIMITER.split(params);
        if (tokens.length < 2 || tokens.length > 4) {
            return null;
        }

        if ("lb".equals(tokens[0])) {
            RankingPosition rankingPosition = this.parseRankingPosition(tokens[1], tokens.length > 2 ? tokens[2] : "0");
            if (rankingPosition == null) {
                return null;
            }

            boolean requestUsername = tokens.length > 3 && "name".equals(tokens[3]);
            return this.formatPlayerStats(this.service.getLeaderboard(rankingPosition), rankingPosition.ranking(), requestUsername);
        }

        return null;
    }

    @Override
    public @NotNull List<String> getPlaceholders() {
        return this.placeholders;
    }

    private @Nullable RankingPosition parseRankingPosition(@NotNull String ranking, @NotNull String position) {
        try {
            return new RankingPosition(Ranking.valueOf(ranking.toUpperCase(Locale.ROOT)), Integer.parseInt(position));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private @NotNull String formatPlayerStats(@Nullable ThimblePlayerStats stats, @NotNull Ranking ranking, boolean username) {
        if (stats == null) {
            return username ? "player" : "0";
        }
        return username ? stats.name() : String.valueOf(ranking.get(stats));
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "thimble";
    }

    @Override
    public @NotNull String getAuthor() {
        return "syldium";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @SuppressWarnings("unchecked")
    public @NotNull String setPlaceholders(@NotNull Player player, @NotNull String text) {
        return PlaceholderAPI.setPlaceholders(((AbstractPlayer<org.bukkit.entity.Player>) player).getHandle(), text);
    }
}
