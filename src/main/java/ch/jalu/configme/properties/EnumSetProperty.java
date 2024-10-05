package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.EnumSetPropertyType;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * Introduce an EnumSetProperty that goes along with EnumSetPropertyType.
 *
 * @param <E> enum
 */
public class EnumSetProperty<E extends Enum<E>> extends SetProperty<Set<E>> {

    public EnumSetProperty(String path, Class<E> enumClass, EnumSet<E> defaultValue) {
        super(path, new EnumSetPropertyType(enumClass), defaultValue);
    }

    public EnumSetProperty(String path, Class<E> enumClass, E... defaultValue) {
        super(path, new EnumSetPropertyType(enumClass), castToEnumSet(enumClass, defaultValue));
    }

    private static <E extends Enum<E>> Set<E> castToEnumSet(Class<E> enumClass, E[] defaultValue) {
        EnumSet<E> enumSet = EnumSet.noneOf(enumClass);
        if (defaultValue != null) {
            enumSet.addAll(Arrays.asList(defaultValue));
        }
        return enumSet;
    }
}
