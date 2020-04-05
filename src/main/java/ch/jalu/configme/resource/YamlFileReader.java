package ch.jalu.configme.resource;

import ch.jalu.configme.exception.ConfigMeException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * YAML file reader.
 */
public class YamlFileReader implements PropertyReader {

    private final Path path;
    private final Charset charset;
    private final Map<String, Object> root;

    /**
     * Constructor.
     *
     * @param path the file to load
     */
    public YamlFileReader(Path path) {
        this(path, StandardCharsets.UTF_8);
    }

    /**
     * Constructor.
     *
     * @param path the file to load
     * @param charset the charset to read the data as
     */
    public YamlFileReader(Path path, Charset charset) {
        this.path = path;
        this.charset = charset;
        this.root = loadFile();
    }

    /**
     * Constructor (legacy). Prefer the constructors taking {@link Path}.
     *
     * @param file the file to load
     * @deprecated scheduled for removal in favor of Path
     */
    @Deprecated
    public YamlFileReader(File file) {
        this(file.toPath(), StandardCharsets.UTF_8);
    }

    @Override
    public Object getObject(String path) {
        if (path.isEmpty()) {
            return root;
        }

        Object node = root;
        String[] keys = path.split("\\.");
        for (String key : keys) {
            node = getEntryIfIsMap(key, node);
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    @Override
    public String getString(String path) {
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

    @Override
    public Set<String> getKeys(boolean onlyLeafNodes) {
        Set<String> allKeys = new LinkedHashSet<>();
        collectKeysIntoSet("", root, allKeys, onlyLeafNodes);
        return allKeys;
    }

    @Override
    public Set<String> getChildKeys(String path) {
        Object object = getObject(path);
        if (object instanceof Map) {
            String pathPrefix = path.isEmpty() ? "" : path + ".";
            return ((Map<String, Object>) object).keySet().stream()
                .map(childPath -> pathPrefix + childPath)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        return Collections.emptySet();
    }

    /**
     * Recursively collects keys from maps into the given set.
     *
     * @param path the path of the given map
     * @param map the map to process recursively
     * @param result set to save keys to
     * @param onlyLeafNodes whether only leaf nodes should be added to the result set
     */
    private void collectKeysIntoSet(String path, Map<String, Object> map, Set<String> result, boolean onlyLeafNodes) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String childPath = path.isEmpty() ? entry.getKey() : path + "." + entry.getKey();
            if (!onlyLeafNodes || isLeafValue(entry.getValue())) {
                result.add(childPath);
            }

            if (entry.getValue() instanceof Map) {
                collectKeysIntoSet(childPath, (Map) entry.getValue(), result, onlyLeafNodes);
            }
        }
    }

    private static boolean isLeafValue(Object o) {
        return !(o instanceof Map) || ((Map) o).isEmpty();
    }

    /**
     * Loads the values of the file.
     *
     * @return map with the values from the file
     */
    protected Map<String, Object> loadFile() {
        try (InputStream is = Files.newInputStream(path);
             InputStreamReader isr = new InputStreamReader(is, charset)) {
            Map<Object, Object> rootMap = new Yaml().load(isr);
            return normalizeMap(rootMap);
        } catch (IOException e) {
            throw new ConfigMeException("Could not read file '" + path + "'", e);
        } catch (ClassCastException e) {
            throw new ConfigMeException("Top-level is not a map in '" + path + "'", e);
        } catch (YAMLException e) {
            throw new ConfigMeException("YAML error while trying to load file '" + path + "'", e);
        }
    }

    /**
     * Processes the map as read from SnakeYAML and may return a new, adjusted one.
     *
     * @param map the map to normalize
     * @return the normalized map (or same map if no changes are needed)
     */
    protected Map<String, Object> normalizeMap(@Nullable Map<Object, Object> map) {
        return new MapNormalizer().normalizeMap(map);
    }

    // Scheduled for removal in favor of #getPath
    @Deprecated
    protected final File getFile() {
        return path.toFile();
    }

    protected final Path getPath() {
        return path;
    }

    protected final Map<String, Object> getRoot() {
        return root;
    }

    /**
     * Gets the object at the given path and safely casts it to the given class' type. Returns null
     * if no value is available or if it cannot be cast.
     *
     * @param path the path to retrieve
     * @param clazz the class to cast to
     * @param <T> the class type
     * @return cast value at the given path, null if not applicable
     */
    @Nullable
    protected <T> T getTypedObject(String path, Class<T> clazz) {
        Object value = getObject(path);
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        return null;
    }

    @Nullable
    private static Object getEntryIfIsMap(String key, Object value) {
        if (value instanceof Map<?, ?>) {
            return ((Map<?, ?>) value).get(key);
        }
        return null;
    }

}
