package ch.jalu.configme.properties;

import ch.jalu.configme.properties.inlinearray.InlineArrayConverter;
import ch.jalu.configme.properties.types.PropertyType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

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
    private final PropertyType<K> type;

    /**
     * Constructor.
     *
     * @param type the property type
     */
    public PropertyBuilder(@Nullable PropertyType<K> type) {
        this.type = type;
    }

    /**
     * Sets the path of the property.
     *
     * @param path the path
     * @return this builder
     */
    public @NotNull B path(@NotNull String path) {
        this.path = path;
        return (B) this;
    }

    /**
     * Sets the default of the property.
     *
     * @param defaultValue the default value to set
     * @return this builder
     */
    public @NotNull B defaultValue(@NotNull T defaultValue) {
        this.defaultValue = defaultValue;
        return (B) this;
    }

    /**
     * Creates a property with the configured details. All mandatory settings must have been set beforehand.
     *
     * @return the created property
     */
    public abstract @NotNull Property<T> build();


    // Note: these getters are @Nullable because they may be null only in some implementations, or because a caller
    // might have forgotten to set a value (e.g. the path). Some values are never valid as null, but _can_ be null here.

    protected final @Nullable String getPath() {
        return path;
    }

    protected final @Nullable T getDefaultValue() {
        return defaultValue;
    }

    protected final @Nullable PropertyType<K> getType() {
        return type;
    }

    /**
     * Builder for {@link MapProperty}.
     *
     * @param <T> the value type of the map
     */
    public static class MapPropertyBuilder<T> extends PropertyBuilder<T, Map<String, T>, MapPropertyBuilder<T>> {

        public MapPropertyBuilder(@NotNull PropertyType<T> type) {
            super(type);
            defaultValue(new LinkedHashMap<>());
        }

        public @NotNull MapPropertyBuilder<T> defaultEntry(@NotNull String key, @NotNull T value) {
            getDefaultValue().put(key, value);
            return this;
        }

        @Override
        public @NotNull MapProperty<T> build() {
            return new MapProperty<>(getPath(), getType(), getDefaultValue());
        }
    }

    /**
     * Builder for {@link TypeBasedProperty}.
     *
     * @param <T> the value type
     */
    public static class TypeBasedPropertyBuilder<T> extends PropertyBuilder<T, T, TypeBasedPropertyBuilder<T>> {

        private CreateFunction<T, T> createFunction = TypeBasedProperty::new;

        public TypeBasedPropertyBuilder(@NotNull PropertyType<T> type) {
            super(type);
        }

        public @NotNull TypeBasedPropertyBuilder<T> createFunction(@NotNull CreateFunction<T, T> createFunction) {
            this.createFunction = createFunction;
            return this;
        }

        @Override
        public @NotNull Property<T> build() {
            return createFunction.apply(getPath(), getType(), getDefaultValue());
        }
    }

    /**
     * Builder for {@link ArrayProperty}.
     *
     * @param <T> the type of the elements in the array
     */
    public static class ArrayPropertyBuilder<T> extends PropertyBuilder<T, T[], ArrayPropertyBuilder<T>> {

        private final IntFunction<T[]> arrayProducer;

        public ArrayPropertyBuilder(@NotNull PropertyType<T> type, @NotNull IntFunction<T[]> arrayProducer) {
            super(type);
            this.arrayProducer = arrayProducer;
        }

        @Override
        public @NotNull ArrayPropertyBuilder<T> defaultValue(@NotNull T @NotNull ... defaultValue) {
            return super.defaultValue(defaultValue);
        }

        @Override
        public @NotNull Property<T[]> build() {
            return new ArrayProperty<>(getPath(), getType(), getDefaultValue(), arrayProducer);
        }
    }

    /**
     * Builder for {@link InlineArrayProperty}.
     *
     * @param <T> the type of the elements in the array
     */
    public static class InlineArrayPropertyBuilder<T> extends PropertyBuilder<T, T[], InlineArrayPropertyBuilder<T>> {

        private InlineArrayConverter<T> inlineConverter;

        public InlineArrayPropertyBuilder(@NotNull InlineArrayConverter<T> inlineConverter) {
            super(null);
            this.inlineConverter = inlineConverter;
        }

        @Override
        public @NotNull InlineArrayPropertyBuilder<T> defaultValue(@NotNull T @NotNull ... defaultValue) {
            return super.defaultValue(defaultValue);
        }

        @Override
        public @NotNull Property<T[]> build() {
            return new InlineArrayProperty<>(getPath(), getDefaultValue(), inlineConverter);
        }
    }

    /**
     * Builder for {@link ListProperty}.
     *
     * @param <T> the type of the elements in the list
     */
    public static class ListPropertyBuilder<T> extends PropertyBuilder<T, List<T>, ListPropertyBuilder<T>> {

        public ListPropertyBuilder(@NotNull PropertyType<T> type) {
            super(type);
        }

        public @NotNull ListPropertyBuilder<T> defaultValue(@NotNull T @NotNull ... defaultValue) {
            return super.defaultValue(Arrays.asList(defaultValue));
        }

        @Override
        public @NotNull Property<List<T>> build() {
            return new ListProperty<>(getPath(), getType(), getDefaultValue());
        }
    }

    /**
     * Builder for {@link SetProperty}.
     *
     * @param <T> the type of the elements in the set
     */
    public static class SetPropertyBuilder<T> extends PropertyBuilder<T, Set<T>, SetPropertyBuilder<T>> {

        public SetPropertyBuilder(@NotNull PropertyType<T> type) {
            super(type);
        }

        public @NotNull SetPropertyBuilder<T> defaultValue(@NotNull T @NotNull ... defaultValue) {
            Set<T> defaultSet = Arrays.stream(defaultValue)
                .collect(Collectors.toCollection(LinkedHashSet::new));
            return super.defaultValue(defaultSet);
        }

        @Override
        public @NotNull Property<Set<T>> build() {
            return new SetProperty<>(getPath(), getType(), getDefaultValue());
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

        @NotNull Property<T> apply(@NotNull String path, @NotNull PropertyType<K> type, @NotNull T defaultValue);

    }

}
