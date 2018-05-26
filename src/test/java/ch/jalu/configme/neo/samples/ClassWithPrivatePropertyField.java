package ch.jalu.configme.neo.samples;

import ch.jalu.configme.neo.SettingsHolder;
import ch.jalu.configme.neo.properties.Property;

import static ch.jalu.configme.neo.properties.PropertyInitializer.newProperty;

public class ClassWithPrivatePropertyField implements SettingsHolder {

    private static final Property<Integer> PRIVATE_INT_PROPERTY = newProperty("int", 4);
}
