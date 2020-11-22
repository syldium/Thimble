package me.syldium.decoudre.sponge;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import me.syldium.decoudre.api.Location;
import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.config.ArenaConfig;
import me.syldium.decoudre.common.game.Arena;
import me.syldium.decoudre.common.util.Task;
import me.syldium.decoudre.sponge.adapter.SpongePlayerAdapter;
import me.syldium.decoudre.sponge.command.SpongeCommandExecutor;
import me.syldium.decoudre.sponge.config.serializer.ArenaSerializer;
import me.syldium.decoudre.sponge.config.SpongeArenaConfig;
import me.syldium.decoudre.sponge.config.SpongeMainConfig;
import me.syldium.decoudre.sponge.config.serializer.LocationSerializer;
import me.syldium.decoudre.sponge.util.LoggerWrapper;
import me.syldium.decoudre.sponge.util.SpongeTask;
import net.kyori.adventure.platform.spongeapi.SpongeAudiences;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.Server;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStoppingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.LiteralText;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Plugin(
        id = "decoudre",
        name = "DeCoudre"
)
public class DeSpongePlugin extends DeCoudrePlugin {

    private LoggerWrapper logger;

    @Inject
    private Game game;

    @Inject
    private PluginContainer container;

    private SpongeAudiences audiences;

    private SpongePlayerAdapter platform;

    private ArenaConfig arenaConfig;

    @Inject @DefaultConfig(sharedRoot = true)
    private Path configDir;

    @Inject @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;

    @Inject @ConfigDir(sharedRoot = false)
    private File dataFolder;

    @Listener @SuppressWarnings("UnstableApiUsage")
    public void onEnable(GameInitializationEvent event) throws IOException {
        this.saveDefaultConfig();
        this.logger = new LoggerWrapper(this.container.getLogger());
        this.audiences = SpongeAudiences.create(this.container, this.game);
        this.platform = new SpongePlayerAdapter(this, this.audiences);

        this.game.getCommandManager().register(this, new SpongeCommandExecutor(this), "dac");
        TypeSerializerCollection.defaults().register(TypeToken.of(Arena.class), new ArenaSerializer(this));
        TypeSerializerCollection.defaults().register(TypeToken.of(Location.class), new LocationSerializer());
        this.arenaConfig = new SpongeArenaConfig(getFile("arenas.conf"), this.getSlf4jLogger());

        this.enable(new SpongeMainConfig(this.configManager, this.getSlf4jLogger()));
    }

    @Listener
    public void onRightClickEntity(InteractBlockEvent event, @Root Player player){
        if (event.getInteractionPoint().isPresent()) {
            Vector3d v = event.getInteractionPoint().get();
            BlockState state = player.getWorld().getBlock((int) Math.floor(v.getX()), (int) v.getY(), (int) Math.floor(v.getZ()));
            player.sendMessage(LiteralText.of(state.getApplicableProperties()));
        }
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

    @Override
    public @NotNull ArenaConfig getArenaConfig() {
        return this.arenaConfig;
    }

    @Override
    public @NotNull File getDataFolder() {
        return this.dataFolder;
    }

    @Override
    public @NotNull Task startGameTask(@NotNull Runnable runnable) {
        return new SpongeTask(this.game.getScheduler().createTaskBuilder().execute(runnable).interval(1000 / 20, TimeUnit.MILLISECONDS).submit(this));
    }

    @Override
    public @Nullable me.syldium.decoudre.common.player.Player getPlayer(@NotNull UUID uuid) {
        return this.getServer().getPlayer(uuid).map(this.platform::asAbstractPlayer).orElse(null);
    }

    @Override
    public @NotNull SpongePlayerAdapter getPlayerAdapter() {
        return this.platform;
    }

    public @NotNull Server getServer() {
        return this.game.getServer();
    }

    public @NotNull GameRegistry getRegistry() {
        return this.game.getRegistry();
    }

    private void saveDefaultConfig() throws IOException {
        if (!Files.exists(this.configDir)) {
            this.game.getAssetManager().getAsset(this, "default.conf").get().copyToFile(this.configDir);
        }
    }
}
