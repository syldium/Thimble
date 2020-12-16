package me.syldium.thimble.sponge.config.serializer;

import com.google.common.reflect.TypeToken;
import me.syldium.thimble.api.util.BlockVector;
import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.arena.ThimbleGameMode;
import me.syldium.thimble.common.game.Arena;
import me.syldium.thimble.common.util.EnumUtil;
import me.syldium.thimble.sponge.ThSpongePlugin;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public class ArenaSerializer implements TypeSerializer<Arena> {

    private final ThSpongePlugin plugin;

    public ArenaSerializer(@NotNull ThSpongePlugin plugin) {
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
        arena.setGameMode(EnumUtil.valueOf(ThimbleGameMode.class, value.getString("gamemode"), ThimbleGameMode.SINGLE));
        this.setLocation(value, arena::setSpawnLocation, "spawn-location");
        this.setLocation(value, arena::setJumpLocation, "jump-location");
        this.setLocation(value, arena::setWaitLocation, "wait-location");
        this.setBlockVector(value, arena::setPoolMinPoint, "min-point");
        this.setBlockVector(value, arena::setPoolMaxPoint, "max-point");
        return arena;
    }

    private void setLocation(@NotNull ConfigurationNode node, @NotNull Consumer<@NotNull Location> setter, @NotNull Object ...path) throws ObjectMappingException {
        Location location = node.getNode(path).getValue(TypeToken.of(Location.class));
        if (location != null) {
            setter.accept(location);
        }
    }

    private void setBlockVector(@NotNull ConfigurationNode node, @NotNull Consumer<@NotNull BlockVector> setter, @NotNull Object ...path) throws ObjectMappingException {
        BlockVector vector = node.getNode(path).getValue(TypeToken.of(BlockVector.class));
        if (vector != null) {
            setter.accept(vector);
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
        value.getNode("gamemode").setValue(arena.getGameMode().name());
        value.getNode("spawn-location").setValue(TypeToken.of(Location.class), arena.getSpawnLocation());
        value.getNode("jump-location").setValue(TypeToken.of(Location.class), arena.getJumpLocation());
        value.getNode("wait-location").setValue(TypeToken.of(Location.class), arena.getWaitLocation());
        value.getNode("min-point").setValue(TypeToken.of(BlockVector.class), arena.getPoolMinPoint());
        value.getNode("max-point").setValue(TypeToken.of(BlockVector.class), arena.getPoolMaxPoint());
    }
}
