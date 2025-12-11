package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.TemporalType;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * {@link LocalDateTime} property.
 */
public class LocalDateTimeProperty extends TypeBasedProperty<LocalDateTime> {

    /**
     * Constructor.
     *
     * @param path         the path of the property
     * @param defaultValue the default value of the property
     */
    public LocalDateTimeProperty(@NotNull String path, @NotNull LocalDateTime defaultValue) {
        super(path, TemporalType.LOCAL_DATE_TIME, defaultValue);
    }
}
