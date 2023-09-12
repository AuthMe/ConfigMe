package ch.jalu.configme.properties.types;

import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Property type that manages an enum set.
 *
 * @param <E> the enum type of the entries
 */
public class EnumSetPropertyType<E extends Enum<E>> extends CollectionPropertyType<E, EnumSet<E>> {

    /**
     * Constructor.
     *
     * @param enumClass the enum class the entries should have
     */
    public EnumSetPropertyType(@NotNull Class<E> enumClass) {
        this(EnumPropertyType.of(enumClass));
    }

    /**
     * Constructor.
     *
     * @param entryType enum type of the entries
     */
    public EnumSetPropertyType(@NotNull EnumPropertyType<E> entryType) {
        super(entryType);
    }

    @Override
    public @NotNull EnumPropertyType<E> getEntryType() {
        return (EnumPropertyType<E>) super.getEntryType();
    }

    public @NotNull Class<E> getEnumClass() {
        return getEntryType().getEnumClass();
    }

    @Override
    protected @NotNull Collector<E, ?, EnumSet<E>> resultCollector() {
        return Collectors.toCollection(() -> EnumSet.noneOf(getEnumClass()));
    }
}
