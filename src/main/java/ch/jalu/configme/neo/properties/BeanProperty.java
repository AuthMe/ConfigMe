package ch.jalu.configme.neo.properties;

import ch.jalu.configme.neo.beanmapper.Mapper;
import ch.jalu.configme.neo.beanmapper.MapperImpl;
import ch.jalu.configme.neo.propertytype.NonNullPropertyType;
import ch.jalu.configme.neo.resource.PropertyReader;

public class BeanProperty<T> extends BaseProperty<T> {

    public BeanProperty(Class<T> propertyType, String path, T defaultValue) {
        super(path, defaultValue, new BeanPropertyType<>(propertyType, new MapperImpl()));
    }

    // TODO: CLean up this mess (assuming property type and property are going to be merged again)
    private static class BeanPropertyType<T> extends NonNullPropertyType<T> {

        private final Class<T> clazz;
        private final Mapper mapper;

        private BeanPropertyType(Class<T> clazz, Mapper mapper) {
            this.clazz = clazz;
            this.mapper = mapper;
        }

        @Override
        public T getFromReader(PropertyReader reader, String path) {
            return mapper.convertToBean(reader, path, clazz);
        }

        @Override
        public Object toExportValue(T value) {
            return mapper.toExportValue(value);
        }
    }
}
