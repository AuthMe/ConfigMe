package ch.jalu.configme.configurationdata;

import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.resource.PropertyReader;
import ch.jalu.configme.resource.PropertyResource;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains information about the available properties and their associated comments.
 */
public class ConfigurationDataImpl implements ConfigurationData {

    private final List<Property<?>> properties;
    private final Map<String, List<String>> allComments;
    private final Map<String, Object> values;

    /**
     * Constructor. See also {@link ConfigurationDataBuilder}.
     *
     * @param allProperties all known properties
     * @param allComments map of comments by path
     */
    protected ConfigurationDataImpl(List<? extends Property<?>> allProperties, Map<String, List<String>> allComments) {
        this.properties = Collections.unmodifiableList(allProperties);
        this.allComments = Collections.unmodifiableMap(allComments);
        this.values = new HashMap<>();
    }

    @Override
    public List<Property<?>> getProperties() {
        return properties;
    }

    @Override
    public List<String> getCommentsForSection(String path) {
        return allComments.getOrDefault(path, Collections.emptyList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getValue(Property<T> property) {
        return (T) values.get(property.getPath());
    }

    @Override
    public <T> void setValue(Property<T> property, T value) {
        if (property.isValidValue(value)) {
            values.put(property.getPath(), value);
        } else {
            throw new ConfigMeException("Invalid value for property '" + property + "': " + value);
        }
    }

    @Override
    public <T> T getRelativeValue(String rootPath, Property<T> property, PropertyResource resource) {
        String path = rootPath + "." + property.getPath();

        if (this.values.containsKey(path)) {
            return (T) this.values.get(path);
        }

        Property<T> relativeProperty = property.cloneWithNewPath(path);

        if (relativeProperty == null) {
            return null;
        }

        T value = relativeProperty.determineValue(resource.createReader());
        this.setValue(relativeProperty, value);

        return value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initializeValues(PropertyReader reader) {
        values.clear();
        getProperties().forEach(property -> setValue((Property) property, property.determineValue(reader)));
    }
}
