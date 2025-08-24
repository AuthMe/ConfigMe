package ch.jalu.configme.properties;

import ch.jalu.configme.beanmapper.DefaultMapper;
import ch.jalu.configme.beanmapper.Mapper;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.types.BeanPropertyType;
import ch.jalu.typeresolver.TypeInfo;
import org.jetbrains.annotations.NotNull;

/**
 * Property whose value is a bean class: maps to a custom Java class (called a "bean")
 * using a {@link Mapper}.
 *
 * @param <T> the bean type
 * @link <a href="https://github.com/AuthMe/ConfigMe/wiki/Bean-properties">ConfigMe Wiki: Bean properties</a>
 */
public class BeanProperty<T> extends TypeBasedProperty<T> {

    public BeanProperty(@NotNull String path, @NotNull Class<T> beanType, @NotNull T defaultValue) {
        this(path, beanType, defaultValue, DefaultMapper.getInstance());
    }

    public BeanProperty(@NotNull String path, @NotNull Class<T> beanType, @NotNull T defaultValue,
                        @NotNull Mapper mapper) {
        super(path, BeanPropertyType.of(beanType, mapper), defaultValue);
    }

    public BeanProperty(@NotNull String path, @NotNull BeanPropertyType<T> type, @NotNull T defaultValue) {
        super(path, type, defaultValue);
    }

    /**
     * Constructor. Allows to instantiate bean properties with generic types. Since it is hard to validate that
     * the default value is actually correct, it is recommended to extend this class with specific type parameters.
     *
     * @param path the path
     * @param beanType the bean type
     * @param defaultValue the default value
     * @param mapper the mapper to map with
     */
    protected BeanProperty(@NotNull String path, @NotNull TypeInfo beanType, @NotNull T defaultValue,
                           @NotNull Mapper mapper) {
        super(path, new BeanPropertyType<>(beanType, mapper), defaultValue);


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
