package me.syldium.decoudre.common.util;

import me.syldium.decoudre.common.DeCoudrePlugin;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class ResourceReader {

    private ResourceReader() throws IllegalAccessException {
        throw new IllegalAccessException(this.getClass().getSimpleName() + " cannot be instantiated.");
    }

    /**
     * Read a resource on the jar's classpath into a string.
     *
     * @param path The path on the classpath to read.
     * @return The read string.
     */
    public static @NotNull String readResource(@NotNull String path) {
        try (InputStream stream = DeCoudrePlugin.class.getResourceAsStream("/" + path);
             BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            return stringBuilder.toString();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
