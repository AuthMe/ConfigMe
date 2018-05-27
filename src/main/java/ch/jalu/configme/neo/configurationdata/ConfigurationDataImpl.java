package ch.jalu.configme.neo.configurationdata;

import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.neo.properties.Property;
import ch.jalu.configme.neo.resource.PropertyReader;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains information about the available properties and their associated comments.
 */
// TODO: Do we really need an interface for this? This class is probably fine on its own and would keep old code working.
public class ConfigurationDataImpl implements ConfigurationData {

    private final List<Property<?>> properties;
    private final Map<String, List<String>> sectionComments;

    public ConfigurationDataImpl(List<? extends Property<?>> allProperties) {
        this(allProperties, Collections.emptyMap());
    }

    public ConfigurationDataImpl(List<? extends Property<?>> allProperties, Map<String, List<String>> sectionComments) {
        this.properties = Collections.unmodifiableList(allProperties);
        this.sectionComments = Collections.unmodifiableMap(sectionComments);
    }

    @Override
    public List<Property<?>> getProperties() {
        return properties;
    }

    @Override
    public List<String> getCommentsForSection(String path) {
        return sectionComments.getOrDefault(path, Collections.emptyList());
    }

    private final Map<String, Object> values = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getValue(Property<T> property) {
        return (T) values.get(property.getPath());
    }

    @Override
    public <T> void setValue(Property<T> property, T value) {
        if (property.isValidValueForSetting(value)) {
            values.put(property.getPath(), value);
        } else {
            throw new ConfigMeException("Invalid value for property '" + property + "': " + value);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initializeValues(PropertyReader reader) {
        values.clear();
        getProperties().forEach(property -> setValue((Property) property, property.getValue(reader)));
    }
}
