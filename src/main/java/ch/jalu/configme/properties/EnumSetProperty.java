package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.EnumSetPropertyType;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

/**
 * EnumSet property.
 *
 * @param <E> the enum type
 */
public class EnumSetProperty<E extends Enum<E>> extends SetProperty<E> {

    public EnumSetProperty(@NotNull String path, @NotNull Class<E> enumClass, @NotNull EnumSet<E> defaultValue) {
        super(path, new EnumSetPropertyType(enumClass), defaultValue);
    }

    public EnumSetProperty(@NotNull String path, @NotNull Class<E> enumClass, @NotNull E @NotNull... defaultValue) {
        super(path, new EnumSetPropertyType(enumClass), defaultValue);
    }
}
