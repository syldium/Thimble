package me.syldium.thimble.sponge.config;

import me.syldium.thimble.common.config.ConfigManager;
import me.syldium.thimble.common.config.ConfigFile;
import me.syldium.thimble.sponge.ThSpongePlugin;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class SpongeConfigManager extends ConfigManager<ThSpongePlugin> {

    private ConfigurationNode config;

    public SpongeConfigManager(@NotNull ThSpongePlugin plugin) {
        super(plugin);
    }

    @Override
    protected @NotNull ConfigFile getConfig(@NotNull File file) {
        ConfigurationLoader<CommentedConfigurationNode> loader;
        if ("config.conf".equals(file.getName())) {
            loader = this.plugin.getConfigLoader();
        } else {
            loader = HoconConfigurationLoader.builder().setFile(file).build();
        }

        ConfigurationNode root;
        try {
            root = loader.load();
        } catch (IOException ex) {
            this.severe("Unable to load the config file!", ex);
            root = loader.createEmptyNode();
        }
        if ("config.conf".equals(file.getName())) {
            this.config = root;
        }
        return new SpongeConfigFile(loader, root);
    }

    public @NotNull ConfigurationNode getConfig() {
        return this.config;
    }

    @Override
    protected final @NotNull String getFileExtension() {
        return "conf";
    }
}
