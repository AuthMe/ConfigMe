package ch.jalu.configme.properties;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.properties.types.PropertyType;
import ch.jalu.configme.resource.PropertyReader;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MapProperty<V> extends BaseProperty<Map<String, V>> {

    private final PropertyType<V> type;

    /**
     * Constructor.
     *
     * @param path the path of the property
     * @param defaultValue the default value of the property
     * @param type the property type
     */
    public MapProperty(String path, Map<String, V> defaultValue, PropertyType<V> type) {
        super(path, defaultValue);
        Objects.requireNonNull(type, "type");
        this.type = type;
    }

    @Nullable
    @Override
    protected Map<String, V> getFromReader(PropertyReader reader, ConvertErrorRecorder errorRecorder) {
        Object rawObject = reader.getObject(getPath());

        if (!(rawObject instanceof Map<?, ?>)) {
            return null;
        }

        Map<?, ?> rawMap = (Map<?, ?>) rawObject;
        Map<String, V> map = new HashMap<>();

        for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
            String path = entry.getKey().toString();
            V value = type.convert(entry.getValue(), errorRecorder);

            if (value != null) {
                map.put(path, value);
            }
        }

        return map;
    }

    @Nullable
    @Override
    public Object toExportValue(Map<String, V> value) {
        Map<String, Object> exportMap = new HashMap<>();

        for (Map.Entry<String, V> entry : value.entrySet()) {
            exportMap.put(entry.getKey(), type.toExportValue(entry.getValue()));
        }

        return exportMap;
    }
}
