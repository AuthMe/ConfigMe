package ch.jalu.configme.properties;

import ch.jalu.configme.properties.inlinearray.InlineArrayConverter;
import ch.jalu.configme.properties.types.PropertyType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.regex.Pattern;

/**
 * Convenience class for instantiating {@link Property} objects. You can use
 * a static import for the methods for a short, convenient way to declare properties.
 * <p>
 * If you use additional property types, it may make the most sense to write your own
 * property initializer class similar to this one, or extend this class to keep the
 * default methods.
 */
public class PropertyInitializer {

    protected PropertyInitializer() {
        // Protected constructor to allow inheritance
    }

    /**
     * Creates a new boolean property.
     *
     * @param path the property's path
     * @param defaultValue the default value
     * @return the created property
     */
    public static @NotNull Property<Boolean> newProperty(@NotNull String path, boolean defaultValue) {
        return new BooleanProperty(path, defaultValue);
    }

    /**
     * Creates a new integer property.
     *
     * @param path the property's path
     * @param defaultValue the default value
     * @return the created property
     */
    public static @NotNull Property<Integer> newProperty(@NotNull String path, int defaultValue) {
        return new IntegerProperty(path, defaultValue);
    }

    /**
     * Creates a new double property.
     *
     * @param path the property's path
     * @param defaultValue the default value
     * @return the created property
     */
    public static @NotNull Property<Double> newProperty(@NotNull String path, double defaultValue) {
        return new DoubleProperty(path, defaultValue);
    }

    /**
     * Creates a new String property.
     *
     * @param path the property's path
     * @param defaultValue the default value
     * @return the created property
     */
    public static @NotNull Property<String> newProperty(@NotNull String path, @NotNull String defaultValue) {
        return new StringProperty(path, defaultValue);
    }

    /**
     * Creates a new enum property.
     *
     * @param clazz the enum class
     * @param path the property's path
     * @param defaultValue the default value
     * @param <E> the enum type
     * @return the created enum property
     */
    public static <E extends Enum<E>> @NotNull Property<E> newProperty(@NotNull Class<E> clazz, @NotNull String path, @NotNull E defaultValue) {
        return new EnumProperty<>(clazz, path, defaultValue);
    }

    /**
     * Creates a new regex pattern property.
     *
     * @param path the property's path
     * @param defaultRegexValue the default pattern of the property
     * @return the created regex property
     */
    public static @NotNull RegexProperty newRegexProperty(@NotNull String path, @NotNull String defaultRegexValue) {
        return new RegexProperty(path, defaultRegexValue);
    }

    /**
     * Creates a new regex pattern property.
     *
     * @param path the property's path
     * @param defaultRegexValue the default pattern of the property
     * @return the created regex property
     */
    public static @NotNull RegexProperty newRegexProperty(@NotNull String path, @NotNull Pattern defaultRegexValue) {
        return new RegexProperty(path, defaultRegexValue);
    }

    /**
     * Creates a new String list property.
     *
     * @param path the property's path
     * @param defaultValues the items in the default list
     * @return the created list property
     */
    public static @NotNull Property<List<String>> newListProperty(@NotNull String path, @NotNull String @NotNull ... defaultValues) {
        // does not have the same name as not to clash with #newProperty(String, String)
        return new StringListProperty(path, defaultValues);
    }

    /**
     * Creates a new String list property.
     *
     * @param path the property's path
     * @param defaultValues the default value of the property
     * @return the created list property
     */
    public static @NotNull Property<List<String>> newListProperty(@NotNull String path, @NotNull List<String> defaultValues) {
        // does not have the same name as not to clash with #newProperty(String, String)
        return new StringListProperty(path, defaultValues);
    }

    /**
     * Creates a new String set property.
     *
     * @param path the property's path
     * @param defaultValues the items in the default set
     * @return the created set property
     */
    public static @NotNull Property<Set<String>> newSetProperty(@NotNull String path, @NotNull String @NotNull ... defaultValues) {
        return new StringSetProperty(path, defaultValues);
    }

