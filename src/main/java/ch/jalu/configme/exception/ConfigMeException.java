package ch.jalu.configme.exception;

/**
 * ConfigMe exception.
 */
public class ConfigMeException extends RuntimeException {

    private static final long serialVersionUID = -865062331853823084L;

    public ConfigMeException(String message) {
        super(message);
    }

    public ConfigMeException(String message, Throwable cause) {
        super(message, cause);
    }
}
