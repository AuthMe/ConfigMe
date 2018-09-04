package ch.jalu.configme.properties.types;

import ch.jalu.configme.beanmapper.DefaultMapper;
import ch.jalu.configme.beanmapper.Mapper;
import ch.jalu.configme.resource.PropertyReader;
import ch.jalu.configme.utils.TypeInformation;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class BeanPropertyType<B> implements PropertyType<B> {

    private final TypeInformation beanType;
    private final Mapper mapper;

    // Caching by type and mapper
    private static final Map<Class<?>, Map<Mapper, BeanPropertyType<?>>> CACHE = new HashMap<>();

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
        return (BeanPropertyType<B>) CACHE
            .computeIfAbsent(type, (key) -> new HashMap<>())
            .computeIfAbsent(mapper, (key) -> create(type, key));
    }

    static <B> BeanPropertyType<B> of(Class<B> type) {
        return of(type, DefaultMapper.getInstance()); // Create with default mapper
    }

    private static <B> BeanPropertyType<B> create(Class<B> type, Mapper mapper) {
        return new BeanPropertyType<>(new TypeInformation(type), mapper);
    }

}
