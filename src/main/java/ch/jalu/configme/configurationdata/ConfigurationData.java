package ch.jalu.configme.configurationdata;

import ch.jalu.configme.properties.Property;
import ch.jalu.configme.resource.PropertyReader;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Manages configuration data:
 * <ul>
 *  <li>knows all available properties</li>
 *  <li>keeps all comments</li>
 *  <li>manages the values associated with the properties</li>
 * </ul>
 *
 * Create instances with {@link ConfigurationDataBuilder}.
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
     * Returns all comments registered to this configuration data. Typically for tests and
     * debugging only. Use {@link #getCommentsForSection(String)} if you are not interested
     * in the entirety of comments.
     *
     * @return read-only view of all comments
     */
    Map<String, List<String>> getAllComments();

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
    @Nullable
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

    /**
     * Returns if the last call of {@link #initializeValues} had fully valid values in the resource
     * for all known properties. If false, at least one property was either not present in the resource, or the data
     * under its path was not fully acceptable for the property type.
     *
     * @return true if all properties are represented in a fully valid way in the resource, false otherwise
     * @see PropertyValue#isValidInResource()
     */
    boolean areAllValuesValidInResource();

}
