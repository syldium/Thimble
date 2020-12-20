package me.syldium.thimble.bukkit.config;

import me.syldium.thimble.common.config.ConfigNode;
import me.syldium.thimble.common.config.NodeEntry;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;

class BukkitNodeIterator implements Iterator<NodeEntry> {

    private final Iterator<Map.Entry<String, Object>> keys;

    BukkitNodeIterator(@NotNull ConfigurationSection section) {
        this.keys = section.getValues(false).entrySet().iterator();
    }

    @Override
    public boolean hasNext() {
        return this.keys.hasNext();
    }

    @Override
    public NodeEntry next() {
        Map.Entry<String, Object> entry = this.keys.next();
        ConfigNode node = entry.getValue() instanceof ConfigurationSection ?
                new BukkitConfigNode((ConfigurationSection) entry.getValue())
                : new BukkitObjectConfigNode(entry.getKey(), entry.getValue());
        return new NodeEntry(entry.getKey(), node);
    }
}
