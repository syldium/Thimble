package me.syldium.thimble.sponge;

import com.google.inject.Inject;
import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.service.GameService;
import me.syldium.thimble.api.service.StatsService;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.CommandManager;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.util.Fireworks;
import me.syldium.thimble.common.util.Task;
import me.syldium.thimble.common.player.Player;
import me.syldium.thimble.sponge.adapter.SpongeEventAdapter;
import me.syldium.thimble.sponge.adapter.SpongePlayerAdapter;
import me.syldium.thimble.sponge.command.SpongeCommandExecutor;
import me.syldium.thimble.sponge.config.SpongeConfigManager;
import me.syldium.thimble.sponge.config.SpongeSavedPlayersManager;
import me.syldium.thimble.sponge.listener.DamageListener;
import me.syldium.thimble.sponge.listener.RestrictionListener;
import me.syldium.thimble.sponge.util.LoggerWrapper;
import me.syldium.thimble.sponge.util.SpongeFireworks;
import me.syldium.thimble.sponge.util.SpongeTask;
import net.kyori.adventure.platform.spongeapi.SpongeAudiences;
import net.kyori.adventure.util.Ticks;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.Server;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStoppingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.ServiceManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Plugin(
        id = "thimble",
        name = "Thimble"
)
public class ThSpongePlugin extends ThimblePlugin {

    private LoggerWrapper logger;

    @Inject
    private Game game;

    @Inject
    private PluginContainer container;

    private SpongeAudiences audiences;
    private SpongeEventAdapter eventAdapter;
    private SpongePlayerAdapter playerAdapter;
    private SpongeConfigManager configManager;
    private SpongeSavedPlayersManager savedPlayersManager;

    @Inject @DefaultConfig(sharedRoot = true)
    private Path configDir;

    @Inject @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> configLoader;

    @Inject @ConfigDir(sharedRoot = false)
    private File dataFolder;

    private SpongeCommandExecutor commandManager;

    @Listener
    public void onEnable(GameInitializationEvent event) throws IOException {
        this.saveDefaultConfig();
        this.logger = new LoggerWrapper(this.container.getLogger());
        this.audiences = SpongeAudiences.create(this.container, this.game);

        this.commandManager = new SpongeCommandExecutor(this);
        this.game.getCommandManager().register(this, this.commandManager, "thimble", "th");
        this.configManager = new SpongeConfigManager(this);
        this.savedPlayersManager = new SpongeSavedPlayersManager(this);

        this.enable();

        this.getServiceManager().setProvider(this.container, StatsService.class, this.getStatsService());
        this.getServiceManager().setProvider(this.container, GameService.class, this.getGameService());
        this.eventAdapter = new SpongeEventAdapter(this.container);
        this.playerAdapter = new SpongePlayerAdapter(this, this.audiences);

        new DamageListener(this);
        new RestrictionListener(this);
    }

    @Listener
    public void onDisable(final GameStoppingEvent event) {
        this.disable();
    }

    @Override
    public @NotNull LoggerWrapper getLogger() {
        return this.logger;
    }

    public @NotNull Logger getSlf4jLogger() {
        return this.container.getLogger();
    }

    public @NotNull ConfigurationLoader<CommentedConfigurationNode> getConfigLoader() {
        return this.configLoader;
    }

    @Override
    public @NotNull File getDataFolder() {
        return this.dataFolder;
    }

    @Override
    public @NotNull Task startGameTask(@NotNull Runnable runnable) {
        return new SpongeTask(this.game.getScheduler().createTaskBuilder().execute(runnable).interval(Ticks.SINGLE_TICK_DURATION_MS, TimeUnit.MILLISECONDS).submit(this));
    }

    @Override
    public @NotNull Fireworks spawnFireworks(@NotNull Location from) {
        return new SpongeFireworks(this.playerAdapter.asPlatform(from));
    }

    @Override
    public @NotNull CommandManager getCommandManager() {
        return this.commandManager;
    }

    @Override
    public @Nullable Player getPlayer(@NotNull UUID uuid) {
        return this.getServer().getPlayer(uuid).map(this.playerAdapter::asAbstractPlayer).orElse(null);
    }

    @Override
    public @NotNull SpongeEventAdapter getEventAdapter() {
        return this.eventAdapter;
    }

    @Override
    public @NotNull SpongePlayerAdapter getPlayerAdapter() {
        return this.playerAdapter;
    }

    public @NotNull SpongeConfigManager getConfigManager() {
        return this.configManager;
    }

    @Override
    public @NotNull SpongeSavedPlayersManager getSavedPlayersManager() {
        return this.savedPlayersManager;
    }

    @Override
    public void runSync(@NotNull Runnable runnable) {
        this.game.getScheduler().createTaskBuilder().execute(runnable).submit(this);
    }

    public void sendFeedback(@NotNull org.spongepowered.api.entity.living.player.Player spongePlayer, @NotNull CommandResult result) {
        this.playerAdapter.asAbstractPlayer(spongePlayer).sendFeedback(result);
    }

    public @NotNull Server getServer() {
        return this.game.getServer();
    }

    public @NotNull GameRegistry getRegistry() {
        return this.game.getRegistry();
    }

    public @NotNull ServiceManager getServiceManager() {
        return this.game.getServiceManager();
    }

    public void registerListeners(@NotNull Object listener) {
        this.game.getEventManager().registerListeners(this, listener);
    }

    private void saveDefaultConfig() throws IOException {
        if (!Files.exists(this.configDir)) {
            this.game.getAssetManager().getAsset(this, "default.conf").get().copyToFile(this.configDir);
        }
    }
}
