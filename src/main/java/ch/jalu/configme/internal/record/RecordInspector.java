package ch.jalu.configme.internal.record;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * Inspects classes and returns Record information (Java 14+). The inspection is performed by reflection
 * because ConfigMe is still compiled against Java 8.
 */
public class RecordInspector {

    private final ReflectionHelper reflectionHelper;
    private Method isRecordMethod; // Class#isRecord
    private Method getRecordComponentsMethod; // Class#getRecordComponents

    private Method getComponentNameMethod; // RecordComponent#getName
    private Method getComponentTypeMethod; // RecordComponent#getType
    private Method getComponentGenericTypeMethod; // RecordComponent#getGenericType

    public RecordInspector(@NotNull ReflectionHelper reflectionHelper) {
        this.reflectionHelper = reflectionHelper;
    }

    /**
     * Returns whether the given class is a record.
     * <p>
     * This method uses {@code Class#isRecord} in a way that is compatible with Java 8 and above.
     *
     * @param clazz the class to inspect
     * @return true if it's a record, false otherwise
     */
    public boolean isRecord(@NotNull Class<?> clazz) {
        // Check superclass to make sure that Class#isRecord will exist, and to avoid redundant reflective
        // calls to the method if we can rule out records anyway
        if (hasRecordAsSuperclass(clazz)) {
            if (isRecordMethod == null) {
                isRecordMethod = reflectionHelper.getZeroArgMethod(Class.class, "isRecord");
            }
            return reflectionHelper.invokeZeroArgMethod(isRecordMethod, clazz);
        }
        return false;
    }

    /**
     * Returns the components that make up the record. This method should only be called after checking that
     * the class is a record ({@link #isRecord(Class)}).
     * <p>
     * This calls {code Class#getRecordComponents} in a way that is compatible with Java 8 and above.
     *
     * @param clazz a record type whose components should be returned
     * @return the record's components
     */
    public RecordComponent @Nullable [] getRecordComponents(@NotNull Class<?> clazz) {
        if (getRecordComponentsMethod == null) {
            getRecordComponentsMethod = reflectionHelper.getZeroArgMethod(Class.class, "getRecordComponents");
        }

        Object[] components = reflectionHelper.invokeZeroArgMethod(getRecordComponentsMethod, clazz);
        if (getComponentGenericTypeMethod == null) {
            Class<?> recordComponentClass = reflectionHelper.getClassOrThrow("java.lang.reflect.RecordComponent");
            getComponentNameMethod = reflectionHelper.getZeroArgMethod(recordComponentClass, "getName");
            getComponentTypeMethod = reflectionHelper.getZeroArgMethod(recordComponentClass, "getType");
            getComponentGenericTypeMethod = reflectionHelper.getZeroArgMethod(recordComponentClass, "getGenericType");
        }

        return Arrays.stream(components)
            .map(this::mapComponent)
            .toArray(RecordComponent[]::new);
    }

    boolean hasRecordAsSuperclass(@NotNull Class<?> clazz) {
        return clazz.getSuperclass() != null
            && "java.lang.Record".equals(clazz.getSuperclass().getName());
    }

    private @NotNull RecordComponent mapComponent(@NotNull Object component) {
        String name = reflectionHelper.invokeZeroArgMethod(getComponentNameMethod, component);
        Class<?> type = reflectionHelper.invokeZeroArgMethod(getComponentTypeMethod, component);
        Type genericType = reflectionHelper.invokeZeroArgMethod(getComponentGenericTypeMethod, component);

        return new RecordComponent(name, type, genericType);
    }
}
