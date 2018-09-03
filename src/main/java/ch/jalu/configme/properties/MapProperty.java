package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.PropertyType;
import ch.jalu.configme.resource.PropertyReader;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class MapProperty<V> extends BaseProperty<Map<String, V>> {

    private final PropertyType<V> type;

    /**
     * Constructor.
     *
     * @param path         the path of the property
     * @param defaultValue the default value of the property
     * @param type         the property type
     */
    public MapProperty(String path, Map<String, V> defaultValue, PropertyType<V> type) {
        super(path, defaultValue);

        this.type = type;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    protected Map<String, V> getFromResource(PropertyReader reader) {
        Map<String, ?> rawMap = (Map<String, ?>) reader.getObject(this.getPath());

        // If map is null, then return default value.
        if (rawMap == null) {
            return this.getDefaultValue();
        }

        Map<String, V> map = new HashMap<>();

        for (Map.Entry<String, ?> entry : rawMap.entrySet()) {
            V value = this.type.get(reader, this.getPath() + "." + entry.getKey()); // We are find value for key 'this.path + entry.path'

            if (value != null) {
                map.put(entry.getKey(), value);
            }
        }

        return map;
    }

    @Nullable
    @Override
    public Object toExportValue(Map<String, V> value) {
        Map<String, Object> exportMap = new HashMap<>();

        for (Map.Entry<String, V> entry : value.entrySet()) {
            exportMap.put(entry.getKey(), this.type.toExportValue(entry.getValue()));
        }

        return exportMap;
    }
}
