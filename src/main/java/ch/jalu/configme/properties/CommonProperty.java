package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.PropertyType;
import ch.jalu.configme.resource.PropertyReader;

import javax.annotation.Nullable;

public class CommonProperty<T> extends BaseProperty<T> {

    private final PropertyType<T> type;

    /**
     * Constructor.
     *
     * @param path         the path of the property
     * @param defaultValue the default value of the property
     * @param type         the property type
     */
    public CommonProperty(String path, T defaultValue, PropertyType<T> type) {
        super(path, defaultValue);

        this.type = type;
    }

    @Nullable
    @Override
    protected T getFromResource(PropertyReader reader) {
        return this.type.get(reader, this.getPath());
    }

    @Nullable
    @Override
    public Object toExportValue(T value) {
        return this.type.toExportValue(value);
    }

}
