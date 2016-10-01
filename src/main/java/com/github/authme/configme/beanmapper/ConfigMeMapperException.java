package com.github.authme.configme.beanmapper;

import com.github.authme.configme.exception.ConfigMeException;

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
     * Constructor.
     *
     * @param message the exception message
     * @param cause the cause
     */
    public ConfigMeMapperException(String message, Throwable cause) {
        super(message, cause);
    }
}
