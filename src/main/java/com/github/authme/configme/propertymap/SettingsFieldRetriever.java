package com.github.authme.configme.propertymap;

import com.github.authme.configme.Comment;
import com.github.authme.configme.SettingsHolder;
import com.github.authme.configme.properties.Property;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class responsible for retrieving all {@link Property} fields
 * from {@link SettingsHolder} implementations via reflection.
 */
public class SettingsFieldRetriever {

    /** The classes to scan for properties. */
    private final List<Class<? extends SettingsHolder>> classes;

    @SafeVarargs
    public SettingsFieldRetriever(Class<? extends SettingsHolder>... classes) {
        this.classes = Arrays.asList(classes);
    }

    /**
     * Scans all given classes for their properties and return the generated {@link PropertyMap}.
     *
     * @return PropertyMap containing all found properties and their associated comments
     * @see #classes
     */
    public PropertyMap getAllPropertyFields() {
        PropertyMap properties = new PropertyMap();
        for (Class<?> clazz : classes) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                Property<?> property = getPropertyField(field);
                if (property != null) {
                    properties.put(property, getCommentsForField(field));
                }
            }
        }
        return properties;
    }

    private static String[] getCommentsForField(Field field) {
        if (field.isAnnotationPresent(Comment.class)) {
            return field.getAnnotation(Comment.class).value();
        }
        return new String[0];
    }

    /**
     * Returns the given field's value if it is a static {@link Property}.
     *
     * @param field The field's value to return
     * @return The property the field defines, or null if not applicable
     */
    private static Property<?> getPropertyField(Field field) {
        field.setAccessible(true);
        if (field.isAccessible() && Property.class.equals(field.getType()) && Modifier.isStatic(field.getModifiers())) {
            try {
                return (Property<?>) field.get(null);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Could not fetch field '" + field.getName() + "' from class '"
                    + field.getDeclaringClass().getSimpleName() + "'", e);
            }
        }
        return null;
    }

}
