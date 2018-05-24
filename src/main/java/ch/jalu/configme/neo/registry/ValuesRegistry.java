package ch.jalu.configme.neo.registry;

import ch.jalu.configme.neo.configurationdata.ConfigurationData;
import ch.jalu.configme.neo.properties.Property;
import ch.jalu.configme.neo.resource.PropertyReader;

// TODO: Better name?
// TODO: javadoc that this should be basically be a dumb map that performs no validation whatsoever.
public interface ValuesRegistry {

    <T> T get(Property<T> property);

    <T> void set(Property<T> property, T value);

    default void setAllProperties(PropertyReader reader, ConfigurationData configurationData) {
        configurationData.getAllProperties().forEach(
            (Property property) -> set(property, property.getValue(reader)));
    }

    // TODO int size(); ?
    // TODO getKeys() ?
}
