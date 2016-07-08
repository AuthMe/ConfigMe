package com.github.authme.configme.samples;

import com.github.authme.configme.SettingsHolder;
import com.github.authme.configme.properties.Property;
import com.github.authme.configme.propertymap.PropertyMap;


import java.lang.reflect.Field;
import java.util.List;

import static com.github.authme.configme.properties.Property.newListProperty;
import static com.github.authme.configme.properties.Property.newProperty;


/**
 * Sample properties for testing purposes.
 */
public final class TestConfiguration implements SettingsHolder {

    public static final Property<Integer> DURATION_IN_SECONDS =
        newProperty("test.duration", 4);

    public static final Property<String> SYSTEM_NAME =
        newProperty("test.systemName", "[TestDefaultValue]");

    public static final Property<TestEnum> RATIO_ORDER =
        newProperty(TestEnum.class, "sample.ratio.order", TestEnum.SECOND);

    public static final Property<List<String>> RATIO_FIELDS =
        newListProperty("sample.ratio.fields", "a", "b", "c");

    public static final Property<Integer> VERSION_NUMBER =
        newProperty("version", 32046);

    public static final Property<Boolean> SKIP_BORING_FEATURES =
        newProperty("features.boring.skip", false);

    public static final Property<List<String>> BORING_COLORS =
        newListProperty("features.boring.colors");

    public static final Property<Integer> DUST_LEVEL =
        newProperty("features.boring.dustLevel", -1);

    public static final Property<Boolean> USE_COOL_FEATURES =
        newProperty("features.cool.enabled", false);

    public static final Property<List<String>> COOL_OPTIONS =
        newListProperty("features.cool.options", "Sparks", "Sprinkles");


    private TestConfiguration() {
    }

    /**
     * Generate a property map with all properties in {@link TestConfiguration}.
     *
     * @return The generated property map
     */
    public static PropertyMap generatePropertyMap() {
        PropertyMap propertyMap = new PropertyMap();
        for (Field field : TestConfiguration.class.getDeclaredFields()) {
            Object fieldValue = getStaticFieldValue(field);
            if (fieldValue instanceof Property<?>) {
                Property<?> property = (Property<?>) fieldValue;
                String[] comments = new String[]{"Comment for '" + property.getPath() + "'"};
                propertyMap.put(property, comments);
            }
        }
        return propertyMap;
    }

    private static Object getStaticFieldValue(Field field) {
        field.setAccessible(true);
        try {
            return field.get(null);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

}
