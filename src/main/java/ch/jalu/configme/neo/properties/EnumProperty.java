package ch.jalu.configme.neo.properties;


import ch.jalu.configme.neo.resource.PropertyReader;

/**
 * Enum property.
 *
 * @param <E> The enum class
 */
public class EnumProperty<E extends Enum<E>> extends BaseProperty<E> {

    private final Class<E> clazz;

    public EnumProperty(Class<E> clazz, String path, E defaultValue) {
        super(path, defaultValue);
        this.clazz = clazz;
    }

    @Override
    protected E getFromResource(PropertyReader reader) {
        String value = reader.getString(getPath());
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
    public Object toExportRepresentation(E value) {
        return value.name();
    }
}
