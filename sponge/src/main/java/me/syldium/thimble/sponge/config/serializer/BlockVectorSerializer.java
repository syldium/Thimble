package me.syldium.thimble.sponge.config.serializer;

import com.google.common.reflect.TypeToken;
import me.syldium.thimble.api.BlockVector;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class BlockVectorSerializer implements TypeSerializer<BlockVector> {

    @Override
    public @Nullable BlockVector deserialize(@NotNull TypeToken<?> type, @NotNull ConfigurationNode value) throws ObjectMappingException {
        return new BlockVector(
                value.getNode("x").getInt(),
                value.getNode("y").getInt(),
                value.getNode("z").getInt()
        );
    }

    @Override
    public void serialize(@NotNull TypeToken<?> type, @Nullable BlockVector vector, @NotNull ConfigurationNode value) throws ObjectMappingException {
        if (vector == null) {
            return;
        }
        value.getNode("x").setValue(vector.getX());
        value.getNode("y").setValue(vector.getY());
        value.getNode("z").setValue(vector.getZ());
    }
}
