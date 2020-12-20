package me.syldium.thimble.common.service;

import me.syldium.thimble.api.util.BlockPos;
import me.syldium.thimble.api.arena.ThimbleArena;
import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.api.service.GameService;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.config.ArenaConfig;
import me.syldium.thimble.common.game.Arena;
import me.syldium.thimble.common.game.Game;
import me.syldium.thimble.common.util.SignAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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

    private Map<BlockPos, SignAction> signsToAction;
    private final Map<BlockPos, ThimbleArena> signsToArena = new HashMap<>();
    private final Map<ThimbleArena, Set<BlockPos>> arenaToSigns = new HashMap<>();

    public GameServiceImpl(@NotNull ThimblePlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        ArenaConfig config = this.plugin.getConfigManager().getArenaConfig();
        this.arenas.addAll(config.load());
        this.signsToAction = config.loadActionSigns();
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

        Set<BlockPos> positions = this.arenaToSigns.remove(arena);
        if (positions == null) {
            return;
        }
        for (BlockPos position : positions) {
            this.signsToArena.remove(position);
        }
    }

    public void addSign(@NotNull BlockPos position, @NotNull ThimbleArena arena) {
        this.arenaToSigns.computeIfAbsent(arena, s -> new HashSet<>())
                .add(position);
        this.signsToArena.put(position, arena);
    }

    public void addSign(@NotNull BlockPos position, @NotNull SignAction action) {
        this.signsToAction.put(position, action);
    }

    public void addSigns(@NotNull Iterable<BlockPos> positions, @NotNull ThimbleArena arena) {
        for (BlockPos pos : positions) this.addSign(pos, arena);
    }

    public void removeSign(@NotNull BlockPos position) {
        ThimbleArena arena = this.signsToArena.remove(position);
        if (arena != null) {
            this.arenaToSigns.get(arena).remove(position);
        } else {
            this.signsToAction.remove(position);
        }
    }

    @Override
    public @NotNull Optional<@NotNull ThimbleArena> getArenaFromSign(@NotNull BlockPos position) {
        return Optional.ofNullable(this.signsToArena.get(position));
    }

    public @NotNull Optional<@NotNull SignAction> getActionFromSign(@NotNull BlockPos position) {
        return Optional.ofNullable(this.signsToAction.get(position));
    }

    @Override
    public @NotNull Set<@NotNull BlockPos> getArenaSigns(@NotNull ThimbleArena arena) {
        Set<BlockPos> positions = this.arenaToSigns.get(arena);
        return positions == null ? Collections.emptySet() : Collections.unmodifiableSet(positions);
    }

    @Override
    public @NotNull Set<@NotNull BlockPos> getActionSigns() {
        return Collections.unmodifiableSet(this.signsToAction.keySet());
    }

    public void setPlayerGame(@NotNull UUID player, @Nullable Game game) {
        if (game == null) {
            this.games.remove(player);
        } else {
            this.games.put(player, game);
        }
    }

    public void save() {
        ArenaConfig config = this.plugin.getConfigManager().getArenaConfig();
        config.save(this.arenas);
        Map<SignAction, Set<BlockPos>> reversed = new HashMap<>();
        for (Map.Entry<BlockPos, SignAction> entry : this.signsToAction.entrySet()) {
            reversed.computeIfAbsent(entry.getValue(), s -> new HashSet<>()).add(entry.getKey());
        }
        config.save(reversed);
        config.save();
    }
}
