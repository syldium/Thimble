package me.syldium.thimble.bukkit.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.syldium.thimble.api.Ranking;
import me.syldium.thimble.common.player.AbstractPlayer;
import me.syldium.thimble.common.player.Player;
import me.syldium.thimble.common.service.ExternalPlaceholderProvider;
import me.syldium.thimble.common.service.PlaceholderService;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * <p><a href="https://github.com/PlaceholderAPI/PlaceholderAPI">PlaceholderAPI</a></p>
 *
 * <p>Registers placeholders for the leaderboard, see the README.</p>
 */
class ThimbleExpansion extends PlaceholderExpansion implements PlaceholderService {

    private final ExternalPlaceholderProvider provider;
    private final List<String> placeholders;

    ThimbleExpansion(@NotNull ExternalPlaceholderProvider provider, @NotNull Consumer<@NotNull PlaceholderService> consumer) {
        this.provider = provider;
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
        return this.provider.provide(player.getUniqueId(), params);
    }

    @Override
    public @NotNull List<String> getPlaceholders() {
        return this.placeholders;
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
        return "1.2.0";
    }

    @SuppressWarnings("unchecked")
    public @NotNull String setPlaceholders(@NotNull Player player, @NotNull String text) {
        return PlaceholderAPI.setPlaceholders(((AbstractPlayer<org.bukkit.entity.Player>) player).getHandle(), text);
    }
}
