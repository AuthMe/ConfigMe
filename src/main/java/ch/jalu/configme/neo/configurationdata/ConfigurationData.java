package ch.jalu.configme.neo.configurationdata;

import ch.jalu.configme.neo.properties.Property;

import java.util.List;

public interface ConfigurationData {

    List<Property<?>> getAllProperties();

    String[] getCommentsForSection(String path); // TODO: Subject to change

}
