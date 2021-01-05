package me.syldium.thimble;

import me.syldium.thimble.api.util.BlockVector;
import me.syldium.thimble.mock.adpater.EventAdapterMock;
import me.syldium.thimble.mock.adpater.PlayerAdapterMock;
import me.syldium.thimble.mock.config.ConfigurateManager;
import me.syldium.thimble.mock.config.SavedPlayersManagerMock;
import me.syldium.thimble.mock.player.PlayerMock;
import me.syldium.thimble.mock.TickScheduler;
import me.syldium.thimble.api.Location;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.CommandManager;
import me.syldium.thimble.common.util.Fireworks;
import me.syldium.thimble.common.util.Task;
import me.syldium.thimble.mock.util.BlockDataMock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class PluginMock extends ThimblePlugin {

    private final File dataFolder;
    private final Map<UUID, PlayerMock> players;
    private final Map<BlockVector, BlockDataMock> world = new HashMap<>();
    private final ConfigurateManager configManager;
    private final CommandManager commandManager;
    private final TickScheduler scheduler;
    private final EventAdapterMock eventAdapter;
    private final PlayerAdapterMock playerAdapter;
    private final SavedPlayersManagerMock savedPlayersManager;

    public PluginMock() throws IOException {
        this.dataFolder = Files.createTempDirectory("ThimbleTest-").toFile();
        this.dataFolder.deleteOnExit();
        this.players = new HashMap<>();
        this.configManager = new ConfigurateManager(this);
        this.commandManager = new CommandManager();
        this.scheduler = new TickScheduler();
        this.eventAdapter = new EventAdapterMock();
        this.playerAdapter = new PlayerAdapterMock(this);
        this.savedPlayersManager = new SavedPlayersManagerMock(this);
        this.enable();

        this.getMainConfig().getGameNode().setValue("countdown-time", 1);
        this.getMainConfig().getGameNode().setValue("end-time", 1);
        this.getMainConfig().getGameNode().setValue("jump-time-single", 1);
        this.getMainConfig().getGameNode().setValue("jump-time-concurrent", 1);
    }

    @Override
    public @NotNull Logger getLogger() {
        return Logger.getLogger("PluginMock");
    }

    @Override
    public @NotNull File getDataFolder() {
        return this.dataFolder;
    }

    @Override
    public @NotNull Task startGameTask(@NotNull Runnable runnable) {
        return this.scheduler.schedule(runnable);
    }

    @Override
    public @NotNull Fireworks spawnFireworks(@NotNull Location from) {
        return new Fireworks() {
            @Override
            public void spawn(int count) {

            }
        };
    }

    @Override
    public @NotNull CommandManager getCommandManager() {
        return this.commandManager;
    }

    @Override
    public @Nullable PlayerMock getPlayer(@NotNull UUID uuid) {
        return this.players.get(uuid);
    }

    public PlayerMock addPlayer() {
        UUID uuid = UUID.randomUUID();
        PlayerMock player = new PlayerMock(this, uuid.toString().substring(0, 16), uuid);
        this.players.put(uuid, player);
        return player;
    }

    public void removePlayer(@NotNull PlayerMock player) {
        this.players.remove(player.uuid());
    }

    public void removePlayer(@NotNull UUID uuid) {
        this.players.remove(uuid);
    }

    public void removeAllPlayers() {
        this.players.clear();
    }

    public @NotNull Collection<PlayerMock> getPlayers() {
        return this.players.values();
    }

    @Override
    public @NotNull EventAdapterMock getEventAdapter() {
        return this.eventAdapter;
    }

    @Override
    public @NotNull PlayerAdapterMock getPlayerAdapter() {
        return this.playerAdapter;
    }

    @Override
    public @NotNull ConfigurateManager getConfigManager() {
        return this.configManager;
    }

    @Override
    public @NotNull SavedPlayersManagerMock getSavedPlayersManager() {
        return this.savedPlayersManager;
    }

    @Override
    public void runSync(@NotNull Runnable runnable) {
        runnable.run();
    }

    @Override
    public @NotNull <T> CompletableFuture<T> runSync(@NotNull Supplier<T> supplier) {
        CompletableFuture<T> future = new CompletableFuture<>();
        future.complete(supplier.get());
        return future;
    }

    public @NotNull TickScheduler getScheduler() {
        return this.scheduler;
    }

    public @NotNull Map<BlockVector, BlockDataMock> getWorld() {
        return this.world;
    }

    public @Nullable BlockDataMock getBlockData(@NotNull BlockVector pos) {
        return this.world.get(pos);
    }
}
