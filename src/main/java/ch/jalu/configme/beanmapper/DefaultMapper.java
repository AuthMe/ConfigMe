package ch.jalu.configme.beanmapper;

/**
 * Provides the {@link Mapper} instance which is used by default.
 */
public final class DefaultMapper extends MapperImpl {

    private static DefaultMapper instance;

    private DefaultMapper() {
    }

    /**
     * @return default mapper instance
     */
    public static Mapper getInstance() {
        if (instance == null) {
            instance = new DefaultMapper();
        }
        return instance;
    }
}
