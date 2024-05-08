package ch.jalu.configme.beanmapper.instantiation;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Service for the creation of beans.
 *
 * @see BeanInstantiationServiceImpl
 */
public interface BeanInstantiationService {

    /**
     * Inspects the given class and returns an optional with an object defining how to instantiate the bean;
     * an empty optional is returned if the class cannot be treated as a bean.
     *
     * @param clazz the class to inspect
     * @return optional with the instantiation, empty optional if not possible
     */
    @NotNull Optional<BeanInstantiation> findInstantiation(@NotNull Class<?> clazz);

}
