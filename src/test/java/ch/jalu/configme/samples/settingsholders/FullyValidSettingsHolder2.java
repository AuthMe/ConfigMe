package ch.jalu.configme.samples.settingsholders;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.ListProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.StringListProperty;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public final class FullyValidSettingsHolder2 implements SettingsHolder {

    @Comment({"Double prop", "Double propppp"})
    public static final Property<Double> DOUBLE_PROPERTY =
        newProperty("all.double", 3.0);

    @Comment("The list goes skrrrah")
    public static final ListProperty<String> LIST_PROPERTY =
        new StringListProperty("all.list", "The", "default", "elements");

    private FullyValidSettingsHolder2() {
    }
}
