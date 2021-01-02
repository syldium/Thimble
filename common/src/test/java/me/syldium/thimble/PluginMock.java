package me.syldium.thimble;

import me.syldium.thimble.mock.adpater.EventAdapterMock;
import me.syldium.thimble.mock.adpater.PlayerAdapterMock;
import me.syldium.thimble.mock.config.ConfigurateManager;
import me.syldium.thimble.mock.config.SavedPlayersManagerMock;
import me.syldium.thimble.mock.player.PlayerMock;
import me.syldium.thimble.mock.TickScheduler;
import me.syldium.thimble.api.Location;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.adapter.EventAdapter;
import me.syldium.thimble.common.adapter.PlayerAdapter;
import me.syldium.thimble.common.command.CommandManager;
import me.syldium.thimble.common.config.ConfigManager;
import me.syldium.thimble.common.config.SavedPlayersManager;
import me.syldium.thimble.common.player.Player;
import me.syldium.thimble.common.util.Fireworks;
import me.syldium.thimble.common.util.Task;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class PluginMock extends ThimblePlugin {

    private final File dataFolder;
    private final Map<UUID, PlayerMock> players;
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
        this.playerAdapter = new PlayerAdapterMock();
        this.savedPlayersManager = new SavedPlayersManagerMock(this);
        this.enable();

        this.getMainConfig().getGameNode().setValue("countdown-time", 1);
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
        return null;
    }

    @Override
    public @NotNull CommandManager getCommandManager() {
        return this.commandManager;
    }

    @Override
    public @Nullable Player getPlayer(@NotNull UUID uuid) {
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

    @Override
    public @NotNull EventAdapter<?> getEventAdapter() {
        return this.eventAdapter;
    }

    @Override
    public @NotNull PlayerAdapter<?, ?> getPlayerAdapter() {
        return this.playerAdapter;
    }

    @Override
    public @NotNull ConfigManager<? extends ThimblePlugin> getConfigManager() {
        return this.configManager;
    }

    @Override
    public @NotNull SavedPlayersManager<?> getSavedPlayersManager() {
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
}
