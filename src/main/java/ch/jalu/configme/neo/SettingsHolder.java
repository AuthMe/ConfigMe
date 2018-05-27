package ch.jalu.configme.neo;

import ch.jalu.configme.neo.configurationdata.ConfigurationData;

/**
 * Marker interface for classes that have Property objects.
 * <p>
 * Declare your properties as {@code public static final} fields
 * of {@link ch.jalu.configme.neo.properties.Property} type in a class
 * which implements this interface.
 *
 * @see ch.jalu.configme.neo.properties.PropertyInitializer
 * @see ch.jalu.configme.neo.configurationdata.ConfigurationData
 */
public interface SettingsHolder {

    default void registerComments(ConfigurationData configurationData) {
        // noop
    }
}
