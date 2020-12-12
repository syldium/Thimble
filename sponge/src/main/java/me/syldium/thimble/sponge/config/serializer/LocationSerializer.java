package me.syldium.thimble.sponge.config.serializer;

import com.google.common.reflect.TypeToken;
import me.syldium.thimble.api.Location;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class LocationSerializer implements TypeSerializer<Location> {

    @Override
    public @Nullable Location deserialize(@NotNull TypeToken<?> type, @NotNull ConfigurationNode value) throws ObjectMappingException {
        UUID worldUUID = value.getNode("world").getValue(TypeToken.of(UUID.class));
        if (worldUUID == null) {
            throw new ObjectMappingException("The 'world' node is missing");
        }
        return new Location(
                worldUUID,
                value.getNode("x").getDouble(),
                value.getNode("y").getDouble(),
                value.getNode("z").getDouble(),
                value.getNode("pitch").getFloat(),
                value.getNode("yaw").getFloat()
        );
    }

    @Override
    public void serialize(@NotNull TypeToken<?> type, @Nullable Location location, @NotNull ConfigurationNode value) throws ObjectMappingException {
        if (location == null) {
            return;
        }

        value.getNode("world").setValue(TypeToken.of(UUID.class), location.getWorldUUID());
        value.getNode("x").setValue(location.getX());
        value.getNode("y").setValue(location.getY());
        value.getNode("z").setValue(location.getZ());
        value.getNode("pitch").setValue(location.getPitch());
        value.getNode("yaw").setValue(location.getYaw());
    }
}
