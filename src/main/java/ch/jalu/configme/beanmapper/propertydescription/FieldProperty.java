package ch.jalu.configme.beanmapper.propertydescription;

import ch.jalu.configme.beanmapper.ConfigMeMapperException;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.typeresolver.FieldUtils;
import ch.jalu.typeresolver.TypeInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class FieldProperty implements BeanPropertyDescription {

    private final Field field;
    private final String exportName;
    private final BeanPropertyComments comments;

    public FieldProperty(@NotNull Field field,
                         @Nullable String exportName,
                         @NotNull BeanPropertyComments comments) {
        this.field = field;
        this.exportName = exportName;
        this.comments = comments;
    }

    @Override
    public @NotNull String getName() {
        return exportName == null
            ? field.getName()
            : exportName;
    }

    @Override
    public @NotNull TypeInfo getTypeInformation() {
        return TypeInfo.of(field);
    }

    public @NotNull Class<?> getType() {
        return field.getType();
    }

    public void setValue(@NotNull Object bean, @NotNull Object value) {
        if (!field.isAccessible()) {
            field.setAccessible(true); // todo: exception handling
        }
        try {
            field.set(bean, value);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            String fieldName = FieldUtils.formatField(field);
            throw new ConfigMeMapperException("Failed to set value to field " + fieldName + ". Value: " + value, e);
        }
    }

    @Override
    public @Nullable Object getValue(@NotNull Object bean) {
        if (!field.isAccessible()) {
            field.setAccessible(true); // todo: exception handling
        }
        try {
            return field.get(bean);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new ConfigMeException("Failed to get value for field " + FieldUtils.formatField(field), e);
        }
    }

    @Override
    public @NotNull BeanPropertyComments getComments() {
        return comments;
    }
}
