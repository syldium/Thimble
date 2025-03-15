package me.syldium.thimble.sponge;

import com.google.inject.Inject;
import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.CommandManager;
import me.syldium.thimble.common.util.Fireworks;
import me.syldium.thimble.common.util.ServerType;
import me.syldium.thimble.sponge.adapter.SpongeEventAdapter;
import me.syldium.thimble.sponge.adapter.SpongePlayerAdapter;
import me.syldium.thimble.sponge.command.SpongeCommandExecutor;
import me.syldium.thimble.sponge.config.SpongeConfigManager;
import me.syldium.thimble.sponge.config.SpongeSavedPlayersManager;
import me.syldium.thimble.sponge.listener.DamageListener;
import me.syldium.thimble.sponge.util.LoggerWrapper;
import me.syldium.thimble.sponge.util.SpongeAdapter;
import me.syldium.thimble.sponge.util.SpongeTask;
import net.kyori.adventure.util.Ticks;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.scheduler.TaskExecutorService;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Plugin("thimble")
public class ThSpongePlugin extends ThimblePlugin {

    private final PluginContainer container;
    private final LoggerWrapper logger;

    private Game game;
    private Scheduler scheduler;
    private TaskExecutorService syncExecutor;

    private SpongePlayerAdapter adapter;
    private SpongeConfigManager config;
    private SpongeEventAdapter events;
    private SpongeCommandExecutor command;
    private SpongeSavedPlayersManager savedPlayers;

    @Inject @ConfigDir(sharedRoot = false)
    private Path dataFolder;

    @Inject
    ThSpongePlugin(PluginContainer container, Logger logger) {
        this.container = container;
        this.logger = new LoggerWrapper(logger);
    }

    @Listener
    public void onServerStarting(StartingEngineEvent<Server> event) {
        this.game = event.game();
        this.scheduler = this.game.server().scheduler();
        this.syncExecutor = this.scheduler.executor(this.container);
        this.adapter = new SpongePlayerAdapter(this);
        this.events = new SpongeEventAdapter();
        this.config = new SpongeConfigManager(this);
        this.savedPlayers = new SpongeSavedPlayersManager(this);

        new DamageListener(this);

        enable();
    }

    @Listener
    public void onServerStopping(StoppingEngineEvent<Server> event) {
        disable();
        SpongeAdapter.unregister();
    }

    @Override
    public @NotNull LoggerWrapper getLogger() {
        return this.logger;
    }

    @Override
    public @NotNull File getDataFolder() {
        return this.dataFolder.toFile();
    }

    @Override
    public @NotNull Path getPluginPath() {
        return this.dataFolder;
    }

    @Override
    public @NotNull ServerType getServerType() {
        return ServerType.SPONGE;
    }

    @Override
    public @NotNull SpongeTask startGameTask(@NotNull Runnable runnable) {
        return new SpongeTask(this.scheduler.submit(Task.builder()
                .execute(runnable)
                .interval(Ticks.SINGLE_TICK_DURATION_MS * 2, TimeUnit.MILLISECONDS)
                .plugin(this.container)
                .build()
        ));
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
        return this.command;
    }

    @Override
    public @NotNull String getPlayerName(@NotNull UUID uuid) {
        return this.game.server().player(uuid).map(Player::name).orElseThrow(RuntimeException::new);
    }

    @Override
    public void executeGameEndCommands(@NotNull ThimblePlayer winner) {

    }

    @Override
    public @NotNull SpongeEventAdapter getEventAdapter() {
        return this.events;
    }

    @Override
    public @NotNull SpongePlayerAdapter getPlayerAdapter() {
        return this.adapter;
    }

    @Override
    public @NotNull SpongeConfigManager getConfigManager() {
        return this.config;
    }

    @Override
    public @NotNull SpongeSavedPlayersManager getSavedPlayersManager() {
        return this.savedPlayers;
    }

    @Override
    public boolean isLoaded(@NotNull Location location) {
        return true;
    }

    @Override
    public void runSync(@NotNull Runnable runnable) {
        this.scheduler.submit(Task.builder().execute(runnable).build());
    }

    @Override
    public @NotNull <T> CompletableFuture<T> runSync(@NotNull Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, this.syncExecutor);
    }

    public @NotNull Game game() {
        return this.game;
    }

    public @NotNull Server server() {
        return this.game.server();
    }

    @Listener
    public void onRegisterCommands(RegisterCommandEvent<Command.Raw> event) {
        this.command = new SpongeCommandExecutor(this);
        event.register(this.container, this.command, "thimble", "th");
    }

    public void registerListeners(@NotNull Object listener) {
        this.game.eventManager().registerListeners(this.container, listener, MethodHandles.lookup());
    }
}
