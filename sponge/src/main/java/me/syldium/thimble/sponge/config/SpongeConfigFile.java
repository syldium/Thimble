package me.syldium.thimble.sponge.config;

import me.syldium.thimble.common.config.ConfigFile;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;

import java.io.IOException;

class SpongeConfigFile extends SpongeConfigNode implements ConfigFile {

    private final ConfigurationLoader<?> loader;

    SpongeConfigFile(@NotNull ConfigurationLoader<?> loader, @NotNull ConfigurationNode node) {
        super(node);
        this.loader = loader;
    }

    @Override
    public void save() {
        try {
            this.loader.save(this.parent);
        } catch (IOException ex) {
            Sponge.getPluginManager().getPlugin("thimble").get().getLogger().error("Could not save config to file.", ex);
        }
    }
}
