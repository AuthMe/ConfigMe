package ch.jalu.configme.configurationdata.samples;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SectionComments;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;

import java.util.HashMap;
import java.util.Map;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

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

    @SectionComments
    public static Map<String, String[]> getSectionComments() {
        Map<String, String[]> comments = new HashMap<>(3);
        comments.put("additional", new String[]{"Section comment for 'additional'"});
        comments.put("bogus", new String[]{"This section does not exist anywhere"});
        comments.put("other.section", null);
        return comments;
    }

    @SectionComments
    public static Map<String, String[]> buildOtherComments() {
        // We can have multiple @SectionComments on a class
        // The methods can return null if this is required for some reason
        return null;
    }

}
