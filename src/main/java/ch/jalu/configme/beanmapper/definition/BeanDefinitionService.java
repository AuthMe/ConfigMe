package ch.jalu.configme.beanmapper.definition;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Service for the definition of bean types.
 *
 * @see BeanDefinitionServiceImpl
 */
public interface BeanDefinitionService {

    /**
     * Inspects the given class and returns an optional with an object defining the bean,
     * if the given class can be treated as a bean type.
     *
     * @param clazz the class to inspect
     * @return optional with the definition, empty optional if not possible
     */
    @NotNull Optional<BeanDefinition> findDefinition(@NotNull Class<?> clazz);

}
