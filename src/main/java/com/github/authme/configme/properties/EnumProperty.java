package com.github.authme.configme.properties;


import org.bukkit.configuration.file.FileConfiguration;

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
    public E getFromFile(FileConfiguration configuration) {
        String textValue = configuration.getString(getPath());
        if (textValue == null) {
            return getDefaultValue();
        }
        E mappedValue = mapToEnum(textValue);
        return mappedValue == null ? getDefaultValue() : mappedValue;
    }

    @Override
    public boolean isPresent(FileConfiguration configuration) {
        return super.isPresent(configuration) && mapToEnum(configuration.getString(getPath())) != null;
    }

    @Override
    public String toYaml(FileConfiguration configuration) {
        E value = getFromFile(configuration);
        return getSingleQuoteYaml().dump(value.name());
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
