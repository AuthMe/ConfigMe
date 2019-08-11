package ch.jalu.configme.configurationdata.samples.inheritance;

import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class MiddleInheritanceSettingsHolder extends TopInheritanceSettingsHolder {

    public static final Property<Integer> MIDDLE_VERSION = newProperty("middle.version", 5);

    public static final Property<String> SAMPLE_NAME = newProperty("sample.name", "Sample");

    @Override
    public void registerComments(CommentsConfiguration conf) {
        conf.setComment("middle", "Comes from the holder in the middle");
    }
}
