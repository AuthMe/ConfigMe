package ch.jalu.configme.beanmapper.instantiation;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface BeanInstantiationService {

    @NotNull Optional<BeanInstantiation> findInstantiation(@NotNull Class<?> clazz);

}
