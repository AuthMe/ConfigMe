package ch.jalu.configme.properties.types;

import ch.jalu.configme.beanmapper.DefaultMapper;
import ch.jalu.configme.beanmapper.Mapper;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.utils.TypeInformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BeanPropertyType<B> implements PropertyType<B> {

    private final TypeInformation beanType;
    private final Mapper mapper;

    public BeanPropertyType(TypeInformation beanType, Mapper mapper) {
        this.beanType = beanType;
        this.mapper = mapper;
    }

    public static <B> @NotNull BeanPropertyType<B> of(Class<B> type, Mapper mapper) {
        return new BeanPropertyType<>(new TypeInformation(type), mapper);
    }

    public static <B> @NotNull BeanPropertyType<B> of(Class<B> type) {
        return of(type, DefaultMapper.getInstance());
    }

    @Override
    @SuppressWarnings("unchecked")
    public B convert(Object object, ConvertErrorRecorder errorRecorder) {
        return (B) mapper.convertToBean(object, beanType, errorRecorder);
    }

    @Override
    public @Nullable Object toExportValue(B value) {
        return mapper.toExportValue(value);
    }
}
