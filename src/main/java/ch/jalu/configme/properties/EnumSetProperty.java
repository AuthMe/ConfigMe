package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.EnumSetPropertyType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * EnumSet property.
 *
 * @param <E> the enum type
 */
public class EnumSetProperty<E extends Enum<E>> extends SetProperty<E> {

    public EnumSetProperty(@NotNull String path, @NotNull Class<E> enumClass, @NotNull EnumSet<E> defaultValue) {
        super(new EnumSetPropertyType(enumClass), path, defaultValue);
    }

    public EnumSetProperty(@NotNull String path, @NotNull Class<E> enumClass, @NotNull E @NotNull... defaultValue) {
        super(new EnumSetPropertyType(enumClass), path, newEnumSet(enumClass, defaultValue));
    }

    private static <E extends Enum<E>> @NotNull Set<E> newEnumSet(@NotNull Class<E> enumClass,
                                                                  E @NotNull [] defaultValue) {
        EnumSet<E> enumSet = EnumSet.noneOf(enumClass);
        enumSet.addAll(Arrays.asList(defaultValue));
        return enumSet;
    }
}
