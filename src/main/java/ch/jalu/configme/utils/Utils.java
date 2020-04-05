package ch.jalu.configme.utils;

import ch.jalu.configme.exception.ConfigMeException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
     * @deprecated to be removed in the next major release in favor of {@link #createFileIfNotExists(Path)}
     */
    @Deprecated
    public static void createFileIfNotExists(File file) {
        createFileIfNotExists(file.toPath());
    }

    /**
     * Attempts to create the given Path (as file) if it doesn't exist. If creating the file
     * is unsuccessful, an exception is thrown. If the given path exists but is not a file,
     * an exception is thrown, too.
     *
     * @param file the file to create if it doesn't exist
     */
    public static void createFileIfNotExists(Path file) {
        if (Files.exists(file)) {
            if (!Files.isRegularFile(file)) {
                throw new ConfigMeException("Expected file but '" + file + "' is not a file");
            }
        } else {
            Path parent = file.getParent();
            if (!Files.exists(parent) || !Files.isDirectory(parent)) {
                try {
                    Files.createDirectories(parent);
                } catch (IOException e) {
                    throw new ConfigMeException("Failed to create parent folders for '" + file + "'", e);
                }
            }
            try {
                Files.createFile(file);
            } catch (IOException e) {
                throw new ConfigMeException("Failed to create file '" + file + "'", e);
            }
        }
    }
}
