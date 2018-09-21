package ch.jalu.configme.utils;

import ch.jalu.configme.exception.ConfigMeException;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilities class.
 */
public final class Utils {

    private Utils() {
    }

    /**
     * Attempts to create the given file if it doesn't exist. If creating the file
     * is unsuccessful, an exception is thrown. Throws an exception if the argument
     * exists but is not a file.
     *
     * @param file the file to create if it doesn't exist
     */
    public static void createFileIfNotExists(File file) {
        if (file.exists()) {
            if (!file.isFile()) {
                throw new ConfigMeException("Expected file but '" + file + "' is not a file");
            }
        } else {
            File parent = file.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                throw new ConfigMeException("Failed to create parent folders for '" + file + "'");
            }
            try {
                if (!file.createNewFile()) {
                    throw new ConfigMeException("Could not create file '" + file + "'");
                }
            } catch (IOException e) {
                throw new ConfigMeException("Failed to create file '" + file + "'", e);
            }
        }
    }

    public static Map<String, Object> sanitizeMap(Map<String, Object> sourceMap) {
        if (sourceMap == null || sourceMap.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Object> map = new HashMap<>();

        for (Map.Entry<String, Object> entry : sourceMap.entrySet()) {
            String path = entry.getKey();
            String[] keys = path.split("\\.");
            Object object = entry.getValue();
            Map<String, Object> lastNode = map;

            if (object instanceof Map) {
                object = sanitizeMap((Map<String, Object>) object);
            }

            if (keys.length == 1) {
                mergeIfContains(map, path, object);

                continue;
            }

            for (int i = 0; i < keys.length; i++) {
                Object rawNode = lastNode.get(keys[i]);
                Map<String, Object> node;

                if (rawNode instanceof Map) {
                    node = (Map<String, Object>) rawNode;
                } else {
                    lastNode.put(keys[i], node = new HashMap<>());
                }

                String putPath = keys[i + 1];

                if (i == keys.length - 2) {
                    mergeIfContains(node, putPath, object);

                    break;
                }

                node.put(
                    putPath,
                    node.containsKey(putPath)
                        ? node.get(putPath)
                        : new HashMap<>()
                );

                lastNode = node;
            }
        }

        return map;
    }

    private static void mergeIfContains(Map<String, Object> map, String path, Object object) {
        if (map.containsKey(path) && object instanceof Map) {
            merge(map, Collections.singletonMap(path, object));
        } else {
            map.put(path, object);
        }
    }

    private static void merge(Map<String, Object> target, Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (target.containsKey(entry.getKey())) {
                Object object = target.get(entry.getKey());

                if (object instanceof Map && entry.getValue() instanceof Map) {
                    merge((Map<String, Object>) object, (Map<String, Object>) entry.getValue());

                    continue;
                }
            }

            target.put(entry.getKey(), entry.getValue());
        }
    }

}
