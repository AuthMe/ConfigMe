package ch.jalu.configme.properties.types;

@SuppressWarnings("unchecked")
public class EnumPropertyType<E extends Enum<E>> implements PropertyType<E> {

    private Class<E> enumType;

    public EnumPropertyType(Class<E> enumType) {
        this.enumType = enumType;
    }

    @Override
    public E convert(Object object) {
        // If object is enum, then return this object, casting to E
        if (this.enumType.isInstance(object)) {
            return (E) object;
        }

        // If object is not string, then we return null, because else we cant find a enum
        if (!(object instanceof String)) {
            return null;
        }

        String name = object.toString().toLowerCase();

        // Try find enum by name. If successfully find, then return result c:
        for (E entry : this.enumType.getEnumConstants()) {
            if (entry.name().equalsIgnoreCase(name)) {
                return entry;
            }
        }

        return null;
    }

    @Override
    public Class<E> getType() {
        return this.enumType;
    }

    public Object toExportValue(E value) {
        return value.name();
    }

    static <E extends Enum<E>> EnumPropertyType<E> of(Class<E> type) {
        return new EnumPropertyType<>(type);
    }

}
