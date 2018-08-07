package ch.jalu.configme.beanmapper;

import ch.jalu.configme.beanmapper.leafvaluehandler.LeafValueHandler;
import ch.jalu.configme.beanmapper.propertydescription.BeanDescriptionFactory;

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

    @Override
    protected void setBeanDescriptionFactory(BeanDescriptionFactory beanDescriptionFactory) {
        throw new UnsupportedOperationException("Default mapper is immutable");
    }

    @Override
    protected void setLeafValueHandler(LeafValueHandler leafValueHandler) {
        throw new UnsupportedOperationException("Default mapper is immutable");
    }
}
