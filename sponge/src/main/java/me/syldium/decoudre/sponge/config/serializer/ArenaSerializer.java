package me.syldium.decoudre.sponge.config.serializer;

import com.google.common.reflect.TypeToken;
import me.syldium.decoudre.api.Location;
import me.syldium.decoudre.common.game.Arena;
import me.syldium.decoudre.sponge.DeSpongePlugin;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public class ArenaSerializer implements TypeSerializer<Arena> {

    private final DeSpongePlugin plugin;

    public ArenaSerializer(@NotNull DeSpongePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable Arena deserialize(@NotNull TypeToken<?> type, @NotNull ConfigurationNode value) throws ObjectMappingException {
        String name = value.getNode("name").getString();
        if (name == null) {
            name = (String) value.getKey();
            if (name == null) {
                throw new ObjectMappingException("The 'name' node is missing.");
            }
        }
        Arena arena = new Arena(this.plugin, name);
        arena.setMinPlayers(value.getNode("min-players").getInt(1));
        arena.setMaxPlayers(value.getNode("max-players").getInt(8));
        this.set(value, arena::setSpawnLocation, "spawn-location");
        this.set(value, arena::setJumpLocation, "jump-location");
        return arena;
    }

    private void set(@NotNull ConfigurationNode node, @NotNull Consumer<@NotNull Location> setter, @NotNull Object ...path) throws ObjectMappingException {
        Location location = node.getNode(path).getValue(TypeToken.of(Location.class));
        if (location != null) {
            setter.accept(location);
        }
    }

    @Override
    public void serialize(@NotNull TypeToken<?> type, @Nullable Arena arena, @NotNull ConfigurationNode value) throws ObjectMappingException {
        if (arena == null) {
            return;
        }

        value.getNode("name").setValue(arena.getName());
        value.getNode("min-players").setValue(arena.getMinPlayers());
        value.getNode("max-players").setValue(arena.getMaxPlayers());
        value.getNode("spawn-location").setValue(TypeToken.of(Location.class), arena.getSpawnLocation());
        value.getNode("jump-location").setValue(TypeToken.of(Location.class), arena.getJumpLocation());
    }
}
