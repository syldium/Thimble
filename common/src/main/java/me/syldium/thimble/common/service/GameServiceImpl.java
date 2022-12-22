package me.syldium.thimble.common.service;

import me.syldium.thimble.api.service.ArenaScoreCalculator;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
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
    public @NotNull Optional<@NotNull ThimbleGame> playerGame(@NotNull UUID uuid) {
        return Optional.ofNullable(this.games.get(uuid));
    }

    @Override
    public @NotNull Optional<@NotNull ThimblePlayer> player(@NotNull UUID uuid) {
        Game game = this.games.get(uuid);
        return game == null ? Optional.empty() : Optional.ofNullable(game.player(uuid));
    }

    @Override
    public @Nullable Arena createArena(@NotNull String name) {
        Arena arena = new Arena(this.plugin, name);
        return this.arenas.add(arena) ? arena : null;
    }

    @Override
    public @NotNull Optional<@NotNull ThimbleArena> arena(@NotNull String name) {
        for (ThimbleArena arena : this.arenas) {
            if (arena.name().equals(name)) {
                return Optional.of(arena);
            }
        }
        return Optional.empty();
    }

    @Override
    public @NotNull Set<@NotNull ThimbleArena> arenas() {
        return Collections.unmodifiableSet(this.arenas);
    }

    public @NotNull String arenaCount() {
        int count = this.plugin.getGameService().arenas().size();
        if (count < 1) return "0";
        if (count < 3) return "1-2";
        if (count < 6) return "3-5";
        return "6+";
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
    public @NotNull Optional<@NotNull ThimbleArena> arenaFromSign(@NotNull BlockPos position) {
        return Optional.ofNullable(this.signsToArena.get(position));
    }

    public @NotNull Optional<@NotNull SignAction> getActionFromSign(@NotNull BlockPos position) {
        return Optional.ofNullable(this.signsToAction.get(position));
    }

    @Override
    public @NotNull Set<@NotNull BlockPos> arenaSigns(@NotNull ThimbleArena arena) {
        Set<BlockPos> positions = this.arenaToSigns.get(arena);
        return positions == null ? Collections.emptySet() : Collections.unmodifiableSet(positions);
    }

    @Override
    public @NotNull Set<@NotNull BlockPos> actionSigns() {
        return Collections.unmodifiableSet(this.signsToAction.keySet());
    }

    @Override
    public @NotNull Optional<@NotNull ThimbleArena> findAvailableArena(@NotNull ArenaScoreCalculator selection, int playersCount, @NotNull Random random) {
        // Start with an empty best arenas list
        List<ThimbleArena> selectedArenas = new ArrayList<>();
        int bestScore = Integer.MAX_VALUE;

        for (ThimbleArena arena : this.arenas) {
            // Skip arena if it's not setup
            if (!arena.isSetup()) {
                continue;
            }

            // Skip arena if it cannot afford that number of new players
            Optional<ThimbleGame> game = arena.game();
            if (game.isPresent() && !game.get().acceptPlayers(playersCount)) {
                continue;
            }

            // Determine the appropriation score of this arena
            int score = selection.calculateScore(arena, playersCount);

            // Update the best arena if the current arena has a better score
            if (score < bestScore) {
                bestScore = score;
                selectedArenas.clear();
                selectedArenas.add(arena);
            } else if (score == bestScore) {
                selectedArenas.add(arena);
            }
        }

        if (selectedArenas.isEmpty()) {
            return Optional.empty();
        }
        // Select a random arena
        int index = random.nextInt(selectedArenas.size());
        return Optional.of(selectedArenas.get(index));
    }

    public void setPlayerGame(@NotNull UUID player, @Nullable Game game) {
        if (game == null) {
            this.games.remove(player);
        } else {
            this.games.put(player, game);
        }
    }

    public void save() {
        this.save(this.plugin.getConfigManager().getArenaConfig());
    }

    public void save(@NotNull ArenaConfig config) {
        config.save(this.arenas);
        Map<SignAction, Set<BlockPos>> reversed = new HashMap<>();
        for (Map.Entry<BlockPos, SignAction> entry : this.signsToAction.entrySet()) {
            reversed.computeIfAbsent(entry.getValue(), s -> new HashSet<>()).add(entry.getKey());
        }
        config.save(reversed);
        config.save();
    }
}
