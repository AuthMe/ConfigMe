package com.github.authme.configme.beanmapper;

/**
 * Mapper configured with defaults as singleton.
 */
public final class ConfigMeMapper {

    private static Mapper mapper;

    private ConfigMeMapper() {
    }

    public static Mapper getSingleton() {
        return mapper == null
            ? (mapper = new Mapper())
            : mapper;
    }
}
