package ch.jalu.configme.properties;

import ch.jalu.configme.properties.inlinearray.InlineArrayConverter;
import ch.jalu.configme.properties.types.PropertyType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
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
    public static @NotNull BooleanProperty newProperty(@NotNull String path, boolean defaultValue) {
        return new BooleanProperty(path, defaultValue);
    }

    /**
     * Creates a new short property.
     *
     * @param path the property's path
     * @param defaultValue the default value
     * @return the created property
     */
    public static @NotNull ShortProperty newProperty(@NotNull String path, short defaultValue) {
        return new ShortProperty(path, defaultValue);
    }

    /**
     * Creates a new integer property.
     *
     * @param path the property's path
     * @param defaultValue the default value
     * @return the created property
     */
    public static @NotNull IntegerProperty newProperty(@NotNull String path, int defaultValue) {
        return new IntegerProperty(path, defaultValue);
    }

    /**
     * Creates a new long property.
     *
     * @param path the property's path
     * @param defaultValue the default value
     * @return the created property
     */
    public static @NotNull LongProperty newProperty(@NotNull String path, long defaultValue) {
        return new LongProperty(path, defaultValue);
    }

    /**
     * Creates a new float property.
     *
     * @param path the property's path
     * @param defaultValue the default value
     * @return the created property
     */
    public static @NotNull FloatProperty newProperty(@NotNull String path, float defaultValue) {
        return new FloatProperty(path, defaultValue);
    }

    /**
     * Creates a new double property.
     *
     * @param path the property's path
     * @param defaultValue the default value
     * @return the created property
     */
    public static @NotNull DoubleProperty newProperty(@NotNull String path, double defaultValue) {
        return new DoubleProperty(path, defaultValue);
    }

    /**
     * Creates a new String property.
     *
     * @param path the property's path
     * @param defaultValue the default value
     * @return the created property
     */
    public static @NotNull StringProperty newProperty(@NotNull String path, @NotNull String defaultValue) {
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
    public static <E extends Enum<E>> @NotNull EnumProperty<E> newProperty(@NotNull Class<E> clazz,
                                                                           @NotNull String path,
                                                                           @NotNull E defaultValue) {
        return new EnumProperty<>(path, clazz, defaultValue);
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
    public static @NotNull StringListProperty newListProperty(@NotNull String path,
                                                              @NotNull String @NotNull ... defaultValues) {
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
    public static @NotNull StringListProperty newListProperty(@NotNull String path,
                                                              @NotNull List<String> defaultValues) {
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
    public static @NotNull StringSetProperty newSetProperty(@NotNull String path,
                                                            @NotNull String @NotNull ... defaultValues) {
        return new StringSetProperty(path, defaultValues);
    }

    /**
     * Creates a new String set property.
     *
     * @param path the property's path
     * @param defaultValues the default value of the property
     * @return the created set property
     */
    public static @NotNull StringSetProperty newSetProperty(@NotNull String path,
                                                            @NotNull Set<String> defaultValues) {
        return new StringSetProperty(path, defaultValues);
    }

    /**
     * Creates a new String set property where all values are lowercase.
     *
     * @param path the property's path
     * @param defaultValues the items in the default set
     * @return the created set property
     */
    public static @NotNull LowercaseStringSetProperty newLowercaseStringSetProperty(@NotNull String path,
                                                                           @NotNull String @NotNull ... defaultValues) {
        return new LowercaseStringSetProperty(path, defaultValues);
    }

    /**
     * Creates a new String set property where all values are lowercase.
     *
     * @param path the property's path
     * @param defaultValues the default value of the property
     * @return the created set property
     */
    public static @NotNull LowercaseStringSetProperty newLowercaseStringSetProperty(@NotNull String path,
                                                                            @NotNull Collection<String> defaultValues) {
        return new LowercaseStringSetProperty(path, defaultValues);
    }

    /**
     * Creates a new bean property.
     *
     * @param path the property's path
     * @param beanClass the JavaBean class
     * @param defaultValue default value
     * @param <B> the bean type
     * @return the created bean property
     */
    public static <B> @NotNull BeanProperty<B> newBeanProperty(@NotNull Class<B> beanClass, @NotNull String path,
                                                               @NotNull B defaultValue) {
        return new BeanProperty<>(path, beanClass, defaultValue);
    }

    // --------------
    // Property builders
    // --------------

    @NotNull
    public static <T> PropertyBuilder.TypeBasedPropertyBuilder<T> typeBasedProperty(@NotNull PropertyType<T> type) {
        return new PropertyBuilder.TypeBasedPropertyBuilder<>(type);
    }

    @NotNull
    public static <T> PropertyBuilder.ListPropertyBuilder<T> listProperty(@NotNull PropertyType<T> type) {
        return new PropertyBuilder.ListPropertyBuilder<>(type);
    }

    @NotNull
    public static <T> PropertyBuilder.SetPropertyBuilder<T> setProperty(@NotNull PropertyType<T> type) {
        return new PropertyBuilder.SetPropertyBuilder<>(type);
    }

    @NotNull
    public static <T> PropertyBuilder.MapPropertyBuilder<T> mapProperty(@NotNull PropertyType<T> type) {
        return new PropertyBuilder.MapPropertyBuilder<>(type);
    }

    @NotNull
    public static <T> PropertyBuilder.ArrayPropertyBuilder<T> arrayProperty(@NotNull PropertyType<T> type,
                                                                            @NotNull IntFunction<T[]> arrayProducer) {
        return new PropertyBuilder.ArrayPropertyBuilder<>(type, arrayProducer);
    }

    @NotNull
    public static <T> PropertyBuilder.InlineArrayPropertyBuilder<T> inlineArrayProperty(
                                                                     @NotNull InlineArrayConverter<T> inlineConverter) {
        return new PropertyBuilder.InlineArrayPropertyBuilder<>(inlineConverter);
    }

    // --------------
    // Optional flavors
    // --------------
    public static @NotNull OptionalProperty<Boolean> optionalBooleanProperty(@NotNull String path) {
        return new OptionalProperty<>(new BooleanProperty(path, false));
    }

    public static @NotNull OptionalProperty<Short> optionalShortProperty(@NotNull String path) {
        return new OptionalProperty<>(new ShortProperty(path, (short) 0));
    }

    public static @NotNull OptionalProperty<Integer> optionalIntegerProperty(@NotNull String path) {
        return new OptionalProperty<>(new IntegerProperty(path, 0));
    }

    public static @NotNull OptionalProperty<Long> optionalLongProperty(@NotNull String path) {
        return new OptionalProperty<>(new LongProperty(path, 0L));
    }

    public static @NotNull OptionalProperty<Float> optionalFloatProperty(@NotNull String path) {
        return new OptionalProperty<>(new FloatProperty(path, 0f));
    }

    public static @NotNull OptionalProperty<Double> optionalDoubleProperty(@NotNull String path) {
        return new OptionalProperty<>(new DoubleProperty(path, 0.0));
    }

    public static @NotNull OptionalProperty<String> optionalStringProperty(@NotNull String path) {
        return new OptionalProperty<>(new StringProperty(path, ""));
    }

    public static <E extends Enum<E>> @NotNull OptionalProperty<E> optionalEnumProperty(@NotNull Class<E> clazz,
                                                                                        @NotNull String path) {
        // default value may never be null, so get the first entry in the enum class
        return new OptionalProperty<>(new EnumProperty<>(path, clazz, clazz.getEnumConstants()[0]));
    }

    public static @NotNull OptionalProperty<Pattern> optionalRegexProperty(@NotNull String path) {
        return new OptionalProperty<>(new RegexProperty(path, ""));
    }

    public static @NotNull OptionalProperty<List<String>> optionalListProperty(@NotNull String path) {
        return new OptionalProperty<>(new StringListProperty(path));
    }

    public static @NotNull OptionalProperty<Set<String>> optionalSetProperty(@NotNull String path) {
        return new OptionalProperty<>(new StringSetProperty(path));
    }
}
