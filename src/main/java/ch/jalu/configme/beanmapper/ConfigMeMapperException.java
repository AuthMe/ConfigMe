package ch.jalu.configme.beanmapper;

import ch.jalu.configme.exception.ConfigMeException;

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
    public ConfigMeMapperException(String message) {
        super(message);
    }

    /**
     * Creates an exception which incorporates the mapping context's description into the provided message.
     *
     * @param mappingContext the mapping context with which the message should be extended
     * @param message basic message to extend
     */
    public ConfigMeMapperException(MappingContext mappingContext, String message) {
        super(message + ", for mapping of: [" + mappingContext.createDescription() + "]");
    }

    /**
     * Constructor.
     *
     * @param message the exception message
     * @param cause the cause
     */
    public ConfigMeMapperException(String message, Throwable cause) {
        super(message, cause);
    }
}
