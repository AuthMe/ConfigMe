package com.github.authme.configme.beanmapper;

import com.github.authme.configme.exception.ConfigMeException;
import com.github.authme.configme.knownproperties.PropertyEntry;
import com.github.authme.configme.properties.Property;
import com.github.authme.configme.properties.StringListProperty;
import com.github.authme.configme.resource.PropertyResource;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Generates {@link PropertyEntry} objects for all "leaf" values of a bean to
 * properly export the values.
 */
public class PropertyEntryGenerator {

    public static <B> List<PropertyEntry> generatePropertyEntries(BeanProperty<B> beanProperty, B value) {
        List<PropertyEntry> properties = new ArrayList<>();
        collectPropertiesFromBean(value, beanProperty.getPath(), properties);
        return properties;
    }

    private static void collectPropertiesFromBean(Object bean, String path, List<PropertyEntry> properties) {
        List<PropertyDescriptor> writableProperties = MapperUtils.getWritableProperties(bean.getClass());
        if (writableProperties.isEmpty()) {
            throw new ConfigMeException("Class '" + bean.getClass() + "' has no writable properties");
        }
        for (PropertyDescriptor property : writableProperties) {
            collectPropertyEntries(
                MapperUtils.getBeanProperty(property, bean),
                path + "." + property.getName(),
                properties);
        }
    }

    // TODO: make non-static to allow extension - or handle differently?
    private static void collectPropertyEntries(Object value, String path, List<PropertyEntry> properties) {
        if (value instanceof String || value instanceof Enum<?>
            || value instanceof Number || value instanceof Boolean) {
            addEntry(properties, new ConstantValueProperty<>(path, value));
        } else if (value instanceof Collection<?>) {
            // TODO: how to loosely couple a Collection<T> value with a property resource?
            // For now we just create a List of String... (best effort)
            Collection<?> coll = (Collection<?>) value;
            String[] strings = new String[coll.size()];
            int i = 0;
            for (Object o : coll) {
                strings[i] = String.valueOf(o);
                ++i;
            }
            addEntry(properties, new ConstantValueListProperty(path, strings));
        } else if (value instanceof Map<?, ?>) {
            for (Map.Entry<String, ?> entry : ((Map<String, ?>) value).entrySet()) {
                collectPropertyEntries(entry.getValue(), path + "." + entry.getKey(), properties);
            }
        } else {
            Objects.requireNonNull(value);
            // At this point it can only be a bean; the bean method checks that the class really is a bean
            // so we just delegate it to the bean method
            collectPropertiesFromBean(value, path, properties);
        }
    }

    private static void addEntry(List<PropertyEntry> properties, Property<?> property) {
        properties.add(new PropertyEntry(property));
    }


    private static final class ConstantValueProperty<T> extends Property<T> {
        ConstantValueProperty(String path, T value) {
            super(path, value);
        }

        @Override
        protected T getFromResource(PropertyResource resource) {
            // default value is the actual value we need (see constructor)
            return getDefaultValue();
        }
    }

    // We need to use this class here instead of the more generic ConstantValueProperty class because YamlFileResource
    // checks if the property type is of type StringListProperty
    private static final class ConstantValueListProperty extends StringListProperty {
        ConstantValueListProperty(String path, String[] entries) {
            super(path, entries);
        }

        @Override
        public List<String> getValue(PropertyResource resource) {
            // default value is the actual value we need (see constructor)
            return getDefaultValue();
        }
    }
}
