package ch.jalu.configme.neo.resource.rootcommentsamples;

import ch.jalu.configme.neo.Comment;
import ch.jalu.configme.neo.SettingsHolder;
import ch.jalu.configme.neo.configurationdata.CommentsConfiguration;
import ch.jalu.configme.neo.properties.Property;

import static ch.jalu.configme.neo.properties.PropertyInitializer.newProperty;

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
