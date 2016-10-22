package com.github.authme.configme.resource;

import com.github.authme.configme.exception.ConfigMeException;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * YAML file reader.
 */
public class YamlFileReader implements PropertyReader {

    private final File file;
    private Map<String, Object> root;

    /**
     * Constructor.
     *
     * @param file the file to load
     */
    @SuppressWarnings("unchecked")
    public YamlFileReader(File file) {
        this.file = file;
        reload();
    }

    @Override
    public Object getObject(String path) {
        if (path.isEmpty()) {
            return root;
        }
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

    @Override
    public <T> T getTypedObject(String path, Class<T> clazz) {
        Object value = getObject(path);
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void set(String path, Object value) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(value);

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

    @Override
    public void reload() {
        try (FileInputStream fis = new FileInputStream(file)) {
            Object obj = new Yaml().load(fis);
            root = obj == null ? new HashMap<>() : (Map<String, Object>) obj;
        } catch (IOException e) {
            throw new ConfigMeException("Could not read file '" + file + "'", e);
        } catch (ClassCastException e) {
            throw new ConfigMeException("Top-level is not a map in '" + file + "'", e);
        }
    }

    private static Object getIfIsMap(String key, Object value) {
        if (value instanceof Map<?, ?>) {
            return ((Map<?, ?>) value).get(key);
        }
        return null;
    }

}
