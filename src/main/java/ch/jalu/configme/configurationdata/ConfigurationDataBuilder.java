package ch.jalu.configme.configurationdata;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Utility class responsible for retrieving all {@link Property} fields
 * from {@link SettingsHolder} implementations via reflection.
 * <p>
 * Properties must be declared as {@code public static} fields or they are ignored.
 */
public class ConfigurationDataBuilder {

    @SuppressWarnings("checkstyle:VisibilityModifier")
    protected @NotNull PropertyListBuilder propertyListBuilder = new PropertyListBuilder();
    @SuppressWarnings("checkstyle:VisibilityModifier")
    protected @NotNull CommentsConfiguration commentsConfiguration = new CommentsConfiguration();

    protected ConfigurationDataBuilder() {
    }

    /**
     * Collects all properties and comment data from the provided classes.
     * Properties are sorted by their group, and each group is sorted by order of encounter.
     *
     * @param classes the classes to scan for their property data
     * @return collected configuration data
     */
    @SafeVarargs
    public static @NotNull ConfigurationData createConfiguration(@NotNull Class<? extends SettingsHolder>... classes) {
        return createConfiguration(Arrays.asList(classes));
    }

    /**
     * Collects all properties and comment data from the provided classes.
     * Properties are sorted by their group, and each group is sorted by order of encounter.
     *
     * @param classes the classes to scan for their property data
     * @return collected configuration data
     */
    public static @NotNull ConfigurationData createConfiguration(
                                                           @NotNull Iterable<Class<? extends SettingsHolder>> classes) {
        ConfigurationDataBuilder builder = new ConfigurationDataBuilder();
        return builder.collectData(classes);
    }

    public static @NotNull ConfigurationData createConfiguration(@NotNull List<? extends Property<?>> properties) {
        return new ConfigurationDataImpl(properties, Collections.emptyMap());
    }

    public static @NotNull ConfigurationData createConfiguration(@NotNull List<? extends Property<?>> properties,
                                                                 @NotNull CommentsConfiguration commentsConfiguration) {
        return new ConfigurationDataImpl(properties, commentsConfiguration.getAllComments());
    }

    /**
     * Collects property data and comment info from the given class and creates a configuration data
     * instance with it.
     *
     * @param classes the classes to process
     * @return configuration data with the classes' data
     */
    protected @NotNull ConfigurationData collectData(@NotNull Iterable<Class<? extends SettingsHolder>> classes) {
        for (Class<? extends SettingsHolder> clazz : classes) {
            collectProperties(clazz);
            collectSectionComments(clazz);
        }
        return new ConfigurationDataImpl(propertyListBuilder.create(), commentsConfiguration.getAllComments());
    }

    /**
     * Registers all property fields of the given class to this instance's property list builder.
     *
     * @param clazz the class to process
     */
    protected void collectProperties(@NotNull Class<?> clazz) {
        findFieldsToProcess(clazz).forEach(field -> {
            Property<?> property = getPropertyField(field);
            if (property != null) {
                propertyListBuilder.add(property);
                setCommentForPropertyField(field, property.getPath());
            }
        });
    }

    protected void setCommentForPropertyField(@NotNull Field field, @NotNull String path) {
        Comment commentAnnotation = field.getAnnotation(Comment.class);
        if (commentAnnotation != null) {
            commentsConfiguration.setComment(path, commentAnnotation.value());
        }
    }

    /**
     * Returns the given field's value if it is a static {@link Property}.
     *
     * @param field the field's value to return
     * @return the property the field defines, or null if not applicable
     */
    protected @Nullable Property<?> getPropertyField(@NotNull Field field) {
        if (Property.class.isAssignableFrom(field.getType()) && Modifier.isStatic(field.getModifiers())) {
            try {
                setFieldAccessibleIfNeeded(field);
                return (Property<?>) field.get(null);
            } catch (IllegalAccessException e) {
                throw new ConfigMeException("Could not fetch field '" + field.getName() + "' from class '"
                    + field.getDeclaringClass().getSimpleName() + "'. Is it maybe not public?", e);
            }
        }
        return null;
    }

    /**
     * Sets the Field object to be accessible if needed; this makes the code support constants in Kotlin without the
     * {@code @JvmField} annotation. This method only calls {@link Field#setAccessible} if it is needed to avoid
     * potential issues with a security manager. Within Java itself, only {@code public static final} property fields
     * are expected.
     *
     * @param field the field to process
     */
    protected void setFieldAccessibleIfNeeded(@NotNull Field field) {
        try {
            if (!Modifier.isPublic(field.getModifiers())) {
                field.setAccessible(true);
            }
        } catch (Exception e) {
            throw new ConfigMeException("Failed to modify access for field '" + field.getName() + "' from class '"
                + field.getDeclaringClass().getSimpleName() + "'", e);
        }
    }

    protected void collectSectionComments(@NotNull Class<? extends SettingsHolder> clazz) {
        SettingsHolder settingsHolder = createSettingsHolderInstance(clazz);
        settingsHolder.registerComments(commentsConfiguration);
    }

    /**
     * Creates an instance of the given settings holder class.
     *
     * @param clazz the class to instantiate
     * @param <T> the class type
     * @return instance of the class
     */
    protected <T extends SettingsHolder> @NotNull T createSettingsHolderInstance(@NotNull Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new ConfigMeException("Expected no-args constructor to be available for " + clazz, e);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new ConfigMeException("Could not create instance of " + clazz, e);
        }
    }

    /**
     * Returns all fields of the class which should be considered as potential {@link Property} definitions.
     * Considers the class's parents.
     *
     * @param clazz the class whose fields should be returned
     * @return stream of all the fields to process
     */
    protected @NotNull Stream<Field> findFieldsToProcess(@NotNull Class<?> clazz) {
        // In most cases we expect the class not to have any parent, so we check here and "fast track" this case
        if (Object.class.equals(clazz.getSuperclass())) {
            return Arrays.stream(clazz.getDeclaredFields());
        }

        List<Class<?>> classes = new ArrayList<>();
        Class<?> currentClass = clazz;
        while (currentClass != null && !currentClass.equals(Object.class)) {
            classes.add(currentClass);
            currentClass = currentClass.getSuperclass();
        }
        Collections.reverse(classes);

        return classes.stream()
            .map(Class::getDeclaredFields)
            .flatMap(Arrays::stream);
    }
}
