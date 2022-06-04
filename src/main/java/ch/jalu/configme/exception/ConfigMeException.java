package ch.jalu.configme.exception;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ConfigMe exception.
 */
public class ConfigMeException extends RuntimeException {

    private static final long serialVersionUID = -865062331853823084L;

    public ConfigMeException(@NotNull String message) {
        super(message);
    }

    public ConfigMeException(@NotNull String message, @Nullable Throwable cause) {
        super(message, cause);
    }
}
