package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.PropertyType;
import ch.jalu.configme.resource.PropertyReader;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ListProperty<T> extends BaseProperty<List<T>> {

    private final PropertyType<T> type;

    /**
     * Constructor.
     *
     * @param path         the path of the property
     * @param defaultValue the default value of the property
     * @param type         the property type
     */
    public ListProperty(String path, List<T> defaultValue, PropertyType<T> type) {
        super(path, defaultValue);

        this.type = type;
    }

    @Nullable
    @Override
    protected List<T> getFromResource(PropertyReader reader) {
        // Get raw list from reader.
        List<?> rawList = reader.getList(this.getPath());

        // If raw list is null, then return default value
        if (rawList == null) {
            return this.getDefaultValue();
        }

        List<T> list = new ArrayList<>();

        // Iterate objects from raw list and convert it to T. If converted value is not null, add it to list
        for (Object object : rawList) {
            T t = this.type.convert(object);

            if (t != null) {
                list.add(t);
            }
        }

        return list;
    }

    @Nullable
    @Override
    public Object toExportValue(List<T> value) {
        List<Object> list = new ArrayList<>();

        for (T t : value) {
            list.add(this.type.toExportValue(t));
        }

        return list;
    }

}
