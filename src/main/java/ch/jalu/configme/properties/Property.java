package ch.jalu.configme.properties;

import ch.jalu.configme.configurationdata.PropertyValue;
import ch.jalu.configme.resource.PropertyReader;

import javax.annotation.Nullable;

/**
 * Property interface. A property knows its path, its type, and can convert the values from
 * a property reader to a value of its type (if the values of the reader are valid).
 * Properties define their path and their behavior but do not keep track of their value.
 * <p>
 * Property implementations should always extend from {@link BaseProperty} instead of implementing
 * this interface directly.
 *
 * @param <T> the property type
 */
public interface Property<T> {

    /**
     * @return the path of the property
     */
    String getPath();

    /**
     * Returns the value, based on the given reader, which should be used for this property. By default
     * this is the value as constructed from the reader, and otherwise the default value. Implementations
     * of {@link BaseProperty} never return null. The return value must be in sync with
     * {@link #isValidValue(Object)}.
     *
     * @param propertyReader the reader to construct the value from (if possible)
     * @return the value to associate to this property
     */
    PropertyValue<T> determineValue(PropertyReader propertyReader);

    /**
     * Returns the default value of this property.
     *
     * @return the default value
     */
    T getDefaultValue();

    /**
     * Returns whether the value can be associated to the given property, i.e. whether it fulfills all
     * requirements which may be imposed by the property type.
     * <p>
     * This method is used in {@link ch.jalu.configme.configurationdata.ConfigurationDataImpl#setValue}, which
     * throws an exception if this method returns {@code false}. Therefore, this method is intended as a last catch
     * for invalid values and to ensure that programmatically no invalid value can be set. Extended validation of
     * values encountered in the property reader should be preferably handled in {@link #determineValue},
     * or in an extension of {@link ch.jalu.configme.migration.MigrationService}.
     *
     * @param value the value to check
     * @return true if the value can be used for the property, false otherwise
     */
    boolean isValidValue(T value);

    /**
     * Converts the given value to a representation that is suitable for exporting by a property resource. The
     * return value should contain the data in the given value represented with only basic types (String, Number,
     * Boolean), as well as lists and maps thereof.
     * <p>
     * The values which are suitable for returning depend on the support of the used property resource. By default,
     * the supported types include {@code null}, String, Integer, Double, Boolean; Collection of the aforementioned
     * types; Map with String keys and values of any of the aforementioned types. Maps and collections can be nested at
     * any arbitrary level. Null signifies that the property/value should be skipped in the export.
     *
     * @param value the value to convert to an export value
     * @return value to use for export, null to skip the property
     */
    @Nullable
    Object toExportValue(T value);

}
