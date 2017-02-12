package ch.jalu.configme.resource;

import ch.jalu.configme.exception.ConfigMeException;
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
     * It is possible to map an entire configuration file to one bean property, in which
     * case the bean property path is "" (empty string). In such a case, the root is not
     * a map if the bean property's value gets {@link #set(String, Object)} at a later
     * point ({@code set("", newBeanValue)}.
     * <p>
     * To handle this, we track with this field whether the root is an object. If so, we
     * no longer accept setting values to any subpath. For consistent behavior, we may
     * want to disallow setting values in any subpath of any bean property in the future.
     *
     * @see <a href="https://github.com/AuthMe/ConfigMe/issues/22">Issue #22</a>
     */
    private boolean hasObjectAsRoot = false;

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
            return hasObjectAsRoot ? root.get("") : root;
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
    public void set(String path, Object value) {
        Objects.requireNonNull(path);

        if (path.isEmpty()) {
            root.clear();
            root.put("", value);
            hasObjectAsRoot = true;
        } else if (hasObjectAsRoot) {
            throw new ConfigMeException("The root path is a bean property; you cannot set values to any subpath. "
                + "Modify the bean at the root or set a new one instead.");
        } else {
            setValueInChildPath(path, value);
        }
    }

    @SuppressWarnings("unchecked")
    private void setValueInChildPath(String path, Object value) {
        Map<String, Object> node = root;
        String[] keys = path.split("\\.");
        for (int i = 0; i < keys.length - 1; ++i) {
            Object child = node.get(keys[i]);
            if (child instanceof Map<?, ?>) {
                node = (Map<String, Object>) child;
            } else { // child is null or some other value - replace with map
                Map<String, Object> newEntry = new HashMap<>();
                if (value == null) {
                    // For consistency, replace whatever value/null here with an empty map,
                    // but if the value is null our work here is done.
                    return;
                }
                node.put(keys[i], newEntry);
                node = newEntry;
            }
        }
        // node now contains the parent map (existing or newly created)
        if (value == null) {
            node.remove(keys[keys.length - 1]);
        } else {
            node.put(keys[keys.length - 1], value);
        }
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
