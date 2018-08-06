package ch.jalu.configme.configurationdata;

import ch.jalu.configme.properties.Property;
import ch.jalu.configme.resource.PropertyReader;

import java.util.List;

/**
 * Manages configuration data:
 * <ul>
 *  <li>knows all available properties</li>
 *  <li>keeps all comments</li>
 *  <li>manages the values associated with the properties</li>
 * </ul>
 *
 * Default implementation: {@link ConfigurationDataImpl}.
 */
public interface ConfigurationData {

    /**
     * Returns all known properties. The order of the properties is relevant and the export should respect it.
     * For YAML it is important that properties with a common path be together (see {@link PropertyListBuilder}
     * for more details).
     *
     * @return list of properties, in order
     */
    List<Property<?>> getProperties();

    /**
     * Returns the comments associated with the given path.
     *
     * @param path the path for which the comments should be retrieved
     * @return list of comments, never null
     */
    List<String> getCommentsForSection(String path);

    /**
     * Initializes the values of all {@link #getProperties known properties} based on the provided reader.
     * Clears any already existing values.
     *
     * @param propertyReader the reader to use to determine the property's values
     */
    void initializeValues(PropertyReader propertyReader);

    /**
     * Returns the value associated with the given property. Only to be used with properties
     * contained in {@link #getProperties()}.
     *
     * @param property the property to look up
     * @param <T> property type
     * @return value associated with the property, or null if not present
     */
    <T> T getValue(Property<T> property);

    /**
     * Sets the given value for the given property. May throw an exception
     * if the value is not valid.
     *
     * @param property the property to change the value for
     * @param value the value to set
     * @param <T> the property type
     */
    <T> void setValue(Property<T> property, T value);

}
