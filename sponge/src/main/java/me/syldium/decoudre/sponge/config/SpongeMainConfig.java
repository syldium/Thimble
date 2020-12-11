package me.syldium.decoudre.sponge.config;

import com.google.common.reflect.TypeToken;
import me.syldium.decoudre.common.config.MainConfig;
import me.syldium.decoudre.common.service.DataService;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class SpongeMainConfig extends FileConfig<CommentedConfigurationNode> implements MainConfig {

    public SpongeMainConfig(@NotNull ConfigurationLoader<CommentedConfigurationNode> loader, @NotNull Logger logger) {
        super(loader, logger);
    }

    @Override
    public @NotNull DataService.Type getDataStorageMethod() {
        String method = this.root.getNode("storage-method").getString("sqlite");
        try {
            return DataService.Type.valueOf(method.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return DataService.Type.SQLITE;
        }
    }

    @Override
    public @NotNull String getJdbcUrl() {
        return this.root.getNode("sql", "jdbcUri").getString("jdbc:mysql://localhost:3306/decoudre");
    }

    @Override
    public @Nullable String getJdbcUsername() {
        return this.root.getNode("sql", "username").getString();
    }

    @Override
    public @Nullable String getJdbcPassword() {
        return this.root.getNode("sql", "password").getString();
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    public @NotNull List<String> getEnabledIntegrations() {
        try {
            return this.root.getNode("integrations").getList(TypeToken.of(String.class));
        } catch (ObjectMappingException ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean doesTeleportAtEnd() {
        return this.root.getNode("game", "teleport-at-end").getBoolean(true);
    }

    @Override
    public boolean doesCountFailsInConcurrent() {
        return this.root.getNode("game", "count-fails-concurrent").getBoolean(false);
    }

    @Override
    public int getCountdownTime() {
        return this.root.getNode("game", "countdown-time").getInt(30);
    }

    @Override
    public int getJumpTimeSingleMode() {
        return this.root.getNode("game", "jump-time-single").getInt(15);
    }

    @Override
    public int getJumpTimeConcurrentMode() {
        return this.root.getNode("game", "jump-time-concurrent").getInt(40);
    }

    @Override
    public @Nullable String getRawDisplayProperty(@NotNull String audienceName, @NotNull String propertyKey) {
        return this.root.getNode("display", audienceName, propertyKey).getString();
    }
}
