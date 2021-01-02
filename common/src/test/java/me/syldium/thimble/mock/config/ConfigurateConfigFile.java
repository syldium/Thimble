package me.syldium.thimble.mock.config;

import me.syldium.thimble.common.config.ConfigFile;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

class ConfigurateConfigFile extends ConfigurateConfigNode implements ConfigFile {

    private final ConfigurationLoader<?> loader;

    ConfigurateConfigFile(@NotNull ConfigurationLoader<?> loader, @NotNull ConfigurationNode node) {
        super(node);
        this.loader = loader;
    }

    @Override
    public void save() {
        try {
            this.loader.save(this.parent);
        } catch (IOException ex) {
            Logger.getLogger("Configurate").log(Level.SEVERE, "Could not save config to file.", ex);
        }
    }
}
