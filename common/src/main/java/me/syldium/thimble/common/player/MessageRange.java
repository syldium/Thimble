package me.syldium.thimble.common.player;

import me.syldium.thimble.common.config.ConfigNode;
import me.syldium.thimble.common.config.NodeEntry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MessageRange {

    private final Entry[] entries;

    public MessageRange(@NotNull MessageKey message) {
        this.entries = new Entry[]{new Entry(Integer.MIN_VALUE, message)};
    }

    private MessageRange(Entry[] entries) {
        this.entries = entries;
    }

    public @NotNull MessageKey getMessage(int n) {
        for (Entry entry : this.entries) {
            if (n >= entry.aboveInclusive) {
                return entry.message;
            }
        }
        return this.entries[this.entries.length - 1].message;
    }

    @Contract("_, !null -> !null")
    public static @Nullable MessageRange of(@NotNull ConfigNode config, @Nullable MessageRange def) {
        final List<Entry> entries = new ArrayList<>();
        for (NodeEntry entry : config.getChildren()) {
            try {
                final int start = Integer.parseInt(entry.key());
                final MessageKey key = entry.node().getMessageKey("key", null);
                if (key == null) {
                    continue;
                }
                entries.add(new Entry(start, key));
            } catch (NumberFormatException ignored) {}
        }
        if (!entries.isEmpty()) {
            return new MessageRange(entries.toArray(new Entry[0]));
        }
        return def;
    }

    private static final class Entry {
        private final int aboveInclusive;
        private final MessageKey message;

        private Entry(int aboveInclusive, @NotNull MessageKey message) {
            this.aboveInclusive = aboveInclusive;
            this.message = message;
        }
    }
}
