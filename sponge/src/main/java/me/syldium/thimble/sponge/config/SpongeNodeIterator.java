package me.syldium.thimble.sponge.config;

import me.syldium.thimble.common.config.NodeEntry;
import ninja.leaping.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map.Entry;

class SpongeNodeIterator implements Iterator<NodeEntry> {

    private final Iterator<? extends Entry<Object, ? extends ConfigurationNode>> children;

    SpongeNodeIterator(@NotNull ConfigurationNode node) {
        this.children = node.getChildrenMap().entrySet().iterator();
    }

    @Override
    public boolean hasNext() {
        return this.children.hasNext();
    }

    @Override
    public NodeEntry next() {
        Entry<Object, ? extends ConfigurationNode> entry = this.children.next();
        return new NodeEntry(String.valueOf(entry.getKey()), new SpongeConfigNode(entry.getValue()));
    }
}
