package me.syldium.decoudre.sponge.config;

import com.google.common.reflect.TypeToken;
import me.syldium.decoudre.common.config.ArenaConfig;
import me.syldium.decoudre.common.game.Arena;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class SpongeArenaConfig extends FileConfig<CommentedConfigurationNode> implements ArenaConfig {

    public SpongeArenaConfig(@NotNull File file, @NotNull Logger logger) {
        super(HoconConfigurationLoader.builder().setFile(file).build(), logger);
    }

    @Override
    public @NotNull Collection<Arena> load() {
        try {
            return this.root.getNode("arenas").getList(TypeToken.of(Arena.class));
        } catch (ObjectMappingException ex) {
            this.logger.error("Unable to map the set of arenas from the config file.", ex);
        }
        return Collections.emptyList();
    }

    @Override
    public void save(@NotNull Set<Arena> arenas) {
        try {
            this.root.getNode("arenas").setValue(new TypeToken<Set<Arena>>() {}, arenas);
        } catch (ObjectMappingException ex) {
            this.logger.error("Unable to map the set of arenas to the config file.", ex);
        }
        this.save();
    }
}
