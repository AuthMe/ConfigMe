package com.github.authme.configme.propertymap;

import com.github.authme.configme.Comment;
import com.github.authme.configme.SettingsHolder;
import com.github.authme.configme.properties.Property;

import static com.github.authme.configme.properties.PropertyInitializer.newProperty;

/**
 * Sample properties class with some additional properties.
 */
public final class AdditionalTestConfiguration implements SettingsHolder {

    @Comment("Seconds to sleep")
    public static final Property<Integer> SLEEP =
        newProperty("additional.sleep", 10);

    @Comment("Additional name")
    public static final Property<String> NAME =
        newProperty("additional.name", "Supplement");

    @Comment("Show additional things")
    public static final Property<Boolean> SHOW_THINGS =
        newProperty("additional.enable", false);


    // Some additional fields that are either invalid properties or irrelevant
    private Property<String> privateProperty = newProperty("test", "toast");
    public static final boolean BOOL = true;
    public final Property<Integer> nonStaticProperty = newProperty("something", 123);

    private AdditionalTestConfiguration() {
    }

}
