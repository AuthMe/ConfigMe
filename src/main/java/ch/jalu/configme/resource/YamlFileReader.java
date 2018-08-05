package ch.jalu.configme.resource;

import ch.jalu.configme.exception.ConfigMeException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * YAML file reader.
 */
public class YamlFileReader implements PropertyReader {

    protected final File file;
    protected Map<String, Object> root;

    /**
     * Constructor.
     *
     * @param file the file to load
     */
    public YamlFileReader(File file) {
        this.file = file;
        loadFile();
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
    public String getString(String path) {
        // TODO: As an improvement we could also return the toString of any scalar values
        return getTypedObject(path, String.class);
    }

    @Override
    public Integer getInt(String path) {
        Number n = getTypedObject(path, Number.class);
        return (n == null)
            ? null
            : n.intValue();
    }

    @Override
    public Double getDouble(String path) {
        Number n = getTypedObject(path, Number.class);
        return (n == null)
            ? null
            : n.doubleValue();
    }

    @Override
    public Boolean getBoolean(String path) {
        return getTypedObject(path, Boolean.class);
    }

    @Override
    public List<?> getList(String path) {
        return getTypedObject(path, List.class);
    }

    @Override
    public boolean contains(String path) {
        return getObject(path) != null;
    }

    protected void loadFile() {
        try (FileInputStream fis = new FileInputStream(file)) {
            Object obj = new Yaml().load(fis);
            root = obj == null ? Collections.emptyMap() : (Map<String, Object>) obj;
        } catch (IOException e) {
            throw new ConfigMeException("Could not read file '" + file + "'", e);
        } catch (ClassCastException e) {
            throw new ConfigMeException("Top-level is not a map in '" + file + "'", e);
        } catch (YAMLException e) {
            throw new ConfigMeException("YAML error while trying loading file '" + file + "'", e);
        }
    }

    protected <T> T getTypedObject(String path, Class<T> clazz) {
        Object value = getObject(path);
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        return null;
    }

    private static Object getIfIsMap(String key, Object value) {
        if (value instanceof Map<?, ?>) {
            return ((Map<?, ?>) value).get(key);
        }
        return null;
    }

}
