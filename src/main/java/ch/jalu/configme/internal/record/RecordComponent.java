package ch.jalu.configme.internal.record;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

/**
 * Represents information about the component (a "property") of a Java record.
 */
// This contains the relevant portions of java.lang.reflect.RecordComponent, because ConfigMe still supports
// older versions of Java that don't have records (#347).
public class RecordComponent {

    private final String name;
    private final Class<?> type;
    private final Type genericType;

    /**
     * Constructor.
     *
     * @param name the name of this component
     * @param type the type of this component
     * @param genericType type representing the generic type signature of this component
     */
    public RecordComponent(@NotNull String name, @NotNull Class<?> type, @NotNull Type genericType) {
        this.name = name;
        this.type = type;
        this.genericType = genericType;
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull Class<?> getType() {
        return type;
    }

    public @NotNull Type getGenericType() {
        return genericType;
    }
}
