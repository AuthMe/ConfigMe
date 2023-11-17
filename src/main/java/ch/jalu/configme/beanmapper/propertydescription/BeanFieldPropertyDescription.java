package ch.jalu.configme.beanmapper.propertydescription;

import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.internal.record.ReflectionHelper;
import ch.jalu.typeresolver.FieldUtils;
import ch.jalu.typeresolver.TypeInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

/**
 * Bean property description based on a {@link Field}.
 */
public class BeanFieldPropertyDescription implements BeanPropertyDescription {

    private final Field field;
    private final String exportName;
    private final BeanPropertyComments comments;

    public BeanFieldPropertyDescription(@NotNull Field field,
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

    /**
     * Sets the provided value to the field wrapped by this instance on the given bean. This method does not
     * check whether the field is final; in some contexts (e.g. instantiation a record type), this method cannot
     * be called.
     *
     * @param bean the bean to set the value to
     * @param value the value to set
     */
    public void setValue(@NotNull Object bean, @NotNull Object value) {
        ReflectionHelper.setAccessibleIfNeeded(field);

        try {
            field.set(bean, value);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            String fieldName = FieldUtils.formatField(field);
            throw new ConfigMeException("Failed to set value to field " + fieldName + ". Value: " + value, e);
        }
    }

    @Override
    public @Nullable Object getValue(@NotNull Object bean) {
        ReflectionHelper.setAccessibleIfNeeded(field);

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

    @Override
    public @NotNull String toString() {
        return "FieldProperty '" + getName() + "' for field '" + FieldUtils.formatField(field) + "'";
    }
}
