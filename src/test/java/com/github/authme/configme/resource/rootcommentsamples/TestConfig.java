package com.github.authme.configme.resource.rootcommentsamples;

import com.github.authme.configme.Comment;
import com.github.authme.configme.SectionComments;
import com.github.authme.configme.SettingsHolder;
import com.github.authme.configme.properties.Property;

import java.util.HashMap;
import java.util.Map;

import static com.github.authme.configme.properties.PropertyInitializer.newProperty;

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
