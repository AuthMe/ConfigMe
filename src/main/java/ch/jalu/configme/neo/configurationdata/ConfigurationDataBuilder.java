package ch.jalu.configme.neo.configurationdata;

import ch.jalu.configme.Comment;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.neo.SettingsHolder;
import ch.jalu.configme.neo.properties.Property;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * Utility class responsible for retrieving all {@link Property} fields
 * from {@link SettingsHolder} implementations via reflection.
 * <p>
 * Properties must be declared as {@code public static} fields or they are ignored.
 */
public class ConfigurationDataBuilder {

    private final PropertyListBuilder propertyListBuilder = new PropertyListBuilder();
    private final CommentsConfiguration commentsConfiguration = new CommentsConfiguration();

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
    // TODO: Used to be called collectData, check if this is really better (and warrants the breaking change)
    public static ConfigurationData createConfiguration(Class<? extends SettingsHolder>... classes) {
        return createConfiguration(Arrays.asList(classes));
    }

    /**
     * Collects all properties and comment data from the provided classes.
     * Properties are sorted by their group, and each group is sorted by order of encounter.
     *
     * @param classes the classes to scan for their property data
     * @return collected configuration data
     */
    public static ConfigurationData createConfiguration(Iterable<Class<? extends SettingsHolder>> classes) {
        ConfigurationDataBuilder builder = new ConfigurationDataBuilder();
        return builder.collectData(classes);
    }

    protected ConfigurationData collectData(Iterable<Class<? extends SettingsHolder>> classes) {
        for (Class<? extends SettingsHolder> clazz : classes) {
            collectProperties(clazz);
            collectSectionComments(clazz);
        }
        return new ConfigurationDataImpl(propertyListBuilder.create(), commentsConfiguration.getAllComments());
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
            commentsConfiguration.setComment(path, field.getAnnotation(Comment.class).value());
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

    protected void collectSectionComments(Class<? extends SettingsHolder> clazz) {
        SettingsHolder settingsHolder = createSettingsHolderInstance(clazz);
        settingsHolder.registerComments(commentsConfiguration);
    }

    protected <T extends SettingsHolder> T createSettingsHolderInstance(Class<T> clazz) {
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
}
