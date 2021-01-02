package me.syldium.thimble.mock.config;

import me.syldium.thimble.PluginMock;
import me.syldium.thimble.common.config.ConfigFile;
import me.syldium.thimble.common.config.ConfigManager;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;

import java.io.File;
import java.io.IOException;

public class ConfigurateManager extends ConfigManager<PluginMock> {

    public ConfigurateManager(@NotNull PluginMock plugin) {
        super(plugin);
    }

    @Override
    protected @NotNull ConfigFile getConfig(@NotNull File file) {
        GsonConfigurationLoader loader = GsonConfigurationLoader.builder().file(file).build();
        ConfigurationNode root;
        try {
            root = loader.load();
        } catch (IOException ex) {
            this.severe("Unable to load the config file!", ex);
            root = loader.createNode();
        }
        return new ConfigurateConfigFile(loader, root);
    }

    @Override
    protected @NotNull String getFileExtension() {
        return "json";
    }
}
