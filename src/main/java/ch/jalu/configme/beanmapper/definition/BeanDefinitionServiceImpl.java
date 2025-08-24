package ch.jalu.configme.beanmapper.definition;

import ch.jalu.configme.beanmapper.definition.properties.BeanFieldPropertyDefinition;
import ch.jalu.configme.beanmapper.definition.properties.BeanPropertyDefinition;
import ch.jalu.configme.beanmapper.definition.properties.BeanPropertyExtractor;
import ch.jalu.configme.beanmapper.definition.properties.BeanPropertyExtractorImpl;
import ch.jalu.configme.internal.ReflectionHelper;
import ch.jalu.configme.internal.record.RecordComponent;
import ch.jalu.configme.internal.record.RecordInspector;
import ch.jalu.configme.internal.record.RecordInspectorImpl;
import ch.jalu.typeresolver.reflect.ConstructorUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of {@link BeanDefinitionService}: defines how bean classes can be created.
 * <p>
 * This service can handle two different types of classes as beans:<ul>
 *  <li>Regular Java classes with a <b>no-args constructor</b>: all fields that aren't static or transient
 *      will be considered as bean properties. Must have at least one property.</li>
 *  <li>Java records</li>
 * </ul>
 *
 * See {@link BeanPropertyExtractor} for details on how the properties are determined for a bean class.
 */
public class BeanDefinitionServiceImpl implements BeanDefinitionService {

    private final RecordInspector recordInspector;
    private final BeanPropertyExtractor beanPropertyExtractor;
    private final Map<Class<?>, BeanDefinition> cachedDefinitionsByType = new ConcurrentHashMap<>();

    public BeanDefinitionServiceImpl() {
        this.recordInspector = new RecordInspectorImpl(new ReflectionHelper());
        this.beanPropertyExtractor = new BeanPropertyExtractorImpl();
    }

    public BeanDefinitionServiceImpl(@NotNull RecordInspector recordInspector,
                                     @NotNull BeanPropertyExtractor beanPropertyExtractor) {
        this.recordInspector = recordInspector;
        this.beanPropertyExtractor = beanPropertyExtractor;
    }

    protected final @NotNull RecordInspector getRecordInspector() {
        return recordInspector;
    }

    protected final @NotNull BeanPropertyExtractor getBeanPropertyExtractor() {
        return beanPropertyExtractor;
    }

    protected final @NotNull Map<Class<?>, BeanDefinition> getCachedDefinitionsByType() {
        return cachedDefinitionsByType;
    }

    @Override
    public @NotNull Optional<BeanDefinition> findDefinition(@NotNull Class<?> clazz) {
        BeanDefinition cachedDefinition = cachedDefinitionsByType.get(clazz);
        if (cachedDefinition != null) {
            return Optional.of(cachedDefinition);
        }

        BeanDefinition definition = createDefinitionIfApplicable(clazz);
        if (definition != null) {
            cachedDefinitionsByType.put(clazz, definition);
            return Optional.of(definition);
        }
        return Optional.empty();
    }

    /**
     * Inspects the class and returns an appropriate definition for it, if available. Null is returned if no
     * definition could be found for the class.
     *
     * @param clazz the class to process
     * @return bean definition for the class, or null if not applicable
     */
    protected @Nullable BeanDefinition createDefinitionIfApplicable(@NotNull Class<?> clazz) {
        RecordComponent[] recordComponents = recordInspector.getRecordComponents(clazz);
        if (recordComponents != null) {
            List<BeanPropertyDefinition> properties =
                beanPropertyExtractor.collectPropertiesForRecord(clazz, recordComponents);

            return new RecordBeanDefinition(clazz, properties);
        }

        Constructor<?> zeroArgConstructor = ConstructorUtils.getConstructorOrNull(clazz);
        if (zeroArgConstructor != null) {
            List<BeanFieldPropertyDefinition> properties = beanPropertyExtractor.collectProperties(clazz);
            if (!properties.isEmpty()) {
                return new ZeroArgConstructorBeanDefinition(zeroArgConstructor, properties);
            }
        }

        return null;
    }
}
