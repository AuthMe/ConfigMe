package ch.jalu.configme.properties;

import ch.jalu.configme.properties.inlinearray.InlineArrayConverter;
import ch.jalu.configme.properties.types.PropertyType;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

/**
 * Builder for complex types of properties.
 *
 * @param <K> the PropertyType type the builder makes use of
 * @param <T> the type of Property the builder produces
 * @param <B> builder extension (concrete class extending this builder)
 * @see PropertyInitializer
 */
public abstract class PropertyBuilder<K, T, B extends PropertyBuilder<K, T, B>> {

    private String path;
    private T defaultValue;
    private PropertyType<K> type;

    /**
     * Constructor.
     *
     * @param type the property type
     */
    public PropertyBuilder(PropertyType<K> type) {
        this.type = type;
    }

    /**
     * Sets the path of the property.
     *
     * @param path the path
     * @return this builder
     */
    public B path(String path) {
        this.path = path;
        return (B) this;
    }

    /**
     * Sets the default of the property.
     *
     * @param defaultValue the default value to set
     * @return this builder
     */
    public B defaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
        return (B) this;
    }

    /**
     * Creates a property with the configured details. All mandatory settings must have been set beforehand.
     *
     * @return the created property
     */
    public abstract Property<T> build();

    protected final String getPath() {
        return path;
    }

    protected final T getDefaultValue() {
        return defaultValue;
    }

    protected final PropertyType<K> getType() {
        return type;
    }

    /**
     * Builder for {@link MapProperty}.
     *
     * @param <T> the value type of the map
     */
    public static class MapPropertyBuilder<T> extends PropertyBuilder<T, Map<String, T>, MapPropertyBuilder<T>> {

        public MapPropertyBuilder(PropertyType<T> type) {
            super(type);
            defaultValue(new LinkedHashMap<>());
        }

        public MapPropertyBuilder<T> defaultEntry(String key, T value) {
            getDefaultValue().put(key, value);
            return this;
        }

        @Override
        public MapProperty<T> build() {
            return new MapProperty<>(getPath(), getDefaultValue(), getType());
        }
    }

    /**
     * Builder for {@link TypeBasedProperty}.
     *
     * @param <T> the value type
     */
    public static class TypeBasedPropertyBuilder<T> extends PropertyBuilder<T, T, TypeBasedPropertyBuilder<T>> {

        private CreateFunction<T, T> createFunction = TypeBasedProperty::new;

        public TypeBasedPropertyBuilder(PropertyType<T> type) {
            super(type);
        }

        public TypeBasedPropertyBuilder<T> createFunction(CreateFunction<T, T> createFunction) {
            this.createFunction = createFunction;
            return this;
        }

        @Override
        public Property<T> build() {
            return createFunction.apply(getPath(), getDefaultValue(), getType());
        }
    }

    /**
     * Builder for {@link ArrayProperty}.
     *
     * @param <T> the type of the elements in the array
     */
    public static class ArrayPropertyBuilder<T> extends PropertyBuilder<T, T[], ArrayPropertyBuilder<T>> {

        private final IntFunction<T[]> arrayProducer;

        public ArrayPropertyBuilder(PropertyType<T> type, IntFunction<T[]> arrayProducer) {
            super(type);
            this.arrayProducer = arrayProducer;
        }

        @Override
        public ArrayPropertyBuilder<T> defaultValue(T... defaultValue) {
            return super.defaultValue(defaultValue);
        }

        @Override
        public Property<T[]> build() {
            return new ArrayProperty<>(getPath(), getDefaultValue(), getType(), arrayProducer);
        }
    }

    /**
     * Builder for {@link InlineArrayProperty}.
     *
     * @param <T> the type of the elements in the array
     */
    public static class InlineArrayPropertyBuilder<T> extends PropertyBuilder<T, T[], InlineArrayPropertyBuilder<T>> {

        private InlineArrayConverter<T> inlineConverter;

        public InlineArrayPropertyBuilder(InlineArrayConverter<T> inlineConverter) {
            super(null);
            this.inlineConverter = inlineConverter;
        }

        @Override
        public InlineArrayPropertyBuilder<T> defaultValue(T... defaultValue) {
            return super.defaultValue(defaultValue);
        }

        @Override
        public Property<T[]> build() {
            return new InlineArrayProperty<>(getPath(), getDefaultValue(), inlineConverter);
        }
    }

    /**
     * Builder for {@link ListProperty}.
     *
     * @param <T> the type of the elements in the list
     */
    public static class ListPropertyBuilder<T> extends PropertyBuilder<T, List<T>, ListPropertyBuilder<T>> {

        public ListPropertyBuilder(PropertyType<T> type) {
            super(type);
        }

        public ListPropertyBuilder<T> defaultValue(T... defaultValue) {
            return super.defaultValue(Arrays.asList(defaultValue));
        }

        @Override
        public Property<List<T>> build() {
            return new ListProperty<>(getPath(), getType(), getDefaultValue());
        }
    }

    /**
     * Function taking three arguments, which returns a property of the given type.
     *
     * @param <K> the PropertyType used internally by the property
     * @param <T> the actual value type of the Property
     */
    @FunctionalInterface
    public interface CreateFunction<K, T> {

        Property<T> apply(String path, T defaultValue, PropertyType<K> type);

    }

}
