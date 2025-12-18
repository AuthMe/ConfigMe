package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.TemporalType;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

/**
 * {@link LocalDate} property.
 */
public class LocalDateProperty extends TypeBasedProperty<LocalDate> {

    /**
     * Constructor.
     *
     * @param path         the path of the property
     * @param defaultValue the default value of the property
     */
    public LocalDateProperty(@NotNull String path, @NotNull LocalDate defaultValue) {
        super(path, TemporalType.LOCAL_DATE, defaultValue);
    }
}
