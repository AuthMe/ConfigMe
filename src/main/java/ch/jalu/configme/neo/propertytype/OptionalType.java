package ch.jalu.configme.neo.propertytype;

import ch.jalu.configme.neo.resource.PropertyReader;

import java.util.Optional;

public class OptionalType<T> extends NonNullPropertyType<Optional<T>> {

    private final PropertyType<T> baseType;

    public OptionalType(PropertyType<T> baseType) {
        this.baseType = baseType;
    }

    @Override
    public Optional<T> getFromReader(PropertyReader reader, String path) {
        return Optional.ofNullable(baseType.getFromReader(reader, path));
    }

    @Override
    public Object toExportValue(Optional<T> value) {
        return value.map(baseType::toExportValue).orElse(null);
    }

    @Override
    public boolean isPresent(PropertyReader reader, String path) {
        // getFromReader will never return null, and always returning true here prevents this
        // optional(!) property type from triggering migrations
        return true;
    }
}
