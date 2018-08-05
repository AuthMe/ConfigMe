package ch.jalu.configme.configurationdata;

import ch.jalu.configme.properties.Property;
import ch.jalu.configme.resource.PropertyReader;

import java.util.List;

public interface ConfigurationData {

    List<Property<?>> getProperties();

    List<String> getCommentsForSection(String path);

    void initializeValues(PropertyReader propertyReader);

    <T> T getValue(Property<T> property);

    <T> void setValue(Property<T> property, T value);

}
