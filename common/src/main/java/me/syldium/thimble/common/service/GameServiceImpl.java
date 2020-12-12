package me.syldium.thimble.common.service;

import me.syldium.thimble.api.arena.ThimbleArena;
import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.api.service.GameService;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.config.ArenaConfig;
import me.syldium.thimble.common.game.Arena;
import me.syldium.thimble.common.game.Game;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class GameServiceImpl implements GameService {

    private final ThimblePlugin plugin;
    private final Map<UUID, Game> games = new ConcurrentHashMap<>();
    private final Set<Arena> arenas = new CopyOnWriteArraySet<>();
    private final ArenaConfig arenaConfig;

    public GameServiceImpl(@NotNull ThimblePlugin plugin, @NotNull ArenaConfig arenaConfig) {
        this.plugin = plugin;
        this.arenaConfig = arenaConfig;
        this.arenas.addAll(this.arenaConfig.load());
    }

    @Override
    public @NotNull Optional<@NotNull ThimbleGame> getGame(@NotNull UUID uuid) {
        return Optional.ofNullable(this.games.get(uuid));
    }

    @Override
    public @NotNull Optional<@NotNull ThimblePlayer> getInGamePlayer(@NotNull UUID uuid) {
        Game game = this.games.get(uuid);
        return game == null ? Optional.empty() : Optional.ofNullable(game.getPlayer(uuid));
    }

    @Override
    public @Nullable Arena createArena(@NotNull String name) {
        Arena arena = new Arena(this.plugin, name);
        return this.arenas.add(arena) ? arena : null;
    }

    @Override
    public @NotNull Optional<@NotNull ThimbleArena> getArena(@NotNull String name) {
        for (ThimbleArena arena : this.arenas) {
            if (arena.getName().equals(name)) {
                return Optional.of(arena);
            }
        }
        return Optional.empty();
    }

    @Override
    public @NotNull Set<@NotNull ThimbleArena> getArenas() {
        return Collections.unmodifiableSet(this.arenas);
    }

    @Override
    public void removeArena(@NotNull ThimbleArena arena) {
        // noinspection SuspiciousMethodCalls
        this.arenas.remove(arena);
    }

    public void setPlayerGame(@NotNull UUID player, @Nullable Game game) {
        if (game == null) {
            this.games.remove(player);
        } else {
            this.games.put(player, game);
        }
    }

    public void save() {
        this.arenaConfig.save(this.arenas);
    }
}
