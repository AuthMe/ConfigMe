package ch.jalu.configme.beanmapper;

import ch.jalu.configme.beanmapper.context.MappingContext;
import ch.jalu.configme.exception.ConfigMeException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Exception during a bean mapping process.
 */
public class ConfigMeMapperException extends ConfigMeException {

    private static final long serialVersionUID = 5439842847683269906L;

    /**
     * Constructor.
     *
     * @param message the exception message
     */
    public ConfigMeMapperException(@NotNull String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message the exception message
     * @param cause the cause
     */
    public ConfigMeMapperException(@NotNull String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param mappingContext the mapping context with which the message should be extended
     * @param message basic message to extend
     */
    public ConfigMeMapperException(@NotNull MappingContext mappingContext, @NotNull String message) {
        super(constructMessage(mappingContext, message));
    }

    /**
     * Constructor.
     *
     * @param mappingContext the mapping context with which the message should be extended
     * @param message basic message to extend
     * @param cause the cause
     */
    public ConfigMeMapperException(@NotNull MappingContext mappingContext, @NotNull String message,
                                   @Nullable Throwable cause) {
        super(constructMessage(mappingContext, message), cause);
    }

    private static @NotNull String constructMessage(@NotNull MappingContext mappingContext, @NotNull String message) {
        return message + ", for mapping of: [" + mappingContext.createDescription() + "]";
    }
}
