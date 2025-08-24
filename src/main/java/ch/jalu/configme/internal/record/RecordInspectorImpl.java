package ch.jalu.configme.internal.record;

import ch.jalu.configme.internal.ReflectionHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * Inspects classes and returns Record information (Java 16+). The inspection is performed by reflection
 * because ConfigMe is still compiled with Java 8.
 */
public class RecordInspectorImpl implements RecordInspector {

    private final ReflectionHelper reflectionHelper;
    private Method isRecordMethod; // Class#isRecord
    private Method getRecordComponentsMethod; // Class#getRecordComponents

    private Method getComponentNameMethod; // RecordComponent#getName
    private Method getComponentTypeMethod; // RecordComponent#getType
    private Method getComponentGenericTypeMethod; // RecordComponent#getGenericType

    public RecordInspectorImpl(@NotNull ReflectionHelper reflectionHelper) {
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
    boolean isRecord(@NotNull Class<?> clazz) {
        // Check superclass to make sure that Class#isRecord will exist, and to avoid redundant reflective
        // calls to the method if we can rule out records anyway
        if (hasRecordAsSuperclass(clazz)) {
            if (isRecordMethod == null) {
                isRecordMethod = reflectionHelper.getNoArgMethod(Class.class, "isRecord");
            }
            return reflectionHelper.invokeNoArgMethod(isRecordMethod, clazz);
        }
        return false;
    }

    @Override
    public RecordComponent @Nullable [] getRecordComponents(@NotNull Class<?> clazz) {
        if (!isRecord(clazz)) {
            return null;
        }
        if (getRecordComponentsMethod == null) {
            getRecordComponentsMethod = reflectionHelper.getNoArgMethod(Class.class, "getRecordComponents");
        }

        Object[] components = reflectionHelper.invokeNoArgMethod(getRecordComponentsMethod, clazz);
        if (getComponentGenericTypeMethod == null) {
            Class<?> recordComponentClass = reflectionHelper.getClassOrThrow("java.lang.reflect.RecordComponent");
            getComponentNameMethod = reflectionHelper.getNoArgMethod(recordComponentClass, "getName");
            getComponentTypeMethod = reflectionHelper.getNoArgMethod(recordComponentClass, "getType");
            getComponentGenericTypeMethod = reflectionHelper.getNoArgMethod(recordComponentClass, "getGenericType");
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
        String name = reflectionHelper.invokeNoArgMethod(getComponentNameMethod, component);
        Class<?> type = reflectionHelper.invokeNoArgMethod(getComponentTypeMethod, component);
        Type genericType = reflectionHelper.invokeNoArgMethod(getComponentGenericTypeMethod, component);

        return new RecordComponent(name, type, genericType);
    }
}
