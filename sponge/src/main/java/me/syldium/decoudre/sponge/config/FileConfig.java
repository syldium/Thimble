package me.syldium.decoudre.sponge.config;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.IOException;

public abstract class FileConfig<NodeType extends ConfigurationNode> {

    protected final ConfigurationLoader<NodeType> loader;
    protected final NodeType root;
    protected final Logger logger;

    FileConfig(@NotNull ConfigurationLoader<NodeType> loader, @NotNull Logger logger) {
        this.loader = loader;
        this.logger = logger;

        NodeType rootNode;
        try {
            rootNode = loader.load();
        } catch (IOException ex) {
            logger.error("Unable to load the config file!", ex);
            rootNode = loader.createEmptyNode();
        }
        this.root = rootNode;
    }

    protected void save() {
        try {
            this.loader.save(this.root);
        } catch (IOException ex) {
            this.logger.error("Could not save config to file.", ex);
        }
    }
}
