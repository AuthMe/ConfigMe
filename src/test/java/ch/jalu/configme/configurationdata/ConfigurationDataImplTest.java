package ch.jalu.configme.configurationdata;

import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.Property;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.jalu.configme.TestUtils.containsAll;
import static ch.jalu.configme.TestUtils.verifyException;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link ConfigurationDataImpl}.
 */
public class ConfigurationDataImplTest {

    @Test
    public void shouldAcceptListWithTypedProperty() {
        // given
        List<Property<?>> properties = Arrays.asList(
            newProperty("test", "Test"),
            newProperty("taste", "Taste"),
            newProperty("toast", "Toaster"));

        // when
        ConfigurationData configData = new ConfigurationDataImpl(properties, Collections.emptyMap());

        // then
        assertThat(configData.getProperties(), containsAll(properties));
    }

    @Test
    public void shouldHaveImmutablePropertyList() {
        // given
        List<Property<?>> properties = Collections.singletonList(newProperty("test", ""));
        ConfigurationData configData = new ConfigurationDataImpl(properties, Collections.emptyMap());

        // when / then
        verifyException(() -> configData.getProperties().remove(0), UnsupportedOperationException.class);
    }

    @Test
    public void shouldThrowForInvalidValue() {
        // given
        List<Property<?>> properties = Arrays.asList(
            newProperty("test", "Test"),
            newProperty("taste", "Taste"),
            newProperty("toast", "Toaster"));
        ConfigurationData configData = new ConfigurationDataImpl(properties, Collections.emptyMap());

        // when / then
        verifyException(() -> configData.setValue(properties.get(0), null),
            ConfigMeException.class, "Invalid value");
    }

    @Test
    public void shouldReturnAllComments() {
        // given
        // Explicitly make it a modifiable Map so we can check that it's not afterwards
        Map<String, List<String>> comments = new HashMap<>(createSampleCommentsMap());
        ConfigurationData configurationData = new ConfigurationDataImpl(Collections.emptyList(), comments);

        // when
        Map<String, List<String>> result = configurationData.getAllComments();

        // then
        assertThat(result, equalTo(comments));
        assertThat(result.getClass().getName(), equalTo("java.util.Collections$UnmodifiableMap"));
    }

    @Test
    public void shouldReturnCommentsForSections() {
        // given
        ConfigurationData configurationData = new ConfigurationDataImpl(Collections.emptyList(), createSampleCommentsMap());

        // when
        List<String> testComments = configurationData.getCommentsForSection("test");
        List<String> secondComments = configurationData.getCommentsForSection("test.second");
        List<String> absentComments = configurationData.getCommentsForSection("test.doesNotExist");

        // then
        assertThat(testComments, contains("test section comment"));
        assertThat(secondComments, contains("Second thing", "Comes after first"));
        assertThat(absentComments, empty());
    }

    @Test
    public void shouldThrowForUnknownProperty() {
        // given
        List<Property<?>> properties = Collections.singletonList(newProperty("test", "Test"));
        ConfigurationData configurationData = new ConfigurationDataImpl(properties, Collections.emptyMap());
        Property<Integer> nonExistentProperty = newProperty("my.bogus.path", 5);

        // when / then
        verifyException(() -> configurationData.getValue(nonExistentProperty), ConfigMeException.class,
            "No value exists for property with path 'my.bogus.path'");
    }

    @Test
    public void shouldReturnValuesMap() {
        // given
        ConfigurationDataImpl configurationData = new ConfigurationDataImpl(Collections.emptyList(), Collections.emptyMap());

        // when
        int initialValuesSize = configurationData.getValues().size();
        configurationData.setValue(newProperty("test", "foo"), "bar");

        // then
        assertThat(initialValuesSize, equalTo(0));
        assertThat(configurationData.getValues().keySet(), contains("test"));
    }

    private static Map<String, List<String>> createSampleCommentsMap() {
        CommentsConfiguration commentsConfiguration = new CommentsConfiguration();
        commentsConfiguration.setComment("test", "test section comment");
        commentsConfiguration.setComment("test.first", "First thing");
        commentsConfiguration.setComment("test.second", "Second thing", "Comes after first");
        return commentsConfiguration.getAllComments();
    }
}
