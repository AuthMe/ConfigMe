package ch.jalu.configme.beanmapper.instantiation;

import ch.jalu.configme.beanmapper.propertydescription.BeanDescriptionFactory;
import ch.jalu.configme.beanmapper.propertydescription.BeanDescriptionFactoryImpl;
import ch.jalu.configme.beanmapper.propertydescription.BeanPropertyComments;
import ch.jalu.configme.beanmapper.propertydescription.BeanPropertyDescription;
import ch.jalu.configme.beanmapper.propertydescription.FieldProperty;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.internal.record.RecordComponent;
import ch.jalu.configme.internal.record.RecordInspector;
import ch.jalu.configme.internal.record.ReflectionHelper;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.typeresolver.TypeInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class BeanInspector {

    private final RecordInspector recordInspector = new RecordInspector(new ReflectionHelper());
    private final BeanDescriptionFactory beanDescriptionFactory = new BeanDescriptionFactoryImpl();

    public @NotNull Optional<BeanInstantiation> findInstantiation(@NotNull Class<?> clazz) {
        if (recordInspector.isRecord(clazz)) {
            RecordComponent[] recordComponents = recordInspector.getRecordComponents(clazz);
            return Optional.of(new RecordInstantiation(clazz, Arrays.asList(recordComponents)));
        }

        Optional<Constructor<?>> zeroArgConstructor = getConstructor(clazz);
        if (zeroArgConstructor.isPresent()) {
            List<FieldProperty> properties = beanDescriptionFactory.getAllProperties2(clazz);
            return Optional.of(new BeanNoArgConstructor(zeroArgConstructor.get(), properties));
        }

        return Optional.empty();
    }

    private static @NotNull Optional<Constructor<?>> getConstructor(@NotNull Class<?> declarer,
                                                                    Class<?> @NotNull ... parameters) {
        try {
            return Optional.of(declarer.getDeclaredConstructor(parameters));
        } catch (NoSuchMethodException ignore) {
            return Optional.empty();
        }
    }

    private static final class BeanNoArgConstructor implements BeanInstantiation {

        private final Constructor<?> zeroArgsConstructor;
        private final List<FieldProperty> properties;

        private BeanNoArgConstructor(@NotNull Constructor<?> zeroArgsConstructor,
                                     @NotNull List<FieldProperty> properties) {
            this.zeroArgsConstructor = zeroArgsConstructor;
            this.properties = properties;
        }

        @Override
        public @NotNull List<BeanPropertyDescription> getProperties() {
            return Collections.unmodifiableList(properties);
        }

        @Override
        public @Nullable Object create(@NotNull List<Object> propertyValues,
                                       @NotNull ConvertErrorRecorder errorRecorder) {
            final Object bean;
            try {
                bean = zeroArgsConstructor.newInstance();
            } catch (ReflectiveOperationException e) {
                throw new ConfigMeException("Failed to call constructor for "
                    + zeroArgsConstructor.getDeclaringClass());
            }

            if (propertyValues.size() != properties.size()) {
                throw new ConfigMeException("Invalid property values, " + propertyValues.size() + " were given, but "
                    + zeroArgsConstructor.getDeclaringClass() + " has " + properties.size() + " properties");
            }

            Iterator<FieldProperty> propIt = properties.iterator();
            Iterator<Object> valuesIt = propertyValues.iterator();
            while (propIt.hasNext() && valuesIt.hasNext()) {
                FieldProperty property = propIt.next();
                Object value = valuesIt.next();
                if (value == null) {
                    if (property.getValue(bean) == null) {
                        return null; // No default value on field, return null -> no bean with a null value
                    }
                    errorRecorder.setHasError("Fallback to default value");
                } else {
                    property.setValue(bean, value);
                }
            }
            return bean;
        }
    }

    private static final class RecordInstantiation implements BeanInstantiation {

        private final Constructor<?> canonicalConstructor;
        private final List<BeanPropertyDescription> properties;

        public RecordInstantiation(@NotNull Class<?> clazz, @NotNull List<RecordComponent> components) {
            this.properties = components.stream()
                .map(RecordProperty::new)
                .collect(Collectors.toList());
            Class<?>[] recordTypes = components.stream().map(RecordComponent::getType).toArray(Class[]::new);
            this.canonicalConstructor = getConstructor(clazz, recordTypes)
                .orElseThrow(() -> new ConfigMeException("Could not get canonical constructor of " + clazz));
        }

        @Override
        public @NotNull List<BeanPropertyDescription> getProperties() {
            return properties;
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

    private static final class RecordProperty implements BeanPropertyDescription {

        private final RecordComponent recordComponent;

        private RecordProperty(@NotNull RecordComponent recordComponent) {
            this.recordComponent = recordComponent;
        }

        @Override
        public @NotNull String getName() {
            return recordComponent.getName();
        }

        @Override
        public @NotNull TypeInfo getTypeInformation() {
            return TypeInfo.of(recordComponent.getGenericType());
        }

        @Override
        public @Nullable Object getValue(@NotNull Object bean) {
            throw new UnsupportedOperationException(); // TODO
        }

        @Override
        public @NotNull BeanPropertyComments getComments() {
            return BeanPropertyComments.EMPTY; // TODO
        }
    }
}
