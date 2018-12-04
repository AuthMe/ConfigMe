package ch.jalu.configme.resource;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Normalizes the keys of maps, splitting compound keys and ensuring that all keys are Strings.
 */
public class MapNormalizer {

    /**
     * Normalizes the raw map read from a property resource for further use in a property reader.
     *
     * @param loadedMap the map to normalize
     * @return new map with sanitized structure (or same if no changes are needed)
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> normalizeMap(@Nullable Map<Object, Object> loadedMap) {
        if (loadedMap == null) {
            return Collections.emptyMap();
        }
        // Cast to Map<String, Object> if we have an empty optional as the method guarantees to return a new Map
        // if it does not exclusively use String keys
        return createNormalizedMapIfNeeded(loadedMap).orElse((Map) loadedMap);
    }

    /**
     * Processes the given value if it is a Map and returns an Optional with a new Map if the input
     * value is not in its "normalized form." Recursively visits and replaces nested maps.
     *
     * @param value the value to process
     * @return optional with a new map to replace the given one with, empty optional if not needed or not applicable
     */
    protected Optional<Map<String, Object>> createNormalizedMapIfNeeded(Object value) {
        if (!(value instanceof Map<?, ?>)) {
            return Optional.empty();
        }

        Map<Object, Object> map = (Map<Object, Object>) value;
        boolean mapNeedsModification = false;
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            createNormalizedMapIfNeeded(entry.getValue())
                .ifPresent(newMap -> map.put(entry.getKey(), newMap));

            if (!mapNeedsModification && isKeyInvalid(entry.getKey())) {
                mapNeedsModification = true;
            }
        }

        if (mapNeedsModification) {
            Map<String, Object> cleanedMap = new LinkedHashMap<>(map.size());
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                addValueIntoMap(cleanedMap, Objects.toString(entry.getKey()), entry.getValue());
            }
            return Optional.of(cleanedMap);
        }
        return Optional.empty();
    }

    protected boolean isKeyInvalid(Object key) {
        return !(key instanceof String) || ((String) key).contains(".");
    }

    /**
     * Adds the provided value into the given map, splitting the path into periods appropriately and keeping
     * any intermediate nested maps which may already exist.
     *
     * @param map the map to add the value to
     * @param path the path to store the value under
     * @param value the value to store
     */
    protected void addValueIntoMap(Map<String, Object> map, String path, Object value) {
        int dotPosition = path.indexOf(".");
        if (dotPosition > -1) {
            String pathElement = path.substring(0, dotPosition);
            Map<String, Object> mapAtPath = getOrInsertMap(map, pathElement);
            addValueIntoMap(mapAtPath, path.substring(dotPosition + 1), value);
        } else if (value instanceof Map<?, ?>) {
            Map<String, Object> mapAtPath = getOrInsertMap(map, path);
            Map<?, ?> mapValue = (Map<?, ?>) value;
            mapValue.forEach((entryKey, entryValue) ->
                addValueIntoMap(mapAtPath, Objects.toString(entryKey), entryValue));
        } else { // no dot in path and value is not a map: just insert it
            map.put(path, value);
        }
    }

    /**
     * Returns the nested map in the given {@code parentMap} at the given {@code path}, inserting
     * one if none is yet present. Periods in the path argument are not handled by this method.
     * Note that this method overrides any non-Map values stored at the given path.
     *
     * @param parentMap the map to retrieve the nested map from
     * @param path the key with which the value should be looked up from the map
     * @return the nested map, as stored under the path in the given map
     */
    protected Map<String, Object> getOrInsertMap(Map<String, Object> parentMap, String path) {
        Object value = parentMap.get(path);
        if (value instanceof Map<?, ?>) {
            return (Map<String, Object>) value;
        }
        Map<String, Object> newMap = new LinkedHashMap<>();
        parentMap.put(path, newMap);
        return newMap;
    }
}
