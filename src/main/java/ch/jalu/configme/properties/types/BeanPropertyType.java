package ch.jalu.configme.properties.types;

import ch.jalu.configme.beanmapper.DefaultMapper;
import ch.jalu.configme.beanmapper.Mapper;
import ch.jalu.configme.resource.PropertyReader;
import ch.jalu.configme.utils.TypeInformation;

public class BeanPropertyType<B> implements PropertyType<B> {

    private final TypeInformation beanType;
    private final Mapper mapper;

    private BeanPropertyType(TypeInformation beanType, Mapper mapper) {
        this.beanType = beanType;
        this.mapper = mapper;
    }

    @Override
    public B get(PropertyReader reader, String path) {
        return (B) mapper.convertToBean(reader, path, this.beanType);
    }

    @Override
    public B convert(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<B> getType() {
        return (Class<B>) this.beanType.getType();
    }

    static <B> BeanPropertyType<B> of(Class<B> type, Mapper mapper) {
        return new BeanPropertyType<>(new TypeInformation(type), mapper);
    }

    static <B> BeanPropertyType<B> of(Class<B> type) {
        return of(type, DefaultMapper.getInstance());
    }
}
