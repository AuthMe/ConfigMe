package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.EnumPropertyType;
import org.jetbrains.annotations.NotNull;

/**
 * Enum property.
 *
 * @param <E> the enum type
 */
public class EnumProperty<E extends Enum<E>> extends TypeBasedProperty<E> {

    public EnumProperty(@NotNull String path, @NotNull Class<E> clazz, @NotNull E defaultValue) {
        super(path, EnumPropertyType.of(clazz), defaultValue);
    }
}
