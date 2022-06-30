package me.syldium.thimble.common.configurate4;

import me.syldium.thimble.common.config.NodeEntry;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Iterator;
import java.util.Map.Entry;

class ConfigurateNodeIterator implements Iterator<NodeEntry> {

    private final Iterator<? extends Entry<Object, ? extends ConfigurationNode>> children;

    ConfigurateNodeIterator(@NotNull ConfigurationNode node) {
        this.children = node.childrenMap().entrySet().iterator();
    }

    @Override
    public boolean hasNext() {
        return this.children.hasNext();
    }

    @Override
    public NodeEntry next() {
        Entry<Object, ? extends ConfigurationNode> entry = this.children.next();
        return new NodeEntry(String.valueOf(entry.getKey()), new ConfigurateConfigNode(entry.getValue()));
    }
}
