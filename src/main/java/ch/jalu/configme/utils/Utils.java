package ch.jalu.configme.utils;

import ch.jalu.configme.exception.ConfigMeException;

import java.io.File;
import java.io.IOException;

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
}
