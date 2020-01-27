package ch.jalu.configme.samples.settingsholders;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.IntegerProperty;
import ch.jalu.configme.properties.Property;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class MissingCommentsHolder implements SettingsHolder {

    public static final Property<String> STR_PROPERTY = newProperty("lorem.ipsum", "d");

    public static final IntegerProperty INT_PROPERTY = new IntegerProperty("lorem.dolor", 3);

    @Comment("A comment")
    public static final Property<Double> DBL_PROPERTY = newProperty("lorem.amet", 3.40);

    private MissingCommentsHolder() {
    }
}
