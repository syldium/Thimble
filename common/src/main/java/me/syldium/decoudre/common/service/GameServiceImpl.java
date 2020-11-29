package me.syldium.decoudre.common.service;

import me.syldium.decoudre.api.arena.DeArena;
import me.syldium.decoudre.api.arena.DeGame;
import me.syldium.decoudre.api.player.DePlayer;
import me.syldium.decoudre.api.service.GameService;
import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.config.ArenaConfig;
import me.syldium.decoudre.common.game.Arena;
import me.syldium.decoudre.common.game.Game;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameServiceImpl implements GameService {

    private final DeCoudrePlugin plugin;
    private final Map<UUID, Game> games = new ConcurrentHashMap<>();
    private final Set<Arena> arenas = Collections.synchronizedSet(new HashSet<>());
    private final ArenaConfig arenaConfig;

    public GameServiceImpl(@NotNull DeCoudrePlugin plugin, @NotNull ArenaConfig arenaConfig) {
        this.plugin = plugin;
        this.arenaConfig = arenaConfig;
        this.arenas.addAll(this.arenaConfig.load());
    }

    @Override
    public @NotNull Optional<@NotNull DeGame> getGame(@NotNull UUID uuid) {
        return Optional.ofNullable(this.games.get(uuid));
    }

    @Override
    public @NotNull Optional<@NotNull DePlayer> getInGamePlayer(@NotNull UUID uuid) {
        Game game = this.games.get(uuid);
        return game == null ? Optional.empty() : Optional.ofNullable(game.getPlayer(uuid));
    }

    @Override
    public @Nullable Arena createArena(@NotNull String name) {
        Arena arena = new Arena(this.plugin, name);
        return this.arenas.add(arena) ? arena : null;
    }

    @Override
    public @NotNull Optional<@NotNull DeArena> getArena(@NotNull String name) {
        for (DeArena arena : this.arenas) {
            if (arena.getName().equals(name)) {
                return Optional.of(arena);
            }
        }
        return Optional.empty();
    }

    @Override
    public @NotNull Set<@NotNull DeArena> getArenas() {
        return Collections.unmodifiableSet(this.arenas);
    }

    @Override
    public void removeArena(@NotNull DeArena arena) {
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
