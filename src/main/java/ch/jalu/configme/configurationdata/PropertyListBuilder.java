package ch.jalu.configme.configurationdata;

import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds a list of known properties in an ordered and grouped manner.
 * <p>
 * It guarantees that the added entries:
 * <ul>
 *   <li>are grouped by path, e.g. all "DataSource.mysql" properties are together, and "DataSource.mysql" properties
 *   are within the broader "DataSource" group.</li>
 *   <li>are ordered by insertion, e.g. if the first "DataSource" property is inserted before the first "security"
 *   property, then "DataSource" properties will come before the "security" ones.</li>
 *   <li>are unique: if any property is attempted to be added twice, or the addition of a property would remove another
 *   existing property, an exception is thrown</li>
 * </ul>
 */
public class PropertyListBuilder {

    private final @NotNull Map<String, Object> rootEntries = new LinkedHashMap<>();

    /**
     * Adds the property to the list builder.
     *
     * @param property the property to add
     */
    public void add(@NotNull Property<?> property) {
        String[] pathElements = property.getPath().split("\\.", -1);
        Map<String, Object> mapForProperty = getMapBeforeLastElement(pathElements);

        final String lastElement = pathElements[pathElements.length - 1];
        if (mapForProperty.containsKey(lastElement)) {
            throw new ConfigMeException("Path at '" + property.getPath() + "' already exists");
        } else if (pathElements.length > 1 && lastElement.equals("")) {
            throwExceptionForMalformedPath(property.getPath());
        }
        mapForProperty.put(lastElement, property);
    }

    /**
     * Creates a list of properties that have been added, by insertion order but grouped by path parents
     * (see class JavaDoc).
     *
     * @return ordered list of registered properties
     */
    public @NotNull List<Property<?>> create() {
        List<Property<?>> result = new ArrayList<>();
        collectEntries(rootEntries, result);
        if (result.size() > 1 && rootEntries.containsKey("")) {
            throw new ConfigMeException("A property at the root path (\"\") cannot be defined alongside "
                + "other properties as the paths would conflict");
        }
        return result;
    }

    /**
     * Returns the nested map for the given path parts in which a property can be saved (for the last element
     * in the path parts). Throws an exception if the path is malformed.
     *
     * @param pathParts the path elements (i.e. the property path split by ".")
     * @return the map to store the property in
     */
    protected @NotNull Map<String, Object> getMapBeforeLastElement(String @NotNull [] pathParts) {
        Map<String, Object> map = rootEntries;
        for (int i = 0; i < pathParts.length - 1; ++i) {
            map = getChildMap(map, pathParts[i]);
            if (pathParts[i].equals("")) {
                throwExceptionForMalformedPath(String.join(".", pathParts));
            }
        }
        return map;
    }

    protected void throwExceptionForMalformedPath(@NotNull String path) {
        throw new ConfigMeException("The path at '" + path + "' is malformed: dots may not be at the beginning or end "
            + "of a path, and dots may not appear multiple times successively.");
    }

    protected final @NotNull Map<String, Object> getRootEntries() {
        return rootEntries;
    }

    private static @NotNull Map<String, Object> getChildMap(@NotNull Map<String, Object> parent, @NotNull String path) {
        Object o = parent.get(path);
        if (o instanceof Map<?, ?>) {
            return asTypedMap(o);
        } else if (o == null) {
            Map<String, Object> map = new LinkedHashMap<>();
            parent.put(path, map);
            return map;
        } else { // uh oh
            if (o instanceof Property<?>) {
                throw new ConfigMeException("Unexpected entry found at path '" + path + "'");
            } else {
                throw new ConfigMeException("Value of unknown type found at '" + path + "': " + o);
            }
        }
    }

    private static void collectEntries(@NotNull Map<String, Object> map, @NotNull List<Property<?>> results) {
        for (Object o : map.values()) {
            if (o instanceof Map<?, ?>) {
                collectEntries(asTypedMap(o), results);
            } else if (o instanceof Property<?>) {
                results.add((Property<?>) o);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static @NotNull Map<String, Object> asTypedMap(@NotNull Object o) {
        return (Map<String, Object>) o;
    }
}
