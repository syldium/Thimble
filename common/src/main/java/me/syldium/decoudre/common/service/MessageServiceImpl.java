package me.syldium.decoudre.common.service;

import me.syldium.decoudre.common.config.MainConfig;
import me.syldium.decoudre.common.player.MessageKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageServiceImpl implements MessageService {

    private static final String MESSAGES = "messages";

    private final ResourceBundle defaultBundle;
    private final ResourceBundle localeBundle;
    private final Logger logger;

    public MessageServiceImpl(@NotNull MainConfig config, @NotNull Logger logger) {
        this.defaultBundle = ResourceBundle.getBundle(MESSAGES, Locale.ENGLISH, new UTF8PropertiesControl());
        this.localeBundle = ResourceBundle.getBundle(MESSAGES, config.getLocale(), new UTF8PropertiesControl());
        this.logger = logger;
    }

    @Override
    public @NotNull Component formatMessage(@NotNull MessageKey key, @NotNull Template ...templates) {
        String input = this.translate(key.getAccessor());
        return MiniMessage.get().parse(input, templates);
    }

    @Override
    public @NotNull Component formatMessage(@NotNull MessageKey key, @Nullable TextColor color, @NotNull String[] placeholders) {
        String input = this.translate(key.getAccessor());
        return MiniMessage.get().parse(input, placeholders).colorIfAbsent(color);
    }

    private @NotNull String translate(@NotNull String string) {
        try {
            return this.localeBundle.getString(string);
        } catch (MissingResourceException ex) {
            this.logger.log(Level.WARNING, String.format("Missing translation key \"%s\" in translation file %s", ex.getKey(), this.localeBundle.getLocale().toString()), ex);
            return this.defaultBundle.getString(string);
        }
    }

    /**
     * Reads .properties files as UTF-8 instead of ISO-8859-1, which is the default on Java 8/below.
     * Java 9 fixes this by defaulting to UTF-8 for .properties files.
     */
    private static class UTF8PropertiesControl extends ResourceBundle.Control {

        public ResourceBundle newBundle(final String baseName, final Locale locale, final String format, final ClassLoader loader, final boolean reload) throws IOException {
            String resourceName = this.toResourceName(this.toBundleName(baseName, locale), "properties");
            ResourceBundle bundle = null;
            InputStream stream = null;
            if (reload) {
                URL url = loader.getResource(resourceName);
                if (url != null) {
                    URLConnection connection = url.openConnection();
                    if (connection != null) {
                        connection.setUseCaches(false);
                        stream = connection.getInputStream();
                    }
                }
            } else {
                stream = loader.getResourceAsStream(resourceName);
            }
            if (stream != null) {
                try {
                    bundle = new PropertyResourceBundle(new InputStreamReader(stream, StandardCharsets.UTF_8)); // UTF-8
                } finally {
                    stream.close();
                }
            }
            return bundle;
        }
    }
}
