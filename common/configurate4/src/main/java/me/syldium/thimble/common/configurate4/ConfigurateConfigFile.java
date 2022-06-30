package me.syldium.thimble.common.configurate4;

import me.syldium.thimble.common.config.ConfigFile;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigurateConfigFile extends ConfigurateConfigNode implements ConfigFile {

    private final ConfigurationLoader<?> loader;
    private final Logger logger;

    public ConfigurateConfigFile(@NotNull ConfigurationLoader<?> loader, @NotNull ConfigurationNode node, @NotNull Logger logger) {
        super(node);
        this.loader = loader;
        this.logger = logger;
    }

    @Override
    public void save() {
        try {
            this.loader.save(this.parent);
        } catch (IOException ex) {
            this.logger.log(Level.SEVERE, "Could not save config to file.", ex);
        }
    }
}
