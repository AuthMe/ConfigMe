package ch.jalu.configme.properties;

import ch.jalu.configme.properties.helper.InlineArrayConverter;
import ch.jalu.configme.resource.PropertyReader;

public class InlineArrayProperty<T> extends BaseProperty<T[]> {

    private final InlineArrayConverter<T> inlineConverter;

    /**
     * Constructor.
     *
     * @param path the path of the property
     * @param defaultValue the default value of the property
     * @param inlineConverter inline converter
     */
    public InlineArrayProperty(String path, T[] defaultValue, InlineArrayConverter<T> inlineConverter) {
        super(path, defaultValue);
        this.inlineConverter = inlineConverter;
    }

    @Override
    protected T[] getFromReader(PropertyReader reader) {
        String value = reader.getString(getPath());
        return value == null ? null : inlineConverter.fromString(value);
    }

    @Override
    public Object toExportValue(T[] value) {
        return inlineConverter.toExportValue(value);
    }
}
