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

    public TypeInformation(Type type) {
        this.type = type;
    }

    public static TypeInformation fromField(Field field) {
        return new TypeInformation(field.getGenericType());
    }

    @Nullable
    public Type getType() {
        return type;
    }

    @Nullable
    public Class<?> getSafeToWriteClass() {
        return getSafeToWriteClassInternal(type);
    }

    public Class<?> getSafeToReadClass() {
        Class<?> safeToReadClass = getSafeToReadClassInternal(type);
        return safeToReadClass == null ? Object.class : safeToReadClass;
    }

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

    @Nullable
    public Class<?> getGenericTypeAsClass(int index) {
        TypeInformation genericType = getGenericType(index);
        return genericType == null ? null : genericType.getSafeToWriteClass();
    }

    @Nullable
    private static Class<?> getSafeToWriteClassInternal(Type type) {
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
