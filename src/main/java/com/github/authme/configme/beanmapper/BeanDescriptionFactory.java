package com.github.authme.configme.beanmapper;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Creates all {@link BeanPropertyDescription} objects for a given class.
 * <p>
 * The returned bean field objects are required to be writable properties, i.e. any fields
 * which don't have an associated setter (or getter) will be ignored.
 */
public class BeanDescriptionFactory {

    public Collection<BeanPropertyDescription> collectWritableFields(Class<?> clazz) {
        if (!isBeanClass(clazz)) {
            return Collections.emptyList();
        }

        List<BeanPropertyDescription> properties = Arrays.stream(clazz.getDeclaredFields())
            .filter(field -> (field.getModifiers() & (Modifier.STATIC | Modifier.FINAL)) == 0)
            .map(field -> buildPropertyDescription(clazz, field))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        validateProperties(clazz, properties);
        return properties;
    }

    @Nullable
    protected BeanPropertyDescription buildPropertyDescription(Class<?> clazz, Field field) {
        Method setter = getInstanceMethodSilently(clazz, "set" + capitalizeFirst(field.getName()), field.getType());
        if (setter == null) {
            return null;
        }

        Method getter = findGetter(clazz, field);
        if (getter == null) {
            return null;
        }

        return new BeanPropertyDescription(field.getName(), field.getType(), field.getGenericType(), getter, setter);
    }

    protected boolean isBeanClass(Class<?> clazz) {
        try {
            clazz.getConstructor();
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    protected void validateProperties(Class<?> clazz, Collection<BeanPropertyDescription> properties) {
        // With #32 we will allow custom names for properties
        long totalUniqueNames = properties.stream().map(BeanPropertyDescription::getName).distinct().count();
        if (totalUniqueNames != properties.size()) {
            throw new ConfigMeMapperException("Found properties with the same name in '" + clazz + "'");
        }
    }

    @Nullable
    protected Method findGetter(Class<?> clazz, Field field) {
        final String name = capitalizeFirst(field.getName());
        final Class<?> type = field.getType();

        // http://stackoverflow.com/questions/799280/valid-javabeans-names-for-boolean-getter-methods
        if (field.getType() == boolean.class) {
            // TODO #36: the javabeans standard infers properties based on the property names, not based on the fields
            // For boolean type it's possible to have something like isEmpty(), where the field behind might
            // be empty or isEmpty. For now we only support is+<propertyName>
            Method method = findGetterMethodWithReturnValue(clazz, "is" + name, type);
            if (method != null) {
                return method;
            }
        }
        return findGetterMethodWithReturnValue(clazz, "get" + name, type);
    }

    @Nullable
    private static Method getInstanceMethodSilently(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            Method method = clazz.getMethod(name, parameterTypes);
            return Modifier.isStatic(method.getModifiers()) ? null : method;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    @Nullable
    private static Method findGetterMethodWithReturnValue(Class<?> clazz, String name, Class<?> returnType) {
        Method method = getInstanceMethodSilently(clazz, name);
        if (method != null && returnType.isAssignableFrom(method.getReturnType())) {
            return method;
        }
        return null;
    }

    private static String capitalizeFirst(String str) {
        if (str.length() < 1) {
            throw new IllegalArgumentException(str);
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
