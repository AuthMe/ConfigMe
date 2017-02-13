package ch.jalu.configme.beanmapper;

/**
 * Mapper utilities.
 */
final class MapperUtils {

    private MapperUtils() {
    }

    /**
     * Invokes the default constructor on a class. If the constructor does not exist or
     * is not accessible an exception is thrown.
     *
     * @param clazz the class to instantiate
     * @param <T> the class' type
     * @return instance of the class
     */
    static <T> T invokeDefaultConstructor(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ConfigMeMapperException("Could not create object of type '" + clazz.getName()
                + "'. It is required to have a default constructor.", e);
        }
    }

}
