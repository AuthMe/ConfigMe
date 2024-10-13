package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.EnumSetPropertyType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.EnumSet;

/**
 * EnumSet property. The property's values are mutable and in declaration order of the enum.
 *
 * @param <E> the enum type
 */
public class EnumSetProperty<E extends Enum<E>> extends CollectionProperty<E, EnumSet<E>> {

    public EnumSetProperty(@NotNull String path, @NotNull Class<E> enumClass, @NotNull EnumSet<E> defaultValue) {
        super(path, new EnumSetPropertyType<>(enumClass), defaultValue);
    }

    @SafeVarargs
    public EnumSetProperty(@NotNull String path, @NotNull Class<E> enumClass, @NotNull E @NotNull ... defaultValue) {
        super(path, new EnumSetPropertyType<>(enumClass), newEnumSet(enumClass, defaultValue));
    }

    private static <E extends Enum<E>> @NotNull EnumSet<E> newEnumSet(@NotNull Class<E> enumClass,
                                                                      E @NotNull [] defaultValue) {
        EnumSet<E> enumSet = EnumSet.noneOf(enumClass);
        enumSet.addAll(Arrays.asList(defaultValue));
        return enumSet;
    }
}
