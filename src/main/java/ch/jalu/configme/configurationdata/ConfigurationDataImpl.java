package ch.jalu.configme.configurationdata;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.resource.PropertyReader;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

/**
 * Contains information about the available properties and their associated comments.
 */
public class ConfigurationDataImpl implements ConfigurationData {

    private final List<Property<?>> properties;
    private final Map<String, List<String>> allComments;
    private final Map<String, Object> values;
    private boolean allPropertiesValidInResource;

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
    public Map<String, List<String>> getAllComments() {
        return allComments;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getValue(Property<T> property) {
        Object value = values.get(property.getPath());
        if (value == null) {
            throw new ConfigMeException(format("No value exists for property with path '%s'. This may happen if "
                                + "the property belongs to a %s class which was not passed to the settings manager.",
                property.getPath(), SettingsHolder.class.getSimpleName()));
        }
        return (T) value;
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
    public void initializeValues(PropertyReader reader) {
        values.clear();

        allPropertiesValidInResource = getProperties().stream()
            .map(property -> setValueForProperty(property, reader))
            .reduce(true, Boolean::logicalAnd);
    }

    /*
     * Saves the value for the provided property as determined from the reader and returns whether the
     * property is represented in a fully valid way in the resource.
     */
    protected <T> boolean setValueForProperty(Property<T> property, PropertyReader reader) {
        PropertyValue<T> propertyValue = property.determineValue(reader);
        setValue(property, propertyValue.getValue());
        return propertyValue.isValidInResource();
    }

    @Override
    public boolean areAllValuesValidInResource() {
        return allPropertiesValidInResource;
    }

    protected Map<String, Object> getValues() {
        return values;
    }
}
