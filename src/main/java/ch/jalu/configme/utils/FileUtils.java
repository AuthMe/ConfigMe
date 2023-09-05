package ch.jalu.configme.utils;

import ch.jalu.configme.exception.ConfigMeException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Class with file utilities.
 */
public final class FileUtils {

    private FileUtils() {
    }

    /**
     * Attempts to create the given Path (as file) if it doesn't exist. If creating the file
     * is unsuccessful, an exception is thrown. If the given path exists but is not a file,
     * an exception is thrown, too.
     *
     * @param file the file to create if it doesn't exist
     */
    public static void createFileIfNotExists(@NotNull Path file) {
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
