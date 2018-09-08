package ch.jalu.configme.properties;

import ch.jalu.configme.beanmapper.DefaultMapper;
import ch.jalu.configme.beanmapper.Mapper;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.resource.PropertyReader;
import ch.jalu.configme.utils.TypeInformation;

public class BeanProperty<T> extends BaseProperty<T> {

    private final TypeInformation beanType;
    private final Mapper mapper;

    public BeanProperty(Class<T> beanType, String path, T defaultValue) {
        this(beanType, path, defaultValue, DefaultMapper.getInstance());
    }

    public BeanProperty(Class<T> beanType, String path, T defaultValue, Mapper mapper) {
        super(path, defaultValue);
        this.beanType = new TypeInformation(beanType);
        this.mapper = mapper;
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
    protected BeanProperty(TypeInformation beanType, String path, T defaultValue, Mapper mapper) {
        super(path, defaultValue);
        if (!beanType.getSafeToWriteClass().isInstance(defaultValue)) {
            throw new ConfigMeException(
                "Default value for path '" + path + "' does not match bean type '" + beanType + "'");
        }
        this.beanType = beanType;
        this.mapper = mapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected T getFromReader(PropertyReader reader) {
        return (T) mapper.convertToBean(reader.getObject(getPath()), beanType);
    }

    @Override
    public Object toExportValue(T value) {
        return mapper.toExportValue(value);
    }
}
