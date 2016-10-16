package com.github.authme.configme.knownproperties;

import com.github.authme.configme.Comment;
import com.github.authme.configme.SectionComments;
import com.github.authme.configme.SettingsHolder;
import com.github.authme.configme.exception.ConfigMeException;
import com.github.authme.configme.properties.Property;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class responsible for retrieving all {@link Property} fields
 * from {@link SettingsHolder} implementations via reflection.
 * <p>
 * Properties must be declared as {@code public static} fields or they are ignored.
 * Comments for sections (parent paths of properties) may be defined with {@link SectionComments} methods.
 */
public class ConfigurationDataBuilder {

    private final PropertyListBuilder propertyListBuilder = new PropertyListBuilder();
    private final CommentsGatherer commentsGatherer = new CommentsGatherer();

    private ConfigurationDataBuilder() {
    }

    /**
     * Collects all properties and comment data from the provided classes.
     * Properties are sorted by their group, and each group is by insertion order.
     *
     * @param classes the classes to scan for their property data
     * @return collected configuration data
     */
    @SafeVarargs
    public static ConfigurationData getAllProperties(Class<? extends SettingsHolder>... classes) {
        return new ConfigurationDataBuilder().getAllProperties0(classes);
    }

    /**
     * Collects all properties and comment data from the provided classes.
     * Properties are sorted by their group, and each group is by insertion order.
     *
     * @param classes the classes to scan for their property data
     * @return collected configuration data
     */
    private ConfigurationData getAllProperties0(Class<? extends SettingsHolder>[] classes) {
        for (Class<?> clazz : classes) {
            collectProperties(clazz);
            commentsGatherer.collectAllSectionComments(clazz);
        }
        return new ConfigurationData(propertyListBuilder.create(), commentsGatherer.comments);
    }

    private void collectProperties(Class<?> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            Property<?> property = getPropertyField(field);
            if (property != null) {
                propertyListBuilder.add(property);
                saveComment(field, property.getPath());
            }
        }
    }

    private void saveComment(Field field, String path) {
        if (field.isAnnotationPresent(Comment.class)) {
            commentsGatherer.comments.put(path, field.getAnnotation(Comment.class).value());
        }
    }

    /**
     * Returns the given field's value if it is a static {@link Property}.
     *
     * @param field the field's value to return
     * @return the property the field defines, or null if not applicable
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

    /**
     * Collects all section comments via {@link SectionComments} methods and {@link Comment} annotations
     * from the provided classes.
     */
    private static final class CommentsGatherer {
        final Map<String, String[]> comments = new HashMap<>();

        void collectAllSectionComments(Class<?> clazz) {
            Arrays.stream(clazz.getMethods())
                .filter(method -> method.isAnnotationPresent(SectionComments.class))
                .map(method -> callSectionCommentsMethod(method))
                .filter(map -> map != null)
                .forEach(comments::putAll);
        }

        private static Map<String, String[]> callSectionCommentsMethod(Method method) {
            if (!Modifier.isStatic(method.getModifiers())) {
                throw new ConfigMeException(
                    "Methods with @SectionComments must be static! Offending method: '" + method + "'");
            } else if (method.getParameters().length > 0) {
                throw new ConfigMeException(
                    "@SectionComments methods may not have any parameters. Offending method: '" + method + "'");
            }
            try {
                return (Map<String, String[]>) method.invoke(null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ConfigMeException("Could not get section comments from '" + method + "'", e);
            } catch (ClassCastException e) {
                throw new ConfigMeException("Could not get section comments from '" + method
                    + "': Return value must be Map<String, String>", e);
            }
        }
    }

}
