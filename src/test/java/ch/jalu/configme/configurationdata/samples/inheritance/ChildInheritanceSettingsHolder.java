package ch.jalu.configme.configurationdata.samples.inheritance;

import ch.jalu.configme.properties.Property;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

/**
 * Sample settings holder class with inheritance (don't do this).
 */
public class ChildInheritanceSettingsHolder extends MiddleInheritanceSettingsHolder {

    public static final Property<Double> CHILD_DOUBLE = newProperty("child.double", 5.3);

    public static final Property<String> SAMPLE_SUBTITLE = newProperty("sample.subtitle", "");

}
