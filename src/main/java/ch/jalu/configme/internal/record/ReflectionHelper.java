package ch.jalu.configme.internal.record;

import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.typeresolver.classutil.ClassUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

/**
 * Internal helper for reflective operations.
 */
public class ReflectionHelper {

    /**
     * Loads the class by fully qualified name, throwing an exception if the class does not exist.
     *
     * @param name the name of the class to return (e.g. java.lang.Integer)
     * @return the requested class
     */
    public @NotNull Class<?> getClassOrThrow(@NotNull String name) {
        return ClassUtils.loadClassOrThrow(name);
    }

    /**
     * Returns the method with the given name on the given class. The method is assumed to have zero arguments;
     * if it doesn't exist, a runtime exception is thrown.
     *
     * @param declarer the class declaring the method
     * @param name the name of the method to retrieve
     * @return the specified method
     */
    public @NotNull Method getZeroArgMethod(@NotNull Class<?> declarer, @NotNull String name) {
        try {
            return declarer.getDeclaredMethod(name);
        } catch (NoSuchMethodException e) {
            throw new ConfigMeException("Could not get " + declarer.getSimpleName() + "#" + name + " method", e);
        }
    }

    /**
     * Invokes the given method (with zero arguments) on the given {@code instance} object. A runtime exception is
     * thrown if the method invocation failed.
     *
     * @param method the method to invoke
     * @param instance the object to invoke it on
     * @param <T> the return type (type is not statically checked)
     * @return the return value of the method
     */
    @SuppressWarnings("unchecked")
    // TODO: @NotNull on return value not generically valid - revise?
    public <T> @NotNull T invokeZeroArgMethod(@NotNull Method method, @Nullable Object instance) {
        try {
            return (T) method.invoke(instance);
        } catch (ReflectiveOperationException e) {
            throw new ConfigMeException("Failed to call " + method + " for " + instance, e);
        }
    }

    /**
     * Makes the given accessible object (e.g. a field) accessible if it isn't yet.
     *
     * @param accessibleObject the reflected object to make accessible (if needed)
     */
    public static void setAccessibleIfNeeded(AccessibleObject accessibleObject) {
        // #347: Catch InaccessibleObjectException, use #trySetAccessible?
        if (!accessibleObject.isAccessible()) {
            try {
                accessibleObject.setAccessible(true);
            } catch (SecurityException e) {
                throw new ConfigMeException("Failed to make " + accessibleObject + " accessible", e);
            }
        }
    }
}
