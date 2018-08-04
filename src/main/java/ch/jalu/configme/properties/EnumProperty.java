package ch.jalu.configme.properties;


import ch.jalu.configme.resource.PropertyResource;

/**
 * Enum property.
 *
 * @param <E> The enum class
 */
public class EnumProperty<E extends Enum<E>> extends Property<E> {

    private final Class<E> clazz;

    public EnumProperty(Class<E> clazz, String path, E defaultValue) {
        super(path, defaultValue);
        this.clazz = clazz;
    }

    @Override
    protected E getFromResource(PropertyResource resource) {
        // Value is read from file as a String, but when it is set later on it is an enum
        Object value = resource.getObject(getPath());
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }

        if (value instanceof String) {
            String textValue = (String) value;
            return mapToEnum(textValue);
        }
        return null;
    }

    private E mapToEnum(String value) {
        for (E entry : clazz.getEnumConstants()) {
            if (entry.name().equalsIgnoreCase(value)) {
                return entry;
            }
        }
        return null;
    }
}
