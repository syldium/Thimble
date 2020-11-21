package me.syldium.decoudre.common.util;

import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.player.MessageKey;
import me.syldium.decoudre.common.player.Player;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.identity.Identity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PlayerMap<E extends Identity> extends HashMap<UUID, E> implements ForwardingAudience, Iterable<E> {

    private final DeCoudrePlugin plugin;

    public PlayerMap(@NotNull DeCoudrePlugin plugin) {
        this.plugin = plugin;
    }

    public boolean add(@NotNull E player) {
        return this.put(player.uuid(), player) == null;
    }

    public E get(@NotNull Player player) {
        return this.get(player.uuid());
    }

    public boolean remove(UUID uuid) {
        return this.remove((Object) uuid) != null;
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

    public void sendMessage(@NotNull MessageKey messageKey) {
        for (Player player : this.audiences()) player.sendMessage(messageKey);
    }

    public void sendActionBar(@NotNull MessageKey messageKey) {
        for (Player player : this.audiences()) player.sendActionBar(messageKey);
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
}
