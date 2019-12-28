package ch.jalu.configme.samples.settingsholders;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.DoubleProperty;
import ch.jalu.configme.properties.Property;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class SettingsHolderWithInvalidConstants implements SettingsHolder {

    public /*static*/ final DoubleProperty DBL_PROP = new DoubleProperty("com.test", -44);

    public static final Property<String> STR_PROP = newProperty("com.foo", "");

    public static /*final*/ Property<List<String>> STRLIST = newListProperty("com.bar");

    /*public*/ static final Property<TimeUnit> TIME_UNIT = newProperty(TimeUnit.class, "com.timeUnit", TimeUnit.SECONDS);

    private SettingsHolderWithInvalidConstants() {
    }

    // used for test ensuring classes only have a sole no-args private constructor
    private SettingsHolderWithInvalidConstants(boolean other) {
    }

    public static class Child extends SettingsHolderWithInvalidConstants {

        private final Property<String> strProp = null;

    }
}
