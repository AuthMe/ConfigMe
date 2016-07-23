package com.github.authme.configme.properties;


import com.github.authme.configme.resource.PropertyResource;

/**
 * Enum property.
 *
 * @param <E> The enum class
 */
public class EnumProperty<E extends Enum<E>> extends Property<E> {

    private Class<E> clazz;

    public EnumProperty(Class<E> clazz, String path, E defaultValue) {
        super(path, defaultValue);
        this.clazz = clazz;
    }

    @Override
    public E getFromReader(PropertyResource resource) {
        String textValue = resource.getString(getPath());
        if (textValue != null) {
            return mapToEnum(textValue);
        }
        return null;
    }

    @Override
    public boolean isPresent(PropertyResource resource) {
        return super.isPresent(resource) && mapToEnum(resource.getString(getPath())) != null;
    }

    private E mapToEnum(String value) {
        for (E entry : clazz.getEnumConstants()) {
            if (entry.name().equalsIgnoreCase(value)) {
                return entry;
            }
        }
        return null;
    }
}
