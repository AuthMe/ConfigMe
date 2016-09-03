package com.github.authme.configme.propertymap;

import com.github.authme.configme.Comment;
import com.github.authme.configme.SettingsHolder;
import com.github.authme.configme.exception.ConfigMeException;
import com.github.authme.configme.properties.Property;
import com.sun.istack.internal.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Utility class responsible for retrieving all {@link Property} fields
 * from {@link SettingsHolder} implementations via reflection.
 * <p>
 * Properties must be declared as {@code public static} fields or they are ignored.
 */
public class SettingsFieldRetriever {

    private SettingsFieldRetriever() {
    }

    /**
     * Scans all given classes for their properties and return the generated list of property entries.
     * Properties are sorted by their group, and each group by insertion order.
     *
     * @param classes the classes to scan for their property fields
     * @return list with  all found properties and their associated comments
     * @see KnownPropertiesBuilder
     */
    @SafeVarargs
    public static List<PropertyEntry> getAllProperties(Class<? extends SettingsHolder>... classes) {
        KnownPropertiesBuilder propertyListBuilder = new KnownPropertiesBuilder();
        for (Class<?> clazz : classes) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                Property<?> property = getPropertyField(field);
                if (property != null) {
                    propertyListBuilder.add(property, getCommentsForField(field));
                }
            }
        }
        return propertyListBuilder.create();
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
    @Nullable
    private static Property<?> getPropertyField(Field field) {
        field.setAccessible(true);
        if (field.isAccessible() && Property.class.equals(field.getType()) && Modifier.isStatic(field.getModifiers())) {
            try {
                return (Property<?>) field.get(null);
            } catch (IllegalAccessException e) {
                throw new ConfigMeException("Could not fetch field '" + field.getName() + "' from class '"
                    + field.getDeclaringClass().getSimpleName() + "'", e);
            }
        }
        return null;
    }

}
