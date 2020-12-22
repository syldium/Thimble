package me.syldium.thimble.common.config;

import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Saves the state of the players before a game (inventory, life...)
 *
 * @param <P> The player type.
 */
public abstract class SavedPlayersManager<P> {

    private final Map<UUID, SavedPlayer<P>> savedPlayers = new HashMap<>();
    private final Set<UUID> pending = new HashSet<>();
    private final @Nullable File saveDirectory;
    private final Executor executor;

    public SavedPlayersManager(@NotNull ThimblePlugin plugin) {
        this.saveDirectory = plugin.getMainConfig().doesSaveStatesInFile() ? new File(plugin.getDataFolder(), "saves") : null;
        if (this.saveDirectory != null) {
            this.saveDirectory.mkdirs();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.saveDirectory.toPath().toAbsolutePath())) {
                for (Path path : stream) {
                    if (!Files.isDirectory(path)) {
                        String filename = path.getFileName().toString();
                        this.pending.add(UUID.fromString(filename.substring(0, filename.lastIndexOf('.'))));
                    }
                }
            } catch (IOException | IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }
        this.executor = this.saveDirectory == null ? null : Executors.newSingleThreadExecutor();
    }

    /**
     * Saves the player's state in memory and, if configured, in a file.
     *
     * <p>Note that the save is done synchronously and therefore will be directly available in memory.</p>
     *
     * @param player The player.
     * @return When the data is saved.
     */
    @SuppressWarnings("unchecked")
    public @NotNull CompletableFuture<@NotNull SavedPlayer<P>> save(@NotNull Player player) {
        SavedPlayer<P> saved = this.create((P) player.getPlugin().getPlayerAdapter().asPlatform(player));
        UUID uuid = player.uuid();
        this.savedPlayers.put(uuid, saved);
        if (this.saveDirectory != null) {
            return CompletableFuture.supplyAsync(() -> {
                saved.save(this.getPlayerFile(uuid));
                return saved;
            }, this.executor);
        }
        return CompletableFuture.completedFuture(saved);
    }

    /**
     * Load player information from a file.
     *
     * @param file The save file. {@link File#exists()} = true
     * @return A {@link SavedPlayer}, if it exists
     */
    protected abstract @Nullable SavedPlayer<P> load(@NotNull File file);

    /**
     * Creates a new snapshot of the player's state.
     *
     * @param player The player.
     * @return The snapshot.
     */
    protected abstract @NotNull SavedPlayer<P> create(@NotNull P player);

    /**
     * Restores the state of the player.
     *
     * @param player The player.
     */
    public void restore(@NotNull Player player) {
        UUID uuid = player.uuid();
        SavedPlayer<P> saved = this.savedPlayers.get(uuid);
        if (saved != null) {
            saved.restore(player);
            this.savedPlayers.remove(uuid);
            if (this.saveDirectory != null) {
                this.delete(uuid);
            }
        }
    }

    /**
     * Loads a save of the player's state.
     *
     * @param uuid The player's unique identifier.
     * @return The save, if so.
     */
    public @NotNull CompletableFuture<@NotNull Optional<@NotNull SavedPlayer<P>>> getInventorySave(@NotNull UUID uuid) {
        SavedPlayer<P> saved = this.savedPlayers.get(uuid);
        if (saved != null) {
            return CompletableFuture.completedFuture(Optional.of(saved));
        }

        return CompletableFuture.supplyAsync(() -> {
            File file = this.getPlayerFile(uuid);
            if (!file.exists()) {
                return Optional.empty();
            }
            return Optional.ofNullable(this.load(file));
        }, this.executor);
    }

    /**
     * Returns the set of {@link UUID}s of the saves that should be reapplied as soon as possible.
     *
     * @return The set.
     */
    public @NotNull Set<@NotNull UUID> getPending() {
        return this.pending;
    }

    /**
     * Deletes a player's file.
     *
     * @param uuid The player's unique identifier.
     */
    public void delete(@NotNull UUID uuid) {
        this.executor.execute(() -> this.getPlayerFile(uuid).delete());
    }

    private @NotNull File getPlayerFile(@NotNull UUID uuid) {
        return new File(this.saveDirectory, uuid.toString() + ".dat");
    }
}
