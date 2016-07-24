package com.github.authme.configme.resource;

import com.github.authme.configme.exception.ConfigMeException;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * YAML file reader.
 */
public class YamlFileReader {

    private Map<String, Object> root;

    /**
     * Constructor.
     *
     * @param file the file to load
     */
    @SuppressWarnings("unchecked")
    public YamlFileReader(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            Object obj = new Yaml().load(fis);
            root = (Map<String, Object>) obj;
        } catch (IOException e) {
            throw new ConfigMeException("Could not read file '" + file + "'", e);
        } catch (ClassCastException e) {
            throw new ConfigMeException("Top-level is not a map in '" + file + "'", e);
        }
    }

    /**
     * Returns whether there is a value for the given path.
     *
     * @param path the path to process
     * @return true if the path exists, false otherwise
     */
    public boolean contains(String path) {
        return getObject(path) != null;
    }

    /**
     * Returns the value for the given path, or null if not present.
     *
     * @param path the path to retrieve the value for
     * @return the value, or null if not available
     */
    public Object getObject(String path) {
        Object node = root;
        String[] keys = path.split("\\.");
        for (String key : keys) {
            node = getIfIsMap(key, node);
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    /**
     * Returns the value for the given path in a typed manner. Returns null if no value is
     * present or if the value does not match the requested type.
     *
     * @param path the path to retrieve the value for
     * @param clazz the class to cast the value to if possible
     * @param <T> the class' type
     * @return the typed value, or null if unavailable or not applicable
     */
    public <T> T getTypedObject(String path, Class<T> clazz) {
        Object value = getObject(path);
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        return null;
    }

    /**
     * Sets the value at the given path. This method does not persist any values to the read file.
     *
     * @param path the path to set a value for
     * @param value the value to set
     */
    @SuppressWarnings("unchecked")
    public void set(String path, Object value) {
        Map<String, Object> node = root;
        String[] keys = path.split("\\.");
        for (int i = 0; i < keys.length - 1; ++i) {
            Object child = node.get(keys[i]);
            if (child instanceof Map<?, ?>) {
                node = (Map<String, Object>) child;
            } else { // child is null or some other value - replace with map
                Map<String, Object> newEntry = new HashMap<>();
                node.put(keys[i], newEntry);
                node = newEntry;
            }
        }
        // node now contains the parent map (existing or newly created)
        node.put(keys[keys.length - 1], value);
    }

    private static Object getIfIsMap(String key, Object value) {
        if (value instanceof Map<?, ?>) {
            return ((Map<?, ?>) value).get(key);
        }
        return null;
    }

}
