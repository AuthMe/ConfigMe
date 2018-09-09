package ch.jalu.configme.properties;

import ch.jalu.configme.properties.inlinearray.InlineArrayConverter;
import ch.jalu.configme.properties.types.PropertyType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PropertyBuilder<K, T, B extends PropertyBuilder<K, T, B>> {

    private String path;
    protected T defaultValue;
    private PropertyType<K> type;
    private CreateFunction<K, T> createFunction;

    private PropertyBuilder(PropertyType<K> type) {
        this.type = type;
    }

    public B createFunction(CreateFunction<K, T> createFunction) {
        this.createFunction = createFunction;

        return (B) this;
    }

    public B path(String path) {
        this.path = path;

        return (B) this;
    }

    public B defaultValue(T defaultValue) {
        this.defaultValue = defaultValue;

        return (B) this;
    }

    public Property<T> build() {
        Objects.requireNonNull(this.createFunction);

        return this.createFunction.apply(
            this.path,
            this.defaultValue,
            this.type
        );
    }

    public static <T> CommonPropertyBuilder<T, T> commonProperty(PropertyType<T> type) {
        return new CommonPropertyBuilder<T, T>(type).createFunction(CommonProperty::new);
    }

    public static <T> ListPropertyBuilder<T> listProperty(PropertyType<T> type) {
        return new ListPropertyBuilder<>(type).createFunction(ListProperty::new);
    }

    public static <T> MapPropertyBuilder<T> mapProperty(PropertyType<T> type) {
        return new MapPropertyBuilder<>(type).createFunction(MapProperty::new);
    }

    public static <T> ArrayPropertyBuilder<T> arrayProperty(PropertyType<T> type) {
        return new ArrayPropertyBuilder<>(type);
    }

    public static <T> InlineArrayPropertyBuilder<T> inlineArrayProperty(InlineArrayConverter<T> inlineConverter) {
        return new InlineArrayPropertyBuilder<>(inlineConverter);
    }

    public static class MapPropertyBuilder<T> extends PropertyBuilder<T, Map<String, T>, MapPropertyBuilder<T>> {

        private MapPropertyBuilder(PropertyType<T> type) {
            super(type);
        }

        public MapPropertyBuilder<T> defaultEntry(String key, T value) {
            if (this.defaultValue == null) {
                this.defaultValue = new HashMap<>();
            }

            this.defaultValue.put(key, value);

            return this;
        }

    }

    public static class CommonPropertyBuilder<K, T> extends PropertyBuilder<K, T, CommonPropertyBuilder<K, T>> {

        CommonPropertyBuilder(PropertyType<K> type) {
            super(type);
        }

    }

    public static class ArrayPropertyBuilder<T> extends PropertyBuilder<T, T[], ArrayPropertyBuilder<T>> {

        private InlineArrayConverter<T> convertHelper;

        ArrayPropertyBuilder(PropertyType<T> type) {
            super(type);
            this.createFunction(ArrayProperty::new);
        }

        public ArrayPropertyBuilder<T> convertHelper(InlineArrayConverter<T> convertHelper) {
            this.convertHelper = convertHelper;

            return this;
        }

        public ArrayPropertyBuilder<T> defaultValue(T... defaultValue) {
            this.defaultValue = defaultValue;

            return this;
        }
    }

    public static class InlineArrayPropertyBuilder<T> extends PropertyBuilder<T, T[], InlineArrayPropertyBuilder<T>> {

        private InlineArrayPropertyBuilder(InlineArrayConverter<T> inlineConverter) {
            super(null);
            createFunction(
                (path, defaultValue, type) -> new InlineArrayProperty<>(path, defaultValue, inlineConverter));
        }
    }

    public static class ListPropertyBuilder<T> extends PropertyBuilder<T, List<T>, ListPropertyBuilder<T>> {

        ListPropertyBuilder(PropertyType<T> type) {
            super(type);
        }

        public ListPropertyBuilder<T> defaultValue(T... defaultValue) {
            this.defaultValue = Arrays.asList(defaultValue);

            return this;
        }

    }

    @FunctionalInterface
    public interface CreateFunction<K, T> {

        Property<T> apply(String path, T defaultValue, PropertyType<K> type);

    }

}
