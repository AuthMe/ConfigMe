package com.github.authme.configme.beanmapper;

import com.github.authme.configme.exception.ConfigMeException;

/**
 * Exception during a bean mapping process.
 */
public class ConfigMeMapperException extends ConfigMeException {

    private static final long serialVersionUID = 6358740683858182591L;

    /**
     * Constructor.
     *
     * @param message the exception message
     */
    public ConfigMeMapperException(String message) {
        super(message);
    }

}
