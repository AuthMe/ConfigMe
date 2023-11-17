package ch.jalu.configme.beanmapper.instantiation;

import ch.jalu.configme.beanmapper.propertydescription.BeanDescriptionFactory;
import ch.jalu.configme.beanmapper.propertydescription.BeanDescriptionFactoryImpl;
import ch.jalu.configme.beanmapper.propertydescription.BeanFieldPropertyDescription;
import ch.jalu.configme.internal.record.RecordComponent;
import ch.jalu.configme.internal.record.RecordInspector;
import ch.jalu.configme.internal.record.ReflectionHelper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class BeanInstantiationServiceImpl implements BeanInstantiationService {

    private final RecordInspector recordInspector = new RecordInspector(new ReflectionHelper());
    private final BeanDescriptionFactory beanDescriptionFactory = new BeanDescriptionFactoryImpl();
    private final Map<Class<?>, BeanInstantiation> cachedInstantiationByType = new ConcurrentHashMap<>();

    @Override
    public @NotNull Optional<BeanInstantiation> findInstantiation(@NotNull Class<?> clazz) {
        BeanInstantiation cachedInstantiation = cachedInstantiationByType.get(clazz);
        if (cachedInstantiation != null) {
            return Optional.of(cachedInstantiation);
        }

        if (recordInspector.isRecord(clazz)) {
            RecordComponent[] recordComponents = recordInspector.getRecordComponents(clazz);
            List<BeanFieldPropertyDescription> properties =
                beanDescriptionFactory.createRecordProperties(clazz, recordComponents);

            BeanRecordInstantiation recordInstantiation = new BeanRecordInstantiation(clazz, properties);
            cachedInstantiationByType.put(clazz, recordInstantiation);
            return Optional.of(recordInstantiation);
        }

        Optional<Constructor<?>> zeroArgConstructor = tryFindConstructor(clazz);
        if (zeroArgConstructor.isPresent()) {
            List<BeanFieldPropertyDescription> properties = beanDescriptionFactory.getAllProperties(clazz);
            BeanZeroArgConstrInstantiation zeroArgConstrInstantiation =
                new BeanZeroArgConstrInstantiation(zeroArgConstructor.get(), properties);
            cachedInstantiationByType.put(clazz, zeroArgConstrInstantiation);
            return Optional.of(zeroArgConstrInstantiation);
        }

        return Optional.empty();
    }

    /**
     * Returns an optional with the constructor on the given {@code declarer} matching the parameter types,
     * otherwise an empty optional.
     *
     * @param declarer the class to search for constructors
     * @param parameterTypes the parameter types of the desired constructor
     * @return optional with the constructor if found, empty optional otherwise
     */
    protected static @NotNull Optional<Constructor<?>> tryFindConstructor(@NotNull Class<?> declarer,
                                                                          Class<?> @NotNull ... parameterTypes) {
        try {
            return Optional.of(declarer.getDeclaredConstructor(parameterTypes));
        } catch (NoSuchMethodException ignore) {
            return Optional.empty();
        }
    }
}
