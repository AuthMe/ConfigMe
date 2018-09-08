package ch.jalu.configme.utils;

import ch.jalu.configme.exception.ConfigMeException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    public static <T> T applyReplacements(T object, Object... replacements) {
        if (object == null)
            return null;

        if (object instanceof String) {
            String target = (String) object;

            if (target.isEmpty())
                return object;

            if (replacements.length > 1 && replacements.length % 2 == 0) {
                for (int i = 0; i < replacements.length; i += 2) {
                    String s = replacements[i + 1].toString();

                    target = target.replace("{" + replacements[i] + "}", s == null ? "" : s);
                }
            }

            return (T) target;
        }

        if (object instanceof List) {
            List<?> rawList = (List<?>) object;

            if (rawList.isEmpty() || !(rawList.get(0) instanceof String)) {
                return object;
            }

            List<String> list = new ArrayList<>((List<String>) rawList);
            list.replaceAll(s -> applyReplacements(s, replacements));

            return (T) list;
        }

        // If object is collection, then create new array list and work with him
        if (object instanceof Collection) {
            return (T) applyReplacements(new ArrayList<>((Collection<T>) object), replacements);
        }

        if (object instanceof String[]) {
            String[] array = (String[]) object;

            if (array.length == 0) {
                return object;
            }

            for (int i = 0; i < array.length; i++) {
                array[i] = applyReplacements(array[i], replacements);
            }

            return (T) array;
        }

        return object;
    }

}
