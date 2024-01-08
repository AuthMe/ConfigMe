package ch.jalu.configme.internal.record;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utilities for Java records. They are abstracted by this type because ConfigMe is compiled with Java 8.
 * This class will be removed once ConfigMe upgrades to Java {@code >= 14}.
 */
public interface RecordInspector {

    /**
     * Returns the components that make up the record.
     * <p>
     * This calls {@link Class#getRecordComponents} in a way that is compatible with Java 8 and above.
     *
     * @param clazz the class whose record components should be returned
     * @return the record's components, null if the class is not a record
     */
    RecordComponent @Nullable [] getRecordComponents(@NotNull Class<?> clazz);

}
