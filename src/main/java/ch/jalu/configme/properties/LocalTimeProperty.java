package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.TemporalType;
import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;

/**
 * {@link LocalTime} property.
 */
public class LocalTimeProperty extends TypeBasedProperty<LocalTime> {

    /**
     * Constructor.
     *
     * @param path         the path of the property
     * @param defaultValue the default value of the property
     */
    public LocalTimeProperty(@NotNull String path, @NotNull LocalTime defaultValue) {
        super(path, TemporalType.LOCAL_TIME, defaultValue);
    }
}
