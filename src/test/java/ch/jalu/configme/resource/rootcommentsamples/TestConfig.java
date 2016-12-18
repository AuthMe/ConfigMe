package ch.jalu.configme.resource.rootcommentsamples;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SectionComments;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;

import java.util.HashMap;
import java.util.Map;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public final class TestConfig implements SettingsHolder {

    @Comment("Integer property")
    public static final Property<Integer> INT_PROPERTY = newProperty("some.test", 4);

    public static final Property<String> STRING_PROPERTY = newProperty("some.other.property", "hello");

    private TestConfig() {
    }

    @SectionComments
    public static Map<String, String[]> buildSectionComments() {
        Map<String, String[]> comments = new HashMap<>();
        comments.put("", new String[]{"Root comment"});
        comments.put("some", new String[]{"'some' Section", "Explanation for 'some'"});
        comments.put("some.other", new String[]{"Other header"});
        return comments;
    }
}
