package ch.jalu.configme.neo.registry;

import ch.jalu.configme.neo.configurationdata.ConfigurationData;
import ch.jalu.configme.neo.properties.Property;
import ch.jalu.configme.neo.resource.PropertyReader;

import java.util.HashMap;
import java.util.Map;

public class DefaultValueRegistry implements ValuesRegistry {

    private final Map<String, Object> values = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Property<T> property) {
        return (T) values.get(property.getPath());
    }

    @Override
    public <T> void set(Property<T> property, T value) {
        values.put(property.getPath(), value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initializeValues(PropertyReader reader, ConfigurationData configurationData) {
        values.clear();
        configurationData.getAllProperties()
            .forEach(property -> set((Property) property, property.getValue(reader)));
    }
}
