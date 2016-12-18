package ch.jalu.configme.configurationdata;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SectionComments;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.Property;

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
     * Properties are sorted by their group, and each group is sorted by order of encounter.
     *
     * @param classes the classes to scan for their property data
     * @return collected configuration data
     */
    @SafeVarargs
    public static ConfigurationData collectData(Class<? extends SettingsHolder>... classes) {
        return collectData(Arrays.asList(classes));
    }

    /**
     * Collects all properties and comment data from the provided classes.
     * Properties are sorted by their group, and each group is sorted by order of encounter.
     *
     * @param classes the classes to scan for their property data
     * @return collected configuration data
     */
    public static ConfigurationData collectData(Iterable<Class<? extends SettingsHolder>> classes) {
        ConfigurationDataBuilder builder = new ConfigurationDataBuilder();
        for (Class<? extends SettingsHolder> clazz : classes) {
            builder.collectProperties(clazz);
            builder.commentsGatherer.collectAllSectionComments(clazz);
        }
        return new ConfigurationData(builder.propertyListBuilder.create(), builder.commentsGatherer.getComments());
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
        if (Property.class.isAssignableFrom(field.getType()) && Modifier.isStatic(field.getModifiers())) {
            try {
                return (Property<?>) field.get(null);
            } catch (IllegalAccessException e) {
                throw new ConfigMeException("Could not fetch field '" + field.getName() + "' from class '"
                    + field.getDeclaringClass().getSimpleName() + "'. Is it maybe not public?", e);
            }
        }
        return null;
    }

    /**
     * Collects all section comments via {@link SectionComments} methods and {@link Comment} annotations
     * from the provided classes.
     */
    private static final class CommentsGatherer {
        private final Map<String, String[]> comments = new HashMap<>();

        void collectAllSectionComments(Class<?> clazz) {
            Arrays.stream(clazz.getMethods())
                .filter(method -> method.isAnnotationPresent(SectionComments.class))
                .map(method -> callSectionCommentsMethod(method))
                .filter(map -> map != null)
                .forEach(comments::putAll);
        }

        Map<String, String[]> getComments() {
            return comments;
        }

        private static Map<String, String[]> callSectionCommentsMethod(Method method) {
            if (!Modifier.isStatic(method.getModifiers())) {
                throw new ConfigMeException(
                    "Methods with @SectionComments must be static. Offending method: '" + method + "'");
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
