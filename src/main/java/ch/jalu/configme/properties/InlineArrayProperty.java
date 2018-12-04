package ch.jalu.configme.properties;

import ch.jalu.configme.properties.inlinearray.InlineArrayConverter;
import ch.jalu.configme.resource.PropertyReader;

import java.util.Objects;

/**
 * Array property which reads and stores its value as one String in which the elements
 * are separated by a delimiter.
 *
 * @param <T> the array element type
 */
public class InlineArrayProperty<T> extends BaseProperty<T[]> {

    private final InlineArrayConverter<T> inlineConverter;

    /**
     * Constructor.
     *
     * @param path the path of the property
     * @param defaultValue the default value of the property
     * @param inlineConverter the inline converter to use
     */
    public InlineArrayProperty(String path, T[] defaultValue, InlineArrayConverter<T> inlineConverter) {
        super(path, defaultValue);
        Objects.requireNonNull(inlineConverter, "inlineConverter");
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
