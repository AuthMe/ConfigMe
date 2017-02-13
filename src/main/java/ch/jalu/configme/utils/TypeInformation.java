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

    public boolean isOfType(Class<?> type) {
        return type.isAssignableFrom(clazz);
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
