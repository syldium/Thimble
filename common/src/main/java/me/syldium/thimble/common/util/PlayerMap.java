package me.syldium.thimble.common.util;

import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.player.PlayerAudience;
import me.syldium.thimble.common.player.MessageKey;
import me.syldium.thimble.common.player.Player;
import me.syldium.thimble.common.player.ThimblePlaceholder;
import me.syldium.thimble.common.player.media.TimedMedia;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public class PlayerMap<E extends ThimblePlayer> extends HashMap<UUID, E> implements PlayerAudience, ForwardingAudience, Iterable<E> {

    private final ThimblePlugin plugin;
    private final TimedMedia media;
    private transient int size = 0; // Without invisible players

    public PlayerMap(@NotNull ThimblePlugin plugin) {
        this.plugin = plugin;
        this.media = TimedMedia.from(plugin.getMainConfig(), plugin.getMessageService(), "global");
    }

    public boolean add(@NotNull E player) {
        boolean added = this.put(player.uuid(), player) == null;
        if (added && !player.isVanished()) {
            this.size++;
        }
        return added;
    }

    public E get(@NotNull Player player) {
        return this.get(player.uuid());
    }

    public E remove(@NotNull UUID uuid) {
        E removed = this.remove((Object) uuid);
        if (removed != null && !removed.isVanished()) {
            this.size--;
        }
        return removed;
    }

    @Override
    public int size() {
        return this.size;
    }

    public int realSize() {
        return super.size();
    }

    @Override
    public void clear() {
        super.clear();
        this.size = 0;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    public boolean isReallyEmpty() {
        return super.isEmpty();
    }

    public @NotNull Set<UUID> uuidSet() {
        return this.keySet();
    }

    public @NotNull Set<E> playerSet() {
        return new HashSet<>(this.values());
    }

    public boolean contains(UUID uuid) {
        return this.containsKey(uuid);
    }

    public boolean contains(E identity) {
        return this.containsValue(identity);
    }

    public void updateAllScoreboards(@NotNull ThimblePlaceholder... placeholders) {
        this.plugin.getScoreboardService().updateScoreboard(this, placeholders);
    }

    public void sendMessage(@NotNull MessageKey messageKey) {
        this.sendMessage(Identity.nil(), messageKey, TagResolver.empty());
    }

    public void sendMessage(@NotNull MessageKey messageKey, @NotNull TagResolver... placeholders) {
        this.sendMessage(Identity.nil(), messageKey, placeholders);
    }

    public void sendMessage(@NotNull Identity source, @NotNull MessageKey messageKey, @NotNull TagResolver placeholders) {
        Component component = this.plugin.getMessageService().formatMessageWithPrefix(messageKey, placeholders);
        for (Player player : this.audiences()) player.sendMessage(source, component);
    }

    public void sendMessage(@NotNull Identity source, @NotNull MessageKey messageKey, @NotNull TagResolver... placeholders) {
        this.sendMessage(source, messageKey, TagResolver.resolver(placeholders));
    }

    public void sendMessage(@NotNull E source, @NotNull MessageKey messageKey, @NotNull TagResolver placeholders) {
        if (!source.isVanished()) {
            this.sendMessage((Identity) source, messageKey, placeholders);
        }
    }

    public void sendMessage(@NotNull E source, @NotNull MessageKey messageKey, @NotNull TagResolver... placeholders) {
        this.sendMessage(source, messageKey, TagResolver.resolver(placeholders));
    }

    public void sendMessageExclude(@NotNull E source, @NotNull MessageKey messageKey, @NotNull TagResolver... placeholders) {
        Component component = this.plugin.getMessageService().formatMessageWithPrefix(messageKey, placeholders);
        for (E identity : this) {
            // The player who sent the message should not see the message, and a vanished player should not be visible in the message.
            if (identity.uuid().equals(source.uuid()) || (source.isVanished() && !identity.isVanished())) {
                continue;
            }

            Player player = this.plugin.getPlayer(identity.uuid());
            if (player != null) {
                player.sendMessage(source, component);
            }
        }
    }

    public void sendActionBar(@NotNull MessageKey messageKey) {
        this.sendActionBar(messageKey, TagResolver.empty());
    }

    public void sendActionBar(@NotNull MessageKey messageKey, @NotNull TagResolver placeholders) {
        Component component = this.plugin.getMessageService().formatMessage(messageKey, placeholders);
        for (Player player : this.audiences()) player.sendActionBar(component);
    }

    public void sendActionBar(@NotNull MessageKey messageKey, @NotNull TagResolver... placeholders) {
        this.sendActionBar(messageKey, TagResolver.resolver(placeholders));
    }

    @Override
    public @NotNull Iterable<Player> audiences() {
        List<Player> players = new LinkedList<>();
        for (E identity : this) {
            Player player = this.plugin.getPlayer(identity.uuid());
            if (player != null) {
                players.add(player);
            }
        }
        return players;
    }

    @Override
    public @NotNull Iterator<E> iterator() {
        return this.values().iterator();
    }

    public void progress(int ticks, int total) {
        this.media.progress(this, ticks, total);
    }

    public void hide() {
        this.media.hide(this);
    }

    @Override
    public void sendExperienceChange(float percent, int level) {
        for (PlayerAudience expHolder : this.audiences()) expHolder.sendExperienceChange(percent, level);
    }

    public void hideMedia(@NotNull PlayerAudience audience) {
        this.media.hide(audience);
    }

    public @NotNull Stream<E> stream() {
        return this.values().stream();
    }
}
