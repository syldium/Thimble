package me.syldium.thimble.sponge.config;

import me.syldium.thimble.common.config.ConfigFile;
import me.syldium.thimble.common.config.ConfigManager;
import me.syldium.thimble.common.configurate4.ConfigurateConfigFile;
import me.syldium.thimble.sponge.ThSpongePlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;

public class SpongeConfigManager extends ConfigManager<ThSpongePlugin> {

    private ConfigurationNode config;

    public SpongeConfigManager(@NotNull ThSpongePlugin plugin) {
        super(plugin);
    }

    @Override
    protected @NotNull ConfigFile getConfig(@NotNull File file) {
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().file(file).build();

        ConfigurationNode root;
        try {
            root = loader.load();
        } catch (IOException ex) {
            this.severe("Unable to load the config file!", ex);
            root = loader.createNode();
        }
        if ("config.conf".equals(file.getName())) {
            this.config = root;
        }
        return new ConfigurateConfigFile(loader, root, this.plugin.getLogger());
    }

    public @NotNull ConfigurationNode getConfig() {
        return this.config;
    }

    @Override
    protected @NotNull String getFileExtension() {
        return "conf";
    }
}
