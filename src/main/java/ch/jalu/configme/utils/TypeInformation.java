package ch.jalu.configme.utils;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Objects;

/**
 * Encapsulates type information.
 */
public class TypeInformation {

    @Nullable
    private final Type type;

    /**
     * Constructor.
     *
     * @param type the type the instance should wrap
     */
    public TypeInformation(Type type) {
        this.type = type;
    }

    /**
     * Creates a new TypeInformation instance based on the given field's type.
     *
     * @param field the field to create a type information for
     * @return type information wrapping the field's type
     */
    public static TypeInformation fromField(Field field) {
        return new TypeInformation(field.getGenericType());
    }

    /**
     * @return the type this instance is wrapping
     */
    @Nullable
    public Type getType() {
        return type;
    }

    /**
     * Returns a {@link Class} object of the wrapped type which is safe for writing to. In other words, if
     * this instance wraps the Type of a field, the returned Class of this method is guaranteed to be a valid
     * value for setting to the field.
     * <p>
     * Examples: <ul>
     * <li>{@code type = String -> result = String.class}</li>
     * <li>{@code type = List<String> -> result = List.class}</li>
     * <li>{@code type = ? super Integer -> result = Integer.class}</li>
     * <li>{@code type = ? extends Comparable -> result = null}</li>
     * </ul>
     *
     * @return the type as a Class which is safe to use for writing
     *         (e.g. setting a value to a field or adding to a collection);
     *         null if not applicable
     */
    public Class<?> getSafeToWriteClass() {
        return getSafeToWriteClassInternal(type);
    }

    /**
     * Returns a {@link Class} object of the wrapped type which is safe for reading. For example, if this instance
     * wraps the Type of a field, then the value on the field is guaranteed to be of the Class type returned by this
     * method (unless the value is null). The returned Class is as specific as possible.
     * <p>
     * Examples: <ul>
     * <li>{@code type = String -> result = String.class}</li>
     * <li>{@code type = List<String> -> result = List.class}</li>
     * <li>{@code type = ? super Integer -> result = Object.class}</li>
     * <li>{@code type = ? extends Comparable -> result = Comparable.class}</li>
     * </ul>
     *
     * @return the type as Class which is safe for reading (e.g. getting field value or reading from a collection)
     */
    public Class<?> getSafeToReadClass() {
        Class<?> safeToReadClass = getSafeToReadClassInternal(type);
        return safeToReadClass == null ? Object.class : safeToReadClass;
    }

    /**
     * "Unwraps" the type and returns the generic type information for the given index, provided the wrapped type
     * contains generic information. Returns null if not applicable.
     * <p>
     * Examples for index = 0:<ul>
     * <li>{@code type = String -> result = null}</li>
     * <li>{@code type = List<String> -> result = String.class}</li>
     * <li>{@code type = Map<List<Integer>, String> -> result = List<Integer>}</li>
     * <li>{@code type = List -> result = null}</li>
     * </ul>
     *
     * @param index the index of the generic type to get (0-based)
     * @return type information representing the generic type info for the given index, null if not applicable
     */
    @Nullable
    public TypeInformation getGenericType(int index) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            return pt.getActualTypeArguments().length > index
                ? new TypeInformation(pt.getActualTypeArguments()[index])
                : null;
        }
        return null;
    }

    /**
     * "Unwraps" the type and returns the generic type information for the given index as Class object,
     * provided the wrapped type contains generic information. Returns null if not applicable, or if the generic
     * type info cannot be converted to a {@link #getSafeToWriteClass safe-to-write} Class.
     * <p>
     * Examples for index = 0:<ul>
     * <li>{@code type = String -> result = null}</li>
     * <li>{@code type = List<String> -> result = String.class}</li>
     * <li>{@code type = Map<List<Integer>, String> -> result = List.class}</li>
     * <li>{@code type = List -> result = null}</li>
     * </ul>
     *
     * @param index the index of the generic type to get (0-based)
     * @return type information representing the generic type info for the given index, null if not applicable
     */
    @Nullable
    public Class<?> getGenericTypeAsClass(int index) {
        TypeInformation genericType = getGenericType(index);
        return genericType == null ? null : genericType.getSafeToWriteClass();
    }

    @Nullable
    private Class<?> getSafeToWriteClassInternal(Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            // Only Class is possible in current implementations, so cast
            return (Class<?>) pt.getRawType();
        }
        return null;
    }

    @Nullable
    private Class<?> getSafeToReadClassInternal(Type type) {
        Class<?> safeToWriteClass = getSafeToWriteClassInternal(type);
        if (safeToWriteClass != null) {
            return safeToWriteClass;
        }

        Type[] bounds = null;
        if (type instanceof WildcardType) {
            bounds = ((WildcardType) type).getUpperBounds();
        } else if (type instanceof TypeVariable<?>) {
            bounds = ((TypeVariable<?>) type).getBounds();
        }

        if (bounds != null) {
            return Arrays.stream(bounds)
                .map(this::getSafeToReadClassInternal)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        }
        return null;
    }

    @Override
    public String toString() {
        return "TypeInformation[type=" + type + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof TypeInformation) {
            TypeInformation other = (TypeInformation) obj;
            return Objects.equals(this.type, other.type);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.type == null ? 0 : this.type.hashCode();
    }
}
