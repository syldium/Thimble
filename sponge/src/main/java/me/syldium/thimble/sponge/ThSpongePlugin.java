package me.syldium.thimble.sponge;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;

import java.util.Optional;
import java.util.UUID;
import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.api.service.GameService;
import me.syldium.thimble.api.service.StatsService;
import me.syldium.thimble.api.sponge.SpongeAdapter;
import me.syldium.thimble.api.util.PluginVersion;
import me.syldium.thimble.common.logging.Slf4jLogger;
import me.syldium.thimble.common.util.ServerType;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.CommandManager;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.util.Fireworks;
import me.syldium.thimble.common.util.Task;
import me.syldium.thimble.sponge.adapter.SpongeEventAdapter;
import me.syldium.thimble.sponge.adapter.SpongePlayerAdapter;
import me.syldium.thimble.sponge.command.SpongeCommandExecutor;
import me.syldium.thimble.sponge.config.SpongeConfigManager;
import me.syldium.thimble.sponge.config.SpongeSavedPlayersManager;
import me.syldium.thimble.sponge.listener.DamageListener;
import me.syldium.thimble.sponge.listener.ReloadListener;
import me.syldium.thimble.sponge.listener.RestrictionListener;
import me.syldium.thimble.sponge.listener.SpongeConnectionListener;
import me.syldium.thimble.sponge.listener.SpongeMoveListener;
import me.syldium.thimble.sponge.util.SpongeFireworks;
import me.syldium.thimble.sponge.util.SpongeTask;
import net.kyori.adventure.platform.spongeapi.SpongeAudiences;
import net.kyori.adventure.util.Ticks;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.Server;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStoppingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.service.user.UserStorageService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import static me.syldium.thimble.common.util.MinecraftVersion.setVersion;

@Plugin(
        id = "thimble",
        name = "Thimble",
        version = "@version@",
        description = "@description@",
        url = "@url@",
        authors = {"syldium"}
)
public class ThSpongePlugin extends ThimblePlugin {

    private Slf4jLogger logger;

    @Inject
    private Game game;

    @Inject
    private PluginContainer container;

    private SpongeAudiences audiences;
    private SpongeEventAdapter eventAdapter;
    private SpongePlayerAdapter playerAdapter;
    private SpongeConfigManager configManager;
    private SpongeSavedPlayersManager savedPlayersManager;
    private SpongeExecutorService syncExecutor;

    @Inject @DefaultConfig(sharedRoot = true)
    private Path configDir;

    @Inject @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> configLoader;

    @Inject @ConfigDir(sharedRoot = false)
    private File dataFolder;

    private SpongeCommandExecutor commandManager;

    private Function<UUID, Optional<String>> playerNameFunction;

    @Listener
    public void onEnable(GameInitializationEvent event) throws IOException {
        this.saveDefaultConfig();
        setVersion(new PluginVersion(1, 12, 2));
        this.logger = new Slf4jLogger(this.container.getLogger());
        this.audiences = SpongeAudiences.create(this.container, this.game);

        this.commandManager = new SpongeCommandExecutor(this);
        this.game.getCommandManager().register(this, this.commandManager, "thimble", "th");
        this.configManager = new SpongeConfigManager(this);
        this.savedPlayersManager = new SpongeSavedPlayersManager(this);
        this.syncExecutor = this.game.getScheduler().createSyncExecutor(this.container);

        this.enable();

        this.getServiceManager().setProvider(this.container, StatsService.class, this.getStatsService());
        this.getServiceManager().setProvider(this.container, GameService.class, this.getGameService());
        this.eventAdapter = new SpongeEventAdapter(this.container);
        this.playerAdapter = new SpongePlayerAdapter(this, this.audiences);

        Optional<UserStorageService> userServiceOpt = this.game.getServiceManager().provide(UserStorageService.class);
        // noinspection OptionalIsPresent
        if (userServiceOpt.isPresent()) {
            this.playerNameFunction = uuid -> userServiceOpt.get().get(uuid).map(User::getName);
        } else {
            this.playerNameFunction = uuid -> this.game.getServer().getPlayer(uuid).map(Player::getName);
        }

        new DamageListener(this);
        new ReloadListener(this);
        new RestrictionListener(this);
        this.reloadables.add(new SpongeConnectionListener(this));
        this.reloadables.add(new SpongeMoveListener(this));

        if (this.configManager.getConfig().getNode("update-checker").getBoolean(true)) {
            this.game.getScheduler().createTaskBuilder()
                    .async()
                    .execute(this.getUpdateChecker())
                    .submit(this);
        }
    }

    @Override
    public @NotNull String getPlayerName(@NotNull UUID uuid) {
        return this.playerNameFunction.apply(uuid).orElse("invalid-player");
    }

    @Override
    public void executeGameEndCommands(@NotNull ThimblePlayer winner) {
        try {
            for (String command : this.configManager.getConfig().getNode("commands-at-end").getList(TypeToken.of(String.class))) {
                this.game.getCommandManager().process(this.getServer().getConsole(), command.replace(WINNER_TAG, winner.name()));
            }
        } catch (ObjectMappingException ex) {
            ex.printStackTrace();
        }
    }

    @Listener
    public void onDisable(final GameStoppingEvent event) {
        this.disable();
        this.audiences.close();
        SpongeAdapter.unregister();
    }

    @Override
    public @NotNull Slf4jLogger getLogger() {
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
    public @NotNull Path getPluginPath() {
        return this.container.getSource().orElseThrow(() -> new RuntimeException("A Path was excepted"));
    }

    @Override
    public @NotNull ServerType getServerType() {
        return ServerType.SPONGE;
    }

    @Override
    public @NotNull Task startGameTask(@NotNull Runnable runnable) {
        return new SpongeTask(this.game.getScheduler().createTaskBuilder().execute(runnable).interval(Ticks.SINGLE_TICK_DURATION_MS * 2, TimeUnit.MILLISECONDS).submit(this));
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
    public boolean isLoaded(@NotNull Location location) {
        return true;
    }

    @Override
    public void runSync(@NotNull Runnable runnable) {
        this.game.getScheduler().createTaskBuilder().execute(runnable).submit(this);
    }

    @Override
    public <T> @NotNull CompletableFuture<T> runSync(@NotNull Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, this.syncExecutor);
    }

    @Override
    protected @NotNull String getPluginFolder() {
        return "mods/";
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
