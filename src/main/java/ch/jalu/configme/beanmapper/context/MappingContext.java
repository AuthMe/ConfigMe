package ch.jalu.configme.beanmapper.context;

import ch.jalu.configme.beanmapper.ConfigMeMapperException;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.typeresolver.TypeInfo;
import org.jetbrains.annotations.NotNull;

/**
 * Holds necessary information for a certain value that is being mapped in the bean mapper.
 */
public interface MappingContext {

    /**
     * Creates a child context with the given path addition ("name") and type information.
     *
     * @param name additional path element to append to this context's path
     * @param targetType the required type
     * @return new child context
     */
    @NotNull MappingContext createChild(@NotNull String name, @NotNull TypeInfo targetType);

    /**
     * Returns the path, from the root of the bean, that is being mapped. In other words, this is a local path
     * relative to the bean root. The bean root is always {@code ""} (empty string).
     *
     * @return local path of the bean currently being mapped
     */
    @NotNull String getBeanPath();

    /**
     * @return the type the value should be mapped to
     */
    @NotNull TypeInfo getTargetType();

    /**
     * Returns the target type as {@link Class}, throwing an exception if it cannot be converted.
     *
     * @return the target type as a class
     */
    default @NotNull Class<?> getTargetTypeAsClassOrThrow() {
        Class<?> targetClass = getTargetType().toClass();
        if (targetClass == null) {
            throw new ConfigMeMapperException(this, "The target type cannot be converted to a class");
        }
        return targetClass;
    }

    /**
     * Convenience method: returns the type argument at the given index, guaranteeing that it exists
     * and that it can be converted to a safe-to-write class. Throws an exception otherwise.
     *
     * @param index the index to get generic type info for
     * @return the generic type info (throws exception if absent or not precise enough)
     */
    default @NotNull TypeInfo getTargetTypeArgumentOrThrow(int index) {
        TypeInfo typeArgument = getTargetType().getTypeArgumentInfo(index);
        if (typeArgument == null || typeArgument.toClass() == null) {
            throw new ConfigMeMapperException(this, "The type argument at index " + index + " is not well defined");
        }
        return typeArgument;
    }

    /**
     * @return textual representation of the info in the context, used in exceptions
     */
    @NotNull String createDescription();

    /**
     * Registers an error during the mapping process, which delegates to the supplied
     * {@link ch.jalu.configme.properties.convertresult.ConvertErrorRecorder ConvertErrorRecorder},
     * associated to the property this conversion is being performed for.
     *
     * @param reason the error reason (ignored by the default context implementation)
     */
    default void registerError(@NotNull String reason) {
        getErrorRecorder().setHasError("For bean path '" + getBeanPath() + "': " + reason);
    }

    /**
     * @return error recorder to register errors even when a value can be created
     */
    @NotNull ConvertErrorRecorder getErrorRecorder();
}
