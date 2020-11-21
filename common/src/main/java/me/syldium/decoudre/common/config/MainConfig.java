package me.syldium.decoudre.common.config;

import me.syldium.decoudre.common.service.DataService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public interface MainConfig {

    default @NotNull Locale getLocale() {
        return Locale.getDefault();
    }

    @NotNull DataService.Type getDataStorageMethod();

    @NotNull String getJdbcUrl();

    @Nullable String getJdbcUsername();

    @Nullable String getJdbcPassword();
}
