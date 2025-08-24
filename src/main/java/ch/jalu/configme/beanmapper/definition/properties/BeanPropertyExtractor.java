package ch.jalu.configme.beanmapper.definition.properties;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Factory which analyzes a class and returns all writable properties.
 * <p>
 * Default implementation: {@link BeanPropertyExtractorImpl}.
 */
public interface BeanPropertyExtractor {

    /**
     * Returns all properties on the given class which should be considered while creating a bean of the
     * given type. This is usually all properties which can be read from and written to.
     *
     * @param clazz the class whose properties should be returned
     * @return the relevant properties on the class
     */
    @NotNull Collection<BeanPropertyDefinition> getAllProperties(@NotNull Class<?> clazz);

}
