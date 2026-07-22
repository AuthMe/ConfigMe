package ch.jalu.configme.resource;

import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.internal.PathUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.exceptions.YamlEngineException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * YAML file reader.
 */
public class YamlFileReader implements PropertyReader, PathProvider {

    private final Path path;
    private final Charset charset;
    @Nullable
    private final Map<String, Object> root;

    /**
     * Constructor.
     *
     * @param path the file to load
     */
    public YamlFileReader(@NotNull Path path) {
        this(path, StandardCharsets.UTF_8);
    }

    /**
     * Constructor.
     *
     * @param path the file to load
     * @param charset the charset to read the data as
     */
    public YamlFileReader(@NotNull Path path, @NotNull Charset charset) {
        this.path = path;
        this.charset = charset;
        this.root = loadFile();
    }

    @Override
    public @Nullable Object getValue(@NotNull String path) {
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
    public boolean contains(@NotNull String path) {
        return getValue(path) != null;
    }

    @Override
    public @NotNull Set<String> getPaths() {
        return collectPaths(false);
    }

    @Override
    public @NotNull Set<String> getLeafPaths() {
        return collectPaths(true);
    }

    @Override
    public @NotNull Set<String> getChildPaths(@NotNull String path) {
        Object object = getValue(path);
        if (object instanceof Map) {
            String pathPrefix = path.isEmpty() ? "" : path + ".";
            return ((Map<String, Object>) object).keySet().stream()
                .map(childPath -> pathPrefix + childPath)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        return Collections.emptySet();
    }

    private @NotNull Set<String> collectPaths(boolean onlyLeafNodes) {
        if (root == null) {
            return Collections.emptySet();
        }
        Set<String> allPaths = new LinkedHashSet<>();
        collectPathsIntoSet("", root, allPaths, onlyLeafNodes);
        return allPaths;
    }

    /**
     * Recursively collects keys from maps and adds them as paths to {@code result}.
     *
     * @param path the path to the given map
     * @param map the map to process recursively
     * @param result set to save paths to
     * @param onlyLeafNodes whether only leaf nodes should be added to the result set
     */
    private static void collectPathsIntoSet(@NotNull String path, @NotNull Map<String, Object> map,
                                            @NotNull Set<String> result, boolean onlyLeafNodes) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String childPath = PathUtils.concat(path, entry.getKey());
            if (!onlyLeafNodes || isLeafValue(entry.getValue())) {
                result.add(childPath);
            }

            if (entry.getValue() instanceof Map) {
                collectPathsIntoSet(childPath, (Map) entry.getValue(), result, onlyLeafNodes);
            }
        }
    }

    private static boolean isLeafValue(@Nullable Object o) {
        return !(o instanceof Map) || ((Map) o).isEmpty();
    }

    /**
     * Loads the values of the file.
     *
     * @return map with the values from the file
     */
    protected @Nullable Map<String, Object> loadFile() {
        try (InputStream is = Files.newInputStream(path);
             InputStreamReader isr = new InputStreamReader(is, charset)) {
            LoadSettings settings = LoadSettings.builder().build();
            Load load = new Load(settings);
            Map<Object, Object> rootMap = (Map) load.loadFromReader(isr);
            return normalizeMap(rootMap);
        } catch (IOException e) {
            throw new ConfigMeException("Could not read file '" + path + "'", e);
        } catch (ClassCastException e) {
            throw new ConfigMeException("Top-level is not a map in '" + path + "'", e);
        } catch (YamlEngineException e) {
            throw new ConfigMeException("YAML error while trying to load file '" + path + "'", e);
        }
    }

    /**
     * Processes the map as read from SnakeYAML and may return a new, adjusted one.
     *
     * @param map the map to normalize
     * @return the normalized map (or same map if no changes are needed)
     */
    protected @Nullable Map<String, Object> normalizeMap(@Nullable Map<Object, Object> map) {
        return new MapNormalizer().normalizeMap(map);
    }

    /**
     * @return the file this reader read from
     */
    protected final @NotNull Path getPath() {
        return path;
    }

    /**
     * @return the root value; may be null if the file was empty
     * @deprecated use {@code getObject("")} instead
     */
    @Deprecated
    protected final @Nullable Map<String, Object> getRoot() {
        return root;
    }

    private static @Nullable Object getEntryIfIsMap(@NotNull String key, @Nullable Object value) {
        if (value instanceof Map<?, ?>) {
            return ((Map<?, ?>) value).get(key);
        }
        return null;
    }

}
