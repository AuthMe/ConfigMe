package ch.jalu.configme.beanmapper.instantiation;

import ch.jalu.configme.beanmapper.propertydescription.BeanDescriptionFactory;
import ch.jalu.configme.beanmapper.propertydescription.BeanDescriptionFactoryImpl;
import ch.jalu.configme.beanmapper.propertydescription.FieldProperty;
import ch.jalu.configme.internal.record.RecordComponent;
import ch.jalu.configme.internal.record.RecordInspector;
import ch.jalu.configme.internal.record.ReflectionHelper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;

public class BeanInspector {

    private final RecordInspector recordInspector = new RecordInspector(new ReflectionHelper());
    private final BeanDescriptionFactory beanDescriptionFactory = new BeanDescriptionFactoryImpl();

    public @NotNull Optional<BeanInstantiation> findInstantiation(@NotNull Class<?> clazz) {
        if (recordInspector.isRecord(clazz)) {
            RecordComponent[] recordComponents = recordInspector.getRecordComponents(clazz);
            List<FieldProperty> properties =
                beanDescriptionFactory.createRecordProperties(clazz, recordComponents);

            return Optional.of(new RecordInstantiation(clazz, properties));
        }

        Optional<Constructor<?>> zeroArgConstructor = getConstructor(clazz);
        if (zeroArgConstructor.isPresent()) {
            List<FieldProperty> properties = beanDescriptionFactory.getAllProperties(clazz);
            return Optional.of(new BeanZeroArgConstrInstantiation(zeroArgConstructor.get(), properties));
        }

        return Optional.empty();
    }

    static @NotNull Optional<Constructor<?>> getConstructor(@NotNull Class<?> declarer,
                                                            Class<?> @NotNull ... parameters) {
        try {
            return Optional.of(declarer.getDeclaredConstructor(parameters));
        } catch (NoSuchMethodException ignore) {
            return Optional.empty();
        }
    }
}
