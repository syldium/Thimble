// Original: https://github.com/KyoriPowered/adventure-platform
/*
 * This file is part of adventure-platform, licensed under the MIT License.
 *
 * Copyright (c) 2018-2020 KyoriPowered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.syldium.thimble.bukkit.util;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static java.util.Objects.requireNonNull;

/**
 * Reflection utilities for accessing {@code net.minecraft.server}.
 */
public final class MinecraftReflection {
    private MinecraftReflection() {
    }

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final String PREFIX_NMS = "net.minecraft.server";
    private static final String PREFIX_MC = "net.minecraft.";
    private static final String PREFIX_CRAFTBUKKIT = "org.bukkit.craftbukkit";
    private static final String CRAFT_SERVER = "CraftServer";
    private static final @Nullable String VERSION;

    static {
        final Class<?> serverClass = Bukkit.getServer().getClass();
        if (!serverClass.getSimpleName().equals(CRAFT_SERVER)) {
            VERSION = null;
        } else if (serverClass.getName().equals(PREFIX_CRAFTBUKKIT + "." + CRAFT_SERVER)) {
            VERSION = ".";
        } else {
            String name = serverClass.getName();
            name = name.substring(PREFIX_CRAFTBUKKIT.length());
            name = name.substring(0, name.length() - CRAFT_SERVER.length());
            VERSION = name;
        }
    }

    /**
     * Gets a class by the first name available.
     *
     * @param classNames an array of class names to try in order
     * @return a class or {@code null} if not found
     */
    public static @Nullable Class<?> findClass(final @Nullable String @NotNull ... classNames) {
        for (final String clazz : classNames) {
            if (clazz == null) continue;

            try {
                return Class.forName(clazz);
            } catch (final ClassNotFoundException ignored) {
            }
        }
        return null;
    }

    /**
     * Gets whether a class is available.
     *
     * @param classNames an array of class names to try in order
     * @return if the class is loaded
     */
    public static boolean hasClass(final @NotNull String... classNames) {
        return findClass(classNames) != null;
    }

    /**
     * Gets a handle for a class method.
     *
     * @param holderClass a class
     * @param methodName a method name
     * @param returnClass a method return class
     * @param parameterClasses an array of method parameter classes
     * @return a method handle or {@code null} if not found
     */
    public static @Nullable MethodHandle findMethod(final @Nullable Class<?> holderClass, final String methodName, final @Nullable Class<?> returnClass, final Class<?>... parameterClasses) {
        return findMethod(holderClass, new String[]{methodName}, returnClass, parameterClasses);
    }

    /**
     * Gets a handle for a class method.
     *
     * @param holderClass a class
     * @param methodNames a method name
     * @param returnClass a method return class
     * @param parameterClasses an array of method parameter classes
     * @return a method handle or {@code null} if not found
     */
    public static @Nullable MethodHandle findMethod(final @Nullable Class<?> holderClass, final @Nullable String @NotNull [] methodNames, final @Nullable Class<?> returnClass, final Class<?>... parameterClasses) {
        if (holderClass == null || returnClass == null) return null;
        for (final Class<?> parameterClass : parameterClasses) {
            if (parameterClass == null) return null;
        }

        for (final String methodName : methodNames) {
            if (methodName == null) continue;
            try {
                return LOOKUP.findVirtual(holderClass, methodName, MethodType.methodType(returnClass, parameterClasses));
            } catch (final NoSuchMethodException | IllegalAccessException ignored) {
            }
        }
        for (final Method method : holderClass.getMethods()) {
            if (!method.getReturnType().isAssignableFrom(returnClass)) continue;
            final Class<?>[] parametersTypes = method.getParameterTypes();
            if (parametersTypes.length != parameterClasses.length) continue;
            boolean assignable = true;
            for (int i = 0; i < parametersTypes.length; i++) {
                if (!parametersTypes[i].isAssignableFrom(parameterClasses[i])) {
                    assignable = false;
                    break;
                }
            }
            if (!assignable) continue;
            try {
                return LOOKUP.unreflect(method);
            } catch (final IllegalAccessException ignored) {
            }
        }
        return null;
    }

    /**
     * Gets a handle for a class constructor.
     *
     * @param holderClass a class
     * @param parameterClasses an array of method parameter classes
     * @return a method handle or {@code null} if not found
     */
    public static @Nullable MethodHandle findConstructor(final @Nullable Class<?> holderClass, final @Nullable Class<?>... parameterClasses) {
        if (holderClass == null) return null;
        for (final Class<?> parameterClass : parameterClasses) {
            if (parameterClass == null) return null;
        }

        try {
            return LOOKUP.findConstructor(holderClass, MethodType.methodType(void.class, parameterClasses));
        } catch (final NoSuchMethodException | IllegalAccessException e) {
            return null;
        }
    }

