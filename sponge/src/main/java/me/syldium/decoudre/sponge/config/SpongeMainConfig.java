package me.syldium.decoudre.sponge.config;

import me.syldium.decoudre.common.config.MainConfig;
import me.syldium.decoudre.common.service.DataService;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

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
}
