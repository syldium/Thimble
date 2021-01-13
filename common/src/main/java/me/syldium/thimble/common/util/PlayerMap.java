package me.syldium.thimble.common.util;

import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.player.PlayerAudience;
import me.syldium.thimble.common.player.MessageKey;
import me.syldium.thimble.common.player.Player;
import me.syldium.thimble.common.player.media.TimedMedia;
import me.syldium.thimble.common.service.MessageService;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Template;
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
        this.media = TimedMedia.from(plugin.getMainConfig(), "global");
    }

    public boolean add(@NotNull E player) {
        if (!player.isVanished()) this.size++;
        return this.put(player.uuid(), player) == null;
    }

    public E get(@NotNull Player player) {
        return this.get(player.uuid());
    }

    public E remove(@NotNull UUID uuid) {
        E removed = this.remove((Object) uuid);
        if (removed != null && !removed.isVanished()) {
            this.size--;
        }
        Player player = this.plugin.getPlayer(uuid);
        if (player != null) {
            this.media.hide(player);
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

    public void sendMessage(@NotNull MessageKey messageKey, @NotNull Template... templates) {
        MessageService messageService = this.plugin.getMessageService();
        Component component = messageService.prefix().append(messageService.formatMessage(messageKey, templates));
        for (Player player : this.audiences()) player.sendMessage(component);
    }

    public void sendMessage(@NotNull MessageKey messageKey, @NotNull E from, @NotNull Template... templates) {
        MessageService messageService = this.plugin.getMessageService();
        Component component = messageService.prefix().append(messageService.formatMessage(messageKey, templates));
        for (E identity : this) {
            // The player who sent the message should not see the message, and a vanished player should not be visible in the message.
            if (identity.uuid().equals(from.uuid()) || !from.isVanished() || identity.isVanished()) {
                continue;
            }

            Player player = this.plugin.getPlayer(identity.uuid());
            if (player != null) {
                player.sendMessage(component);
            }
        }
    }

    public void sendActionBar(@NotNull MessageKey messageKey, @NotNull Template... templates) {
        Component component = this.plugin.getMessageService().formatMessage(messageKey, templates);
        for (Player player : this.audiences()) player.sendActionBar(component);
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

    public void progress(float progress, int time) {
        this.media.progress(this, progress, time);
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

    public @NotNull Stream<E> stream() {
        return this.values().stream();
    }
}
