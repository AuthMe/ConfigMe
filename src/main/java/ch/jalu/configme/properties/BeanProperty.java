package ch.jalu.configme.properties;

import ch.jalu.configme.beanmapper.DefaultMapper;
import ch.jalu.configme.beanmapper.Mapper;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.types.BeanPropertyType;
import ch.jalu.typeresolver.TypeInfo;
import org.jetbrains.annotations.NotNull;

public class BeanProperty<T> extends TypeBasedProperty<T> {

    public BeanProperty(@NotNull Class<T> beanType, @NotNull String path, @NotNull T defaultValue) {
        this(beanType, path, defaultValue, DefaultMapper.getInstance());
    }

    public BeanProperty(@NotNull Class<T> beanType, @NotNull String path, @NotNull T defaultValue,
                        @NotNull Mapper mapper) {
        super(path, defaultValue, BeanPropertyType.of(beanType, mapper));
    }

    public BeanProperty(@NotNull String path, @NotNull BeanPropertyType<T> type, @NotNull T defaultValue) {
        super(path, defaultValue, type);
    }

    /**
     * Constructor. Allows to instantiate bean properties with generic types. Since it is hard to validate that
     * the default value is actually correct, it is recommended to extend this class with specific type parameters.
     *
     * @param beanType the bean type
     * @param path the path
     * @param defaultValue the default value
     * @param mapper the mapper to map with
     */
    protected BeanProperty(@NotNull TypeInfo beanType, @NotNull String path, @NotNull T defaultValue,
                           @NotNull Mapper mapper) {
        super(path, defaultValue, new BeanPropertyType<>(beanType, mapper));


        Class<?> beanClass = beanType.toClass();
        if (beanClass == null) {
            throw new IllegalArgumentException("The bean type '" + beanType + "' cannot be converted to Class. "
                + "Use a constructor with a custom BeanPropertyType.");
        } else if (!beanClass.isInstance(defaultValue)) {
            throw new ConfigMeException(
                "Default value for path '" + path + "' does not match bean type '" + beanType + "'");
        }
    }
}
