package ch.jalu.configme.neo.properties;

import ch.jalu.configme.neo.beanmapper.Mapper;
import ch.jalu.configme.neo.beanmapper.MapperImpl;
import ch.jalu.configme.neo.resource.PropertyReader;

public class BeanProperty<T> extends BaseProperty<T> {

    private final Class<T> beanType;
    private final Mapper mapper;

    public BeanProperty(Class<T> beanType, String path, T defaultValue) {
        this(beanType, path, defaultValue, new MapperImpl()); // TODO: singleton getter of std. mapper
    }

    public BeanProperty(Class<T> beanType, String path, T defaultValue, Mapper mapper) {
        super(path, defaultValue);
        this.beanType = beanType;
        this.mapper = mapper;
    }

    @Override
    protected T getFromResource(PropertyReader reader) {
        return mapper.convertToBean(reader, getPath(), beanType);
    }

    @Override
    public Object toExportValue(T value) {
        return mapper.toExportValue(value);
    }
}