    /**
     * Creates a new String set property.
     *
     * @param path the property's path
     * @param defaultValues the default value of the property
     * @return the created set property
     */
    public static @NotNull Property<Set<String>> newSetProperty(@NotNull String path, @NotNull Set<String> defaultValues) {
        return new StringSetProperty(path, defaultValues);
    }

    /**
     * Creates a new String set property where all values are lowercase.
     *
     * @param path the property's path
     * @param defaultValues the items in the default set
     * @return the created set property
     */
    public static @NotNull Property<Set<String>> newLowercaseStringSetProperty(@NotNull String path, @NotNull String @NotNull ... defaultValues) {
        return new LowercaseStringSetProperty(path, defaultValues);
    }

    /**
     * Creates a new String set property where all values are lowercase.
     *
     * @param path the property's path
     * @param defaultValues the default value of the property
     * @return the created set property
     */
    public static @NotNull Property<Set<String>> newLowercaseStringSetProperty(@NotNull String path, @NotNull Collection<String> defaultValues) {
        return new LowercaseStringSetProperty(path, defaultValues);
    }

    /**
     * Creates a new bean property.
     *
     * @param beanClass the JavaBean class
     * @param path the property's path
     * @param defaultValue default value
     * @param <B> the bean type
     * @return the created bean property
     */
    public static <B> @NotNull Property<B> newBeanProperty(@NotNull Class<B> beanClass, @NotNull String path, @NotNull B defaultValue) {
        return new BeanProperty<>(beanClass, path, defaultValue);
    }

    // --------------
    // Property builders
    // --------------
    public static <T> PropertyBuilder.@NotNull TypeBasedPropertyBuilder<T> typeBasedProperty(@NotNull PropertyType<T> type) {
        return new PropertyBuilder.TypeBasedPropertyBuilder<>(type);
    }

    public static <T> PropertyBuilder.@NotNull ListPropertyBuilder<T> listProperty(@NotNull PropertyType<T> type) {
        return new PropertyBuilder.ListPropertyBuilder<>(type);
    }

    public static <T> PropertyBuilder.@NotNull SetPropertyBuilder<T> setProperty(@NotNull PropertyType<T> type) {
        return new PropertyBuilder.SetPropertyBuilder<>(type);
    }

    public static <T> PropertyBuilder.@NotNull MapPropertyBuilder<T> mapProperty(@NotNull PropertyType<T> type) {
        return new PropertyBuilder.MapPropertyBuilder<>(type);
    }

    public static <T> PropertyBuilder.@NotNull ArrayPropertyBuilder<T> arrayProperty(@NotNull PropertyType<T> type,
                                                                                     @NotNull IntFunction<T[]> arrayProducer) {
        return new PropertyBuilder.ArrayPropertyBuilder<>(type, arrayProducer);
    }

    public static <T> PropertyBuilder.@NotNull InlineArrayPropertyBuilder<T> inlineArrayProperty(
        @NotNull InlineArrayConverter<T> inlineConverter) {
        return new PropertyBuilder.InlineArrayPropertyBuilder<>(inlineConverter);
    }

    // --------------
    // Optional flavors
    // --------------
    public static @NotNull Property<Optional<Boolean>> optionalBooleanProperty(@NotNull String path) {
        return new OptionalProperty<>(new BooleanProperty(path, false));
    }

    public static @NotNull Property<Optional<Integer>> optionalIntegerProperty(@NotNull String path) {
        return new OptionalProperty<>(new IntegerProperty(path, 0));
    }

    public static @NotNull Property<Optional<String>> optionalStringProperty(@NotNull String path) {
        return new OptionalProperty<>(new StringProperty(path, ""));
    }

    public static <E extends Enum<E>> @NotNull Property<Optional<E>> optionalEnumProperty(@NotNull Class<E> clazz, @NotNull String path) {
        // default value may never be null, so get the first entry in the enum class
        return new OptionalProperty<>(new EnumProperty<>(clazz, path, clazz.getEnumConstants()[0]));
    }
}
