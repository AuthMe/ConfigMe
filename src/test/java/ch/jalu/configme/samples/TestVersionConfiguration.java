package ch.jalu.configme.samples;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;


/**
 * Sample properties for testing versioning purposes.
 * <p>
 *     This class represents the next version of the following config:
 *     version: 1
 *     potatoes: 4
 *     tomatoes: 10
 * </p>
 *
 * The following settings holder is different and needs a migration!
 * <p>
 *  From version 1 to 2 and:
 *  - from potatoes: 4  ----> to shelf.potatoes: 4
 *  - from tomatoes: 10 ----> to shelf.tomatoes: 10
 * </p>
 * So it keeps the same values as the old configuration!
 *
 * @author gamerover98
 */
public final class TestVersionConfiguration implements SettingsHolder {

    @Comment("The version number")
    public static final Property<Integer> VERSION_NUMBER =
        newProperty("version", 2);

    public static final Property<Integer> SHELF_POPATOES =
        newProperty("shelf.potatoes", 40);

    public static final Property<Integer> SHELF_TOMATOES =
        newProperty("shelf.tomatoes", 100);

    private TestVersionConfiguration() {
        // nothing to do.
    }
}
