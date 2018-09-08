package ch.jalu.configme.properties;

import ch.jalu.configme.properties.helper.InlineConvertHelper;
import ch.jalu.configme.properties.types.PropertyType;
import ch.jalu.configme.resource.PropertyReader;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ArrayProperty<T> extends BaseProperty<T[]> {

    private final PropertyType<T> type;

    private final InlineConvertHelper<T> convertHelper;

    /**
     * Constructor.
     *
     * @param path         the path of the property
     * @param defaultValue the default value of the property
     * @param type         the property type
     */
    public ArrayProperty(String path, T[] defaultValue, PropertyType<T> type, InlineConvertHelper<T> convertHelper) {
        super(path, defaultValue);

        this.type = type;
        this.convertHelper = convertHelper;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    protected T[] getFromReader(PropertyReader reader) {
        // Get object from reader.
        Object object = reader.getObject(this.getPath());

        // If object is null, then return null.
        if (object == null) {
            return null;
        }

        if (String.class.isInstance(object) && this.convertHelper != null) {
            return this.convertHelper.fromString((String) object);
        }

        // If object is not collection, then return singleton array.
        if (!(object instanceof Collection<?>)) {
            T[] array = (T[]) Array.newInstance(this.type.getType(), 1);
            array[0] = (T) object;

            return array;
        }

        Collection<?> rawCollection = (Collection<?>) object;
        // We are not know size a array, because if some object is null, then we are not add him to array.
        List<T> list = new ArrayList<>();

        // Iterate collection. If some value after convert is null, we are not add him to list.
        for (Object rawObject : rawCollection) {
            T value = this.type.convert(rawObject);

            if (value != null) {
                list.add(value);
            }
        }

        // Convert list to array.
        // Create array with size 0, because list.toArray() modify array size himself.
        return list.toArray(
            (T[]) Array.newInstance(this.type.getType(), 0)
        );
    }

    @Nullable
    @Override
    public Object toExportValue(T[] value) {
        // If we have a convert helper, then use him
        if (this.convertHelper != null) {
            return this.convertHelper.toExportValue(value);
        }

        Object[] array = new Object[value.length];

        for (int i = 0; i < array.length; i++) {
            array[i] = this.type.toExportValue(value[i]);
        }

        return array;
    }

}
