package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.EnumPropertyType;

/**
 * Enum property.
 *
 * @param <E> the enum type
 */
public class EnumProperty<E extends Enum<E>> extends TypeBasedProperty<E> {

    public EnumProperty(Class<E> clazz, String path, E defaultValue) {
        super(path, defaultValue, EnumPropertyType.of(clazz));
    }
}
