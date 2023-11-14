package ch.jalu.configme.beanmapper.instantiation;

import ch.jalu.configme.beanmapper.propertydescription.BeanPropertyDescription;
import ch.jalu.configme.beanmapper.propertydescription.FieldProperty;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

final class RecordInstantiation implements BeanInstantiation {

    private final Constructor<?> canonicalConstructor;
    private final List<FieldProperty> properties;

    public RecordInstantiation(@NotNull Class<?> clazz, @NotNull List<FieldProperty> properties) {
        this.properties = properties;
        Class<?>[] recordTypes = properties.stream().map(FieldProperty::getType).toArray(Class[]::new);
        this.canonicalConstructor = BeanInspector.getConstructor(clazz, recordTypes)
            .orElseThrow(() -> new ConfigMeException("Could not get canonical constructor of " + clazz));
    }

    @Override
    public @NotNull List<BeanPropertyDescription> getProperties() {
        return Collections.unmodifiableList(properties);
    }

    @Override
    public @Nullable Object create(@NotNull List<Object> propertyValues,
                                   @NotNull ConvertErrorRecorder errorRecorder) {
        if (propertyValues.stream().anyMatch(Objects::isNull)) {
            return null; // No support for null values in records
        }

        Object[] properties = propertyValues.toArray();
        try {
            return canonicalConstructor.newInstance(properties);
        } catch (IllegalArgumentException | ReflectiveOperationException e) {
            // TODO: Separate clause for InvocationTargetException?
            throw new ConfigMeException("Error calling record constructor for "
                + canonicalConstructor.getDeclaringClass(), e);
        }
    }
}
