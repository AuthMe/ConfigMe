package ch.jalu.configme.resource.rootcommentsamples;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public final class TestConfig implements SettingsHolder {

    @Comment("Integer property")
    public static final Property<Integer> INT_PROPERTY = newProperty("some.test", 4);

    public static final Property<String> STRING_PROPERTY = newProperty("some.other.property", "hello");

    private TestConfig() {
    }

    @Override
    public void registerComments(CommentsConfiguration conf) {
        conf.setComment("", "Root comment");
        conf.setComment("some", "'some' Section", "Explanation for 'some'");
        conf.setComment("some.other", "Other header");
    }
}
