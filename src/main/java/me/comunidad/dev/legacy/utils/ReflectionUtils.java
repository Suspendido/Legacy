package me.comunidad.dev.legacy.utils;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("all")
public class ReflectionUtils {

    @NotNull // Just to deny those filthy warnings
    public static Field accessField(Class<?> object, String name) {
        try {

            Field field = object.getDeclaredField(name);
            field.setAccessible(true);
            return field;

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }

    @NotNull // Just to deny those filthy warnings
    public static Method accessMethod(Class<?> object, String name, Class<?>... params) {
        try {

            Method method = object.getMethod(name, params);
            method.setAccessible(true);
            return method;

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    @NotNull // Just to deny those filthy warnings
    public static Constructor<?> accessConstructor(Class<?> object, int index) {
        Constructor<?> constructor = object.getDeclaredConstructors()[index];
        constructor.setAccessible(true);
        return constructor;
    }

    @NotNull // Just to deny those filthy warnings
    public static <T> Constructor<T> accessConstructor(Class<T> object, Class<?>... args) {
        try {

            Constructor<?> constructor = object.getConstructor(args);
            constructor.setAccessible(true);
            return (Constructor<T>) constructor;

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void set(Field field, Object object, Object value) {
        try {

            field.set(object, value);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?> getClass(String name) {
        try {

            return Class.forName(name);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fetch(Field field, Object instance) {
        try {

            return (T) field.get(instance);

        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> T fetch(Method method, Object instance, Object... args) {
        try {

            return (T) method.invoke(instance, args);

        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);

        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}