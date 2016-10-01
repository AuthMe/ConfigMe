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

    /**
     * Generates a list of property entries for the given bean's data.
     *
     * @param beanProperty the bean property
     * @param value the value of the bean property
     * @param <B> the bean type
     * @return list of all property entries necessary to export the bean
     */
    public <B> List<PropertyEntry> generate(BeanProperty<B> beanProperty, B value) {
        List<PropertyEntry> properties = new ArrayList<>();
        collectPropertiesFromBean(value, beanProperty.getPath(), properties);
        return properties;
    }

    /**
     * Processes a bean class and handles all of its writable properties. Throws an exception
     * for non-beans or classes with no writable property.
     *
     * @param bean the bean to process
     * @param path the path of the bean in the config structure
     * @param properties list of properties to add to
     */
    protected void collectPropertiesFromBean(Object bean, String path, List<PropertyEntry> properties) {
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

    /**
     * Creates property entries for the provided value, recursively for beans.
     *
     * @param value the value to process
     * @param path the path of the value in the config structure
     * @param properties list of properties to add to
     */
    protected void collectPropertyEntries(Object value, String path, List<PropertyEntry> properties) {
        if (value instanceof String || value instanceof Enum<?>
            || value instanceof Number || value instanceof Boolean) {
            addEntry(properties, new ConstantValueProperty<>(path, value));
        } else if (value instanceof Collection<?>) {
            handleCollection((Collection<?>) value, path, properties);
        } else if (value instanceof Map<?, ?>) {
            for (Map.Entry<String, ?> entry : ((Map<String, ?>) value).entrySet()) {
                collectPropertyEntries(entry.getValue(), path + "." + entry.getKey(), properties);
            }
        } else {
            Objects.requireNonNull(value);
            // At this point it can only be a bean; the bean method checks that the class is a bean
            // and throws an exception otherwise, so we can just delegate the value to that method
            collectPropertiesFromBean(value, path, properties);
        }
    }

    /**
     * Handles a value that is of {@link Collection} type.
     *
     * @param value the collection
     * @param path the path of the collection in the config structure
     * @param properties list of properties to add to
     */
    protected void handleCollection(Collection<?> value, String path, List<PropertyEntry> properties) {
        // TODO: how to loosely couple a Collection<T> value with a property resource?
        // For now we just create a String array... (best effort)
        String[] strings = new String[value.size()];
        int i = 0;
        for (Object o : value) {
            strings[i] = String.valueOf(o);
            ++i;
        }
        addEntry(properties, new ConstantValueListProperty(path, strings));
    }

    private static void addEntry(List<PropertyEntry> properties, Property<?> property) {
        properties.add(new PropertyEntry(property));
    }


    // -----------------------------------------
    // Property implementations

    /**
     * Property implementation that always returns the provided value.
     * Use {@link ConstantValueListProperty} for string lists so it is properly detected to be a string list
     * in the property resource.
     *
     * @param <T> the property type
     */
    private static final class ConstantValueProperty<T> extends Property<T> {

        /**
         * Constructor.
         *
         * @param path the path of the property
         * @param value the value to <i>always</i> return
         */
        ConstantValueProperty(String path, T value) {
            super(path, value);
        }

        @Override
        protected T getFromResource(PropertyResource resource) {
            // default value is the actual value we need (see constructor)
            return getDefaultValue();
        }
    }

    /**
     * Property that always returns the provided string list.
     */
    // We need to use this class here instead of the more generic ConstantValueProperty class
    // because YamlFileResource checks if the property type is of type StringListProperty
    private static final class ConstantValueListProperty extends StringListProperty {

        /**
         * Constructor.
         *
         * @param path the path of the property
         * @param entries the entries of the list that will always be returned
         */
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
