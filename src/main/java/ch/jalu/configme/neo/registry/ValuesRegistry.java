package ch.jalu.configme.neo.registry;

import ch.jalu.configme.neo.configurationdata.ConfigurationData;
import ch.jalu.configme.neo.properties.Property;
import ch.jalu.configme.neo.resource.PropertyReader;

// TODO: Better name?
// TODO: javadoc that this should be basically be a dumb map that performs no validation whatsoever.
   // Or should it? Otherwise we allow invalid values to be set in the migration service...
public interface ValuesRegistry {

    <T> T get(Property<T> property);

    <T> void set(Property<T> property, T value);

    void initializeValues(PropertyReader reader, ConfigurationData configurationData);

    // TODO int size(); ?
    // TODO getKeys() ?
}
