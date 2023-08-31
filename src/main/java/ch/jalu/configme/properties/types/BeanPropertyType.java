package ch.jalu.configme.properties.types;

import ch.jalu.configme.beanmapper.DefaultMapper;
import ch.jalu.configme.beanmapper.Mapper;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.typeresolver.TypeInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BeanPropertyType<B> implements PropertyType<B> {

    private final TypeInfo beanType;
    private final Mapper mapper;

    public BeanPropertyType(@NotNull TypeInfo beanType, @NotNull Mapper mapper) {
        this.beanType = beanType;
        this.mapper = mapper;
    }

    public static <B> @NotNull BeanPropertyType<B> of(@NotNull Class<B> type, @NotNull Mapper mapper) {
        return new BeanPropertyType<>(new TypeInfo(type), mapper);
    }

    public static <B> @NotNull BeanPropertyType<B> of(@NotNull Class<B> type) {
        return of(type, DefaultMapper.getInstance());
    }

    @Override
    @SuppressWarnings("unchecked")
    public B convert(@Nullable Object object, @NotNull ConvertErrorRecorder errorRecorder) {
        return (B) mapper.convertToBean(object, beanType, errorRecorder);
    }

    @Override
    public @Nullable Object toExportValue(@NotNull B value) {
        return mapper.toExportValue(value);
    }
}
