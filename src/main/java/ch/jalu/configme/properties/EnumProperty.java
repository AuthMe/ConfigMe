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
        // TODO: Review - hacky things like this should be avoided in the future, maybe by actually reading all values
        // into some registry and then never working with the resource anymore. This should also hopefully be more
        // efficient since conversions / sanity checks etc. only take place once.
        // Consider that this might require us to first validate that no properties have overlapping paths (#24)
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
