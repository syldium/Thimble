package me.syldium.thimble.common.service;

import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.player.MessageKey;
import me.syldium.thimble.common.player.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.placeholder.Placeholder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.minimessage.placeholder.PlaceholderResolver.placeholders;

public class MessageServiceImpl implements MessageService {

    private static final ResourceBundle NULL_BUNDLE = new ResourceBundle() {
        public @NotNull Enumeration<String> getKeys() {
            return Collections.emptyEnumeration();
        }

        protected Object handleGetObject(@NotNull String key) {
            return null;
        }
    };

    private final ResourceBundle defaultBundle;
    private final ThimblePlugin plugin;
    private ResourceBundle localeBundle;
    private ResourceBundle customBundle;
    private PlaceholderService placeholders;
    private Component prefix;

    public MessageServiceImpl(@NotNull ThimblePlugin plugin) {
        this.defaultBundle = ResourceBundle.getBundle(MESSAGES_BUNDLE, Locale.ENGLISH, new UTF8PropertiesControl());
        this.plugin = plugin;
        this.placeholders = PlaceholderService.EMPTY;
        this.updateLocale(plugin.getMainConfig().getLocale());
    }

    @Override
    public void updateLocale() {
        this.updateLocale(this.plugin.getMainConfig().getLocale());
    }

    public void updateLocale(@NotNull Locale userLocale) {
        ResourceBundle.clearCache();

        try {
            this.localeBundle = ResourceBundle.getBundle(MESSAGES_BUNDLE, userLocale, new UTF8PropertiesControl());
        } catch (MissingResourceException ex) {
            this.localeBundle = NULL_BUNDLE;
        }

        try {
            this.customBundle = ResourceBundle.getBundle(MESSAGES_BUNDLE, userLocale, new FileResClassLoader(this.plugin.getClass().getClassLoader(), this.plugin), new UTF8PropertiesControl());
        } catch (final MissingResourceException ex) {
            this.customBundle = NULL_BUNDLE;
        }
        this.prefix = miniMessage().parse(this.translate("prefix"));
    }

    @Override
    public @NotNull Component prefix() {
        return this.prefix;
    }

    @Override
    public @NotNull Component formatMessage(@NotNull MessageKey key, @Nullable TextColor color, @NotNull Placeholder<?>... placeholders) {
        String input = this.translate(key.getAccessor());
        return miniMessage().deserialize(input, placeholders(placeholders)).colorIfAbsent(color);
    }

    @Override
    public @NotNull String get(@NotNull MessageKey key) {
        return this.translate(key.getAccessor());
    }

    @Override
    public void setExternalPlaceholderService(@NotNull PlaceholderService service) {
        this.placeholders = service;
    }

    private @NotNull String translate(@NotNull String string) {
        try {
            try {
                return this.customBundle.getString(string);
            } catch (MissingResourceException ex) {
                return this.localeBundle.getString(string);
            }
        } catch (MissingResourceException ex) {
            this.plugin.getLogger().log(Level.WARNING, String.format("Missing translation key \"%s\" in translation file %s", ex.getKey(), this.localeBundle.getLocale().toString()), ex);
            return this.defaultBundle.getString(string);
        }
    }

    @Override
    public @NotNull String setPlaceholders(@NotNull ThimblePlayer player0, @NotNull String text) {
        if (this.placeholders == PlaceholderService.EMPTY) {
            return text;
        }
        Player player = this.plugin.getPlayer(player0.uuid());
        if (player == null) {
            return text;
        }
        return this.placeholders.setPlaceholders(player, text);
    }

    @Override
    public @NotNull String setPlaceholders(@NotNull Player player, @NotNull String text) {
        return this.placeholders.setPlaceholders(player, text);
    }

    /**
     * Attempts to load properties files from the plugin directory before falling back to the jar.
     */
    private static class FileResClassLoader extends ClassLoader {

        private final File dataFolder;

        FileResClassLoader(@NotNull ClassLoader classLoader, @NotNull ThimblePlugin plugin) {
            super(classLoader);
            this.dataFolder = plugin.getDataFolder();
        }

        @Override
        public URL getResource(final String string) {
            File file = new File(this.dataFolder, string);
            if (file.exists()) {
                try {
                    return file.toURI().toURL();
                } catch (final MalformedURLException ignored) { }
            }
            return null;
        }

        @Override
        public InputStream getResourceAsStream(final String string) {
            final File file = new File(this.dataFolder, string);
            if (file.exists()) {
                try {
                    return new FileInputStream(file);
                } catch (FileNotFoundException ignored) { }
            }
            return null;
        }
    }

    /**
     * Reads .properties files as UTF-8 instead of ISO-8859-1, which is the default on Java 8/below.
     * Java 9 fixes this by defaulting to UTF-8 for .properties files.
     */
    private static class UTF8PropertiesControl extends ResourceBundle.Control {

        @Override
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
                try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) { // UTF-8
                    bundle = new PropertyResourceBundle(reader);
                } finally {
                    stream.close();
                }
            }
            return bundle;
        }

        @Override // ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT)
        public @Nullable Locale getFallbackLocale(final String baseName, final Locale locale) {
            return null;
        }
    }
}
