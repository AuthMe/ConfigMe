package ch.jalu.configme.samples;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class ClassWithPrivatePropertyField implements SettingsHolder {

    private static final Property<Integer> PRIVATE_INT_PROPERTY = newProperty("int", 4);
}
