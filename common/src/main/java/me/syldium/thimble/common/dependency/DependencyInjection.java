package me.syldium.thimble.common.dependency;

import me.syldium.thimble.common.ThimblePlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

public final class DependencyInjection {

    private static final Method ADD_URL_METHOD;

    static {
        try {
            ADD_URL_METHOD = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            ADD_URL_METHOD.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private DependencyInjection() throws IllegalAccessException {
        throw new IllegalAccessException(this.getClass().getSimpleName() + " cannot be instantiated.");
    }

    public static void addJarToClasspath(@NotNull Path path, @NotNull URLClassLoader classLoader) {
        try {
            ADD_URL_METHOD.invoke(classLoader, path.toUri().toURL());
        } catch (IllegalAccessException | InvocationTargetException | MalformedURLException ex) {
            ex.printStackTrace();
        }
    }

    public static void addJarToClasspath(@NotNull Path path, @NotNull ThimblePlugin plugin) {
        addJarToClasspath(path, (URLClassLoader) plugin.getClass().getClassLoader());
    }
}
