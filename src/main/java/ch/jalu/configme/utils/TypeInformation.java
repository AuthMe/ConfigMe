package ch.jalu.configme.utils;

import ch.jalu.configme.exception.ConfigMeException;

import javax.annotation.Nullable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Encapsulates type information.
 */
public class TypeInformation<T> {

    private final Class<T> clazz;
    @Nullable
    private final Type genericType;

    private TypeInformation(Class<T> clazz, @Nullable Type genericType) {
        Objects.requireNonNull(clazz);
        this.clazz = clazz;
        this.genericType = genericType;
    }

    public static <T> TypeInformation<T> of(Class<T> clazz) {
        return new TypeInformation<>(clazz, null);
    }

    public static <T> TypeInformation<T> of(Class<T> clazz, Type genericType) {
        return new TypeInformation<>(clazz, genericType);
    }

    public Class<T> getClazz() {
        return clazz;
    }

    /**
     * Returns whether this type is equal to or a child of the given type.
     * <p>
     * Examples:
     * <code>TypeInformation.of(Integer.class).isOfType(Number.class); // true</code>
     * <code>TypeInformation.of(String.class).isOfType(Number.class); // false</code>
     *
     * @param type the type to check
     * @return true if this type extends or is equal to the given type
     */
    public boolean isOfType(Class<?> type) {
        return type.isAssignableFrom(clazz);
    }

    /**
     * Builds a TypeInformation object for the underlying generic type at the given index.
     * For example, given a TypeInformation instance of {@code List<Iterable<Double>>},
     * a TypeInformation object for {@code Iterable<Double>} is returned.
     *
     * @param index the index of the generic type to use
     * @return type information for the "below" type
     */
    public TypeInformation<?> buildGenericType(int index) {
        // if this = List<String>, then getGenericType(0) = String.class
        Type genericType = getGenericType(index);
        if (genericType instanceof Class<?>) {
            return of((Class<?>) genericType);
        }
        // if this = List<Optional<String>>, then genericType(0) = ParameterizedType for Optional<String>
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            Type rawType = pt.getRawType();
            return of((Class<?>) rawType, pt);
        }
        throw new ConfigMeException("Generic type '" + genericType + "' at index " + index + " not recognized");
    }

    @Nullable
    public Class<?> getGenericClass(int index) {
        Type type = getGenericType(index);
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        }
        throw new ConfigMeException(this + " does not have a concrete generic type at index " + index);
    }

    private Type getGenericType(int index) {
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            Type[] actualTypeArguments = pt.getActualTypeArguments();
            if (actualTypeArguments.length > index) {
                return actualTypeArguments[index];
            }
            throw new ConfigMeException("Generic type count in " + this + " too low for index " + index);
        }
        throw new ConfigMeException(this + " has no generic type");
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof TypeInformation<?>) {
            TypeInformation<?> otherType = (TypeInformation<?>) other;
            return Objects.equals(this.clazz, otherType.clazz)
                && Objects.equals(this.genericType, otherType.genericType);
        }
        return false;
    }

    @Override
    public String toString() {
        return "TypeInformation[clazz=" + clazz + ";genericType=" + genericType + "]";
    }
}
