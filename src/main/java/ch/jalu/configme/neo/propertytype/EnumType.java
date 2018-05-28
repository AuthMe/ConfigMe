package ch.jalu.configme.neo.propertytype;

import ch.jalu.configme.neo.resource.PropertyReader;

public class EnumType<E extends Enum<E>> extends NonNullPropertyType<E> {

    private final Class<E> clazz;

    public EnumType(Class<E> clazz) {
        this.clazz = clazz;
    }

    @Override
    public E getFromReader(PropertyReader reader, String path) {
        String value = reader.getString(path);
        return value == null ? null : mapToEnum(value);
    }

    private E mapToEnum(String value) {
        for (E entry : clazz.getEnumConstants()) {
            if (entry.name().equalsIgnoreCase(value)) {
                return entry;
            }
        }
        return null;
    }

    @Override
    public Object toExportValue(E value) {
        return value.name();
    }
}
