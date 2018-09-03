package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.PropertyType;

import java.util.*;

public class PropertyBuilder<K, T, P extends Property<T>, B extends PropertyBuilder<K, T, P, B>> {

    protected String path;
    protected T defaultValue;
    protected PropertyType<K> type;
    protected CreateFunction<K, T, P> createFunction;

    private PropertyBuilder(PropertyType<K> type) {
        this.type = type;
    }

    public B createFunction(CreateFunction<K, T, P> createFunction) {
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

    public P build() {
        Objects.requireNonNull(this.createFunction);

        return this.createFunction.apply(
            this.path,
            this.defaultValue,
            this.type
        );
    }

    public static <T> CommonPropertyBuilder<T, T, CommonProperty<T>> commonProperty(PropertyType<T> type) {
        return new CommonPropertyBuilder<T, T, CommonProperty<T>>(type).createFunction(CommonProperty::new);
    }

    public static <T> ListPropertyBuilder<T> listProperty(PropertyType<T> type) {
        return new ListPropertyBuilder<>(type).createFunction(ListProperty::new);
    }

    public static <T> MapPropertyBuilder<T> mapProperty(PropertyType<T> type) {
        return new MapPropertyBuilder<>(type).createFunction(MapProperty::new);
    }

    public static <T> ArrayPropertyBuilder<T> arrayProperty(PropertyType<T> type) {
        return new ArrayPropertyBuilder<>(type).createFunction(ArrayProperty::new);
    }

    public static class MapPropertyBuilder<T> extends PropertyBuilder<T, Map<String, T>, MapProperty<T>, MapPropertyBuilder<T>> {

        private MapPropertyBuilder(PropertyType<T> type) {
            super(type);
        }

        public MapPropertyBuilder<T> defaultEntry(String key, T value) {
            if (this.defaultValue == null)
                this.defaultValue = new HashMap<>();

            this.defaultValue.put(key, value);

            return this;
        }

    }

    public static class CommonPropertyBuilder<K, T, P extends Property<T>> extends PropertyBuilder<K, T, P, CommonPropertyBuilder<K, T, P>> {

        CommonPropertyBuilder(PropertyType<K> type) {
            super(type);
        }

    }

    public static class ArrayPropertyBuilder<T> extends PropertyBuilder<T, T[], ArrayProperty<T>, ArrayPropertyBuilder<T>> {

        ArrayPropertyBuilder(PropertyType<T> type) {
            super(type);
        }

        public ArrayPropertyBuilder<T> defaultValue(T... defaultValue) {
            this.defaultValue = defaultValue;

            return this;
        }

    }

    public static class ListPropertyBuilder<T> extends PropertyBuilder<T, List<T>, ListProperty<T>, ListPropertyBuilder<T>> {

        ListPropertyBuilder(PropertyType<T> type) {
            super(type);
        }

        public ListPropertyBuilder<T> defaultValue(T... defaultValue) {
            this.defaultValue = Arrays.asList(defaultValue);

            return this;
        }

    }

    @FunctionalInterface
    public interface CreateFunction<K, T, P extends Property<T>> {

        P apply(String path, T defaultValue, PropertyType<K> type);

    }

}
