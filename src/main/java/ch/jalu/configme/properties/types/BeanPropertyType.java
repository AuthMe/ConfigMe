package ch.jalu.configme.properties.types;

import ch.jalu.configme.beanmapper.DefaultMapper;
import ch.jalu.configme.beanmapper.Mapper;
import ch.jalu.configme.utils.TypeInformation;

import java.util.Objects;

@SuppressWarnings("unchecked")
public class BeanPropertyType<B> implements PropertyType<B> {

    private final TypeInformation beanType;
    private final Mapper mapper;

    public BeanPropertyType(TypeInformation beanType, Mapper mapper) {
        this.beanType = beanType;
        this.mapper = mapper;
    }

    @Override
    public B convert(Object object) {
        return (B) this.mapper.convertToBean(object, this.beanType);
    }

    @Override
    public Class<B> getType() {
        return (Class<B>) this.beanType.getType();
    }

    @Override
    public Object toExportValue(B value) {
        return value;
    }

    static <B> BeanPropertyType<B> of(Class<B> type, Mapper mapper) {
        return new BeanPropertyType<>(new TypeInformation(type), mapper);
    }

    static <B> BeanPropertyType<B> of(Class<B> type) {
        return of(type, DefaultMapper.getInstance()); // Create with default mapper
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeanPropertyType<?> that = (BeanPropertyType<?>) o;
        return Objects.equals(beanType, that.beanType) &&
            Objects.equals(mapper, that.mapper);
    }

    @Override
    public int hashCode() {

        return Objects.hash(beanType, mapper);
    }
}
