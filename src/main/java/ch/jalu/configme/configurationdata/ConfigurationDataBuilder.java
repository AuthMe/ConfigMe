package ch.jalu.configme.configurationdata;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.internal.ReflectionHelper;
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
 * Utility class responsible for creating {@link ConfigurationData} by retrieving {@link Property} fields
 * from {@link SettingsHolder} implementations and gathering all comments.
 */
public class ConfigurationDataBuilder {

    private final @NotNull PropertyListBuilder propertyListBuilder;
    private final @NotNull CommentsConfiguration commentsConfiguration;

    /**
     * Constructor. Use {@link #createConfiguration(Class[])} or a similar static method to create configuration data.
     * Use the constructors of this class only if you are overriding specific behavior.
     */
    protected ConfigurationDataBuilder() {
        this(new PropertyListBuilder(), new CommentsConfiguration());
    }

    /**
     * Constructor. Use {@link #createConfiguration(Class[])} or a similar static method to create configuration data.
     * Use the constructors of this class only if you are overriding specific behavior.
     *
     * @param propertyListBuilder property list builder to order and validate property paths
     * @param commentsConfiguration comments configuration to keep track of all comments
     */
    public ConfigurationDataBuilder(@NotNull PropertyListBuilder propertyListBuilder,
                                    @NotNull CommentsConfiguration commentsConfiguration) {
        this.propertyListBuilder = propertyListBuilder;
        this.commentsConfiguration = commentsConfiguration;
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

    /**
     * Manually creates configuration data with the given properties, without any comments. Note that the given
     * properties must be in an order that is suitable for exporting. For instance, the default YAML file resource
     * requires that all properties with the same parent be grouped together (see {@link PropertyListBuilder}).
     *
     * @param properties the properties that make up the configuration data
     * @return configuration data with the given properties
     */
    public static @NotNull ConfigurationData createConfiguration(@NotNull List<? extends Property<?>> properties) {
        return new ConfigurationDataImpl(properties, Collections.emptyMap());
    }

    /**
     * Manually creates configuration data with the given properties and comments. Note that the given
     * properties must be in an order that is suitable for exporting. For instance, the default YAML file resource
     * requires that all properties with the same parent be grouped together.
     *
     * @param properties the properties that make up the configuration data
     * @param commentsConfiguration the comments to include in the export
     * @return configuration data with the given properties
     */
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
    public @NotNull ConfigurationData collectData(@NotNull Iterable<Class<? extends SettingsHolder>> classes) {
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

    protected final @NotNull PropertyListBuilder getPropertyListBuilder() {
        return propertyListBuilder;
    }

    protected final @NotNull CommentsConfiguration getCommentsConfiguration() {
        return commentsConfiguration;
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
                ReflectionHelper.setAccessibleIfNeeded(field);
                return (Property<?>) field.get(null);
            } catch (IllegalAccessException e) {
                throw new ConfigMeException("Could not fetch field '" + field.getName() + "' from class '"
                    + field.getDeclaringClass().getSimpleName() + "'. Is it maybe not public?", e);
            }
        }
        return null;
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
            ReflectionHelper.setAccessibleIfNeeded(constructor);
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new ConfigMeException("Expected no-arg constructor to be available for " + clazz, e);
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
