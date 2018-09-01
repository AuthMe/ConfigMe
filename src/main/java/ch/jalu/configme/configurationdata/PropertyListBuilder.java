package ch.jalu.configme.configurationdata;

import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.Property;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds a list of known properties in an ordered and grouped manner.
 *
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

    private Map<String, Object> rootEntries = new LinkedHashMap<>();

    /**
     * Adds the property to the list builder.
     *
     * @param property the property to add
     */
    public void add(Property<?> property) {
        String[] paths = property.getPath().split("\\.");
        Map<String, Object> map = rootEntries;
        for (int i = 0; i < paths.length - 1; ++i) {
            map = getChildMap(map, paths[i]);
        }

        final String end = paths[paths.length - 1];
        if (map.containsKey(end)) {
            throw new ConfigMeException("Path at '" + property.getPath() + "' already exists");
        }
        map.put(end, property);
    }

    /**
     * Creates a list of properties that have been added, by insertion order but grouped by path parents
     * (see class JavaDoc).
     *
     * @return ordered list of registered properties
     */
    public List<Property<?>> create() {
        List<Property<?>> result = new ArrayList<>();
        collectEntries(rootEntries, result);
        return result;
    }

    private static Map<String, Object> getChildMap(Map<String, Object> parent, String path) {
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

    private static void collectEntries(Map<String, Object> map, List<Property<?>> results) {
        for (Object o : map.values()) {
            if (o instanceof Map<?, ?>) {
                collectEntries(asTypedMap(o), results);
            } else if (o instanceof Property<?>) {
                results.add((Property<?>) o);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asTypedMap(Object o) {
        return (Map<String, Object>) o;
    }
}