    /**
     * Gets a class field if possible and makes it accessible.
     *
     * @param holderClass a class
     * @param fieldName a field name
     * @return an accessible field
     */
    public static @Nullable Field findField(final @Nullable Class<?> holderClass, final @NotNull String... fieldName) {
        return findField(holderClass, null, fieldName);
    }

    /**
     * Gets a class field if it exists and is of the appropriate type and makes
     * it accessible.
     *
     * @param holderClass a class
     * @param expectedType the expected type of the field
     * @param fieldNames a field name
     * @return an accessible field
     */
    public static @Nullable Field findField(final @Nullable Class<?> holderClass, final @Nullable Class<?> expectedType, final @NotNull String... fieldNames) {
        if (holderClass == null) return null;

        Field field;
        for (final String fieldName : fieldNames) {
            try {
                field = holderClass.getDeclaredField(fieldName);
            } catch (final NoSuchFieldException ex) {
                continue;
            }

            field.setAccessible(true);
            if (expectedType != null && !expectedType.isAssignableFrom(field.getType())) {
                continue;
            }

            return field;
        }

        return null;
    }

    /**
     * Gets an enum value.
     *
     * @param enumClass an enum class
     * @param enumName an enum name
     * @return an enum value or {@code null} if not found
     */
    public static @Nullable Object findEnum(final @Nullable Class<?> enumClass, final @NotNull String enumName) {
        return findEnum(enumClass, enumName, Integer.MAX_VALUE);
    }

    /**
     * Gets an enum value.
     *
     * @param enumClass an enum class
     * @param enumName an enum name
     * @param enumFallbackOrdinal an enum ordinal, when the name is not found
     * @return an enum value or {@code null} if not found
     */
    public static @Nullable Object findEnum(final @Nullable Class<?> enumClass, final @NotNull String enumName, final int enumFallbackOrdinal) {
        if (enumClass == null || !Enum.class.isAssignableFrom(enumClass)) {
            return null;
        }

        try {
            return Enum.valueOf(enumClass.asSubclass(Enum.class), enumName);
        } catch (final IllegalArgumentException e) {
            final Object[] constants = enumClass.getEnumConstants();
            if (constants.length > enumFallbackOrdinal) {
                return constants[enumFallbackOrdinal];
            }
        }

        return null;
    }

    /**
     * Gets whether CraftBukkit is available.
     *
     * @return if CraftBukkit is available
     */
    public static boolean isCraftBukkit() {
        return VERSION != null;
    }

    /**
     * Gets a {@code org.bukkit.craftbukkit} class name.
     *
     * @param className a class name, without the {@code org.bukkit.craftbukkit} prefix
     * @return a class name or {@code null} if not found
     */
    public static @Nullable String findCraftClassName(final @NotNull String className) {
        return isCraftBukkit() ? PREFIX_CRAFTBUKKIT + VERSION + className : null;
    }

    /**
     * Gets a {@code org.bukkit.craftbukkit} class.
     *
     * @param className a class name, without the {@code org.bukkit.craftbukkit} prefix
     * @return a class or {@code null} if not found
     */
    public static @Nullable Class<?> findCraftClass(final @NotNull String className) {
        final String craftClassName = findCraftClassName(className);
        if (craftClassName == null) {
            return null;
        }

        return findClass(craftClassName);
    }

    /**
     * Gets a {@code org.bukkit.craftbukkit} class.
     *
     * @param className a class name, without the {@code org.bukkit.craftbukkit} prefix
     * @param superClass a super class
     * @param <T> a super type
     * @return a class or {@code null} if not found
     */
    public static <T> @Nullable Class<? extends T> findCraftClass(final @NotNull String className, final @NotNull Class<T> superClass) {
        final Class<?> craftClass = findCraftClass(className);
        if (craftClass == null || !requireNonNull(superClass, "superClass").isAssignableFrom(craftClass)) {
            return null;
        }
        return craftClass.asSubclass(superClass);
    }

    /**
     * Gets a {@code net.minecraft.server} class name.
     *
     * @param className a class name, without the {@code net.minecraft.server} prefix
     * @return a class name or {@code null} if not found
     */
    public static @Nullable String findNmsClassName(final @NotNull String className) {
        return isCraftBukkit() ? PREFIX_NMS + VERSION + className : null;
    }

    /**
     * Gets a {@code net.minecraft} class name.
     *
     * @param className a class name, without the {@code net.minecraft} prefix
     * @return a class name or {@code null} if not found
     */
    public static @Nullable String findMcClassName(final @NotNull String className) {
        return isCraftBukkit() ? PREFIX_MC + className : null;
    }

    /**
     * Gets the singleton method handle lookup.
     *
     * @return the method handle lookup
     */
    public static MethodHandles.@NotNull Lookup lookup() {
        return LOOKUP;
    }
}
