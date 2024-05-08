package ch.jalu.configme.beanmapper.instantiation;

import ch.jalu.configme.beanmapper.propertydescription.BeanDescriptionFactory;
import ch.jalu.configme.beanmapper.propertydescription.BeanDescriptionFactoryImpl;
import ch.jalu.configme.beanmapper.propertydescription.BeanFieldPropertyDescription;
import ch.jalu.configme.internal.ReflectionHelper;
import ch.jalu.configme.internal.record.RecordComponent;
import ch.jalu.configme.internal.record.RecordInspector;
import ch.jalu.configme.internal.record.RecordInspectorImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of {@link BeanInstantiationService}: defines how bean classes can be created.
 * <p>
 * This service can handle two different types of classes as beans:<ul>
 *  <li>Regular Java classes with a <b>no-args constructor</b>: all fields that aren't static or transient
 *      will be considered as bean properties.</li>
 *  <li>Java records</li>
 * </ul>
 *
 * See {@link BeanDescriptionFactory} for details on how the properties are determined for a bean class.
 */
public class BeanInstantiationServiceImpl implements BeanInstantiationService {

    private final RecordInspector recordInspector;
    private final BeanDescriptionFactory beanDescriptionFactory;
    private final Map<Class<?>, BeanInstantiation> cachedInstantiationsByType = new ConcurrentHashMap<>();

    public BeanInstantiationServiceImpl() {
        this.recordInspector = new RecordInspectorImpl(new ReflectionHelper());
        this.beanDescriptionFactory = new BeanDescriptionFactoryImpl();
    }

    public BeanInstantiationServiceImpl(@NotNull RecordInspector recordInspector,
                                        @NotNull BeanDescriptionFactory beanDescriptionFactory) {
        this.recordInspector = recordInspector;
        this.beanDescriptionFactory = beanDescriptionFactory;
    }

    @Override
    public @NotNull Optional<BeanInstantiation> findInstantiation(@NotNull Class<?> clazz) {
        BeanInstantiation cachedInstantiation = cachedInstantiationsByType.get(clazz);
        if (cachedInstantiation != null) {
            return Optional.of(cachedInstantiation);
        }

        BeanInstantiation instantiation = createInstantiation(clazz);
        if (instantiation != null) {
            cachedInstantiationsByType.put(clazz, instantiation);
            return Optional.of(instantiation);
        }
        return Optional.empty();
    }

    /**
     * Inspects the class and returns an appropriate instantiation for it, if available. Null is returned if the
     * class cannot be treated as a bean.
     *
     * @param clazz the class to process
     * @return bean instantiation for the class, or null if not applicable
     */
    protected @Nullable BeanInstantiation createInstantiation(@NotNull Class<?> clazz) {
        RecordComponent[] recordComponents = recordInspector.getRecordComponents(clazz);
        if (recordComponents != null) {
            List<BeanFieldPropertyDescription> properties =
                beanDescriptionFactory.collectPropertiesForRecord(clazz, recordComponents);

            return new BeanRecordInstantiation(clazz, properties);
        }

        Optional<Constructor<?>> zeroArgConstructor = tryFindConstructor(clazz);
        if (zeroArgConstructor.isPresent()) {
            List<BeanFieldPropertyDescription> properties = beanDescriptionFactory.collectProperties(clazz);
            if (!properties.isEmpty()) {
                return new BeanZeroArgConstructorInstantiation(zeroArgConstructor.get(), properties);
            }
        }

        return null;
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

    protected final @NotNull RecordInspector getRecordInspector() {
        return recordInspector;
    }

    protected final @NotNull BeanDescriptionFactory getBeanDescriptionFactory() {
        return beanDescriptionFactory;
    }

    protected final @NotNull Map<Class<?>, BeanInstantiation> getCachedInstantiationsByType() {
        return cachedInstantiationsByType;
    }
}
