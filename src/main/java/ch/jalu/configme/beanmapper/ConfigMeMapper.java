package ch.jalu.configme.beanmapper;

/**
 * Mapper configured with defaults as singleton.
 */
public final class ConfigMeMapper {

    private static Mapper mapper;

    private ConfigMeMapper() {
    }

    public static Mapper getSingleton() {
        if (mapper == null) {
            mapper = new Mapper();
        }
        return mapper;
    }
}
