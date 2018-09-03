package ch.jalu.configme.properties.types;

import ch.jalu.configme.resource.PropertyReader;

public class EnumPropertyType<E extends Enum<E>> implements PropertyType<E> {

    private Class<E> enumType;

    private EnumPropertyType(Class<E> enumType) {
        this.enumType = enumType;
    }

    @Override
    public E get(PropertyReader reader, String path) {
        return this.convert(reader.getString(path));
    }

    @Override
    public E convert(Object object) {
        if (object instanceof Enum) {
            return (E) object;
        }

        if (!(object instanceof String)) {
            return null;
        }

        String name = object.toString().toLowerCase();

        for (E entry : enumType.getEnumConstants()) {
            if (entry.name().equalsIgnoreCase(name)) {
                return entry;
            }
        }

        return null;
    }

    @Override
    public Class<E> getType() {
        return null;
    }

    public Object toExportValue(E value) {
        return value.name();
    }

    static <E extends Enum<E>> EnumPropertyType<E> of(Class<E> type) {
        return new EnumPropertyType<>(type);
    }

}
