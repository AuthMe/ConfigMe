package ch.jalu.configme;

import ch.jalu.configme.configurationdata.CommentsConfiguration;

/**
 * Marker interface for classes that have Property objects.
 * <p>
 * Declare your properties as {@code public static final} fields
 * of {@link ch.jalu.configme.properties.Property} type in a class
 * which implements this interface.
 *
 * @see ch.jalu.configme.properties.PropertyInitializer
 * @see ch.jalu.configme.configurationdata.ConfigurationData
 */
public interface SettingsHolder {

    default void registerComments(CommentsConfiguration conf) {
        // noop
    }
}
