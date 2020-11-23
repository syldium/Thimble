package me.syldium.decoudre.common.config;

import me.syldium.decoudre.common.service.DataService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public interface MainConfig {

    /**
     * Gets the locale to display messages.
     *
     * @return The locale.
     */
    default @NotNull Locale getLocale() {
        return Locale.getDefault();
    }

    /**
     * Gets the save method chosen for the statistics.
     *
     * @return The storage method.
     */
    @NotNull DataService.Type getDataStorageMethod();

    /**
     * Gets the JDBC url to interact with the database.
     *
     * @return The connection url.
     */
    @NotNull String getJdbcUrl();

    @Nullable String getJdbcUsername();

    @Nullable String getJdbcPassword();

    default @NotNull List<@NotNull String> getEnabledIntegrations() {
        return Collections.emptyList();
    }
}
