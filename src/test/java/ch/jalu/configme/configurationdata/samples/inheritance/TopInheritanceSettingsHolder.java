package ch.jalu.configme.configurationdata.samples.inheritance;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class TopInheritanceSettingsHolder implements SettingsHolder {

    public static final Property<String> STRING_FROM_TOP = newProperty("top.string", "aaa");

}
