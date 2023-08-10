package ch.jalu.configme.configurationdata;

import ch.jalu.configme.TestUtils;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.convertresult.PropertyValue;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.jalu.configme.TestUtils.containsAll;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link ConfigurationDataImpl}.
 */
class ConfigurationDataImplTest {

    @Test
    void shouldAcceptListWithTypedProperty() {
        // given
        List<Property<?>> properties = Arrays.asList(
            newProperty("test", "Test"),
            newProperty("taste", "Taste"),
            newProperty("toast", "Toaster"));

        // when
        ConfigurationData configData = new ConfigurationDataImpl(properties, Collections.emptyMap());

        // then
        assertThat(configData.getProperties(), containsAll(properties));
        assertThat(configData.areAllValuesValidInResource(), equalTo(false)); // false until the values are initialized
    }

    @Test
    void shouldHaveImmutablePropertyList() {
        // given
        List<Property<?>> properties = Collections.singletonList(newProperty("test", ""));
        ConfigurationData configData = new ConfigurationDataImpl(properties, Collections.emptyMap());

        // when / then
        assertThrows(UnsupportedOperationException.class,
            () -> configData.getProperties().remove(0));
    }

    @Test
    void shouldThrowForInvalidValue() {
        // given
        List<Property<?>> properties = Arrays.asList(
            newProperty("test", "Test"),
            newProperty("taste", "Taste"),
            newProperty("toast", "Toaster"));
        ConfigurationData configData = new ConfigurationDataImpl(properties, Collections.emptyMap());

        // when
        Exception ex = assertThrows(Exception.class,
            () -> configData.setValue(properties.get(0), null));

        // then
        if (TestUtils.hasBytecodeCheckForNotNullAnnotation()) {
            assertThat(ex, instanceOf(IllegalArgumentException.class));
            assertThat(ex.getMessage(), containsString("Argument for @NotNull parameter 'value'"));
        } else {
            assertThat(ex, instanceOf(ConfigMeException.class));
            assertThat(ex.getMessage(), matchesPattern("Invalid value for property '.*?': null"));
        }
    }

    @Test
    void shouldReturnAllComments() {
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
    void shouldReturnCommentsForSections() {
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
    void shouldThrowForUnknownProperty() {
        // given
        List<Property<?>> properties = Collections.singletonList(newProperty("test", "Test"));
        ConfigurationData configurationData = new ConfigurationDataImpl(properties, Collections.emptyMap());
        Property<Integer> nonExistentProperty = newProperty("my.bogus.path", 5);

        // when
        ConfigMeException ex = assertThrows(ConfigMeException.class,
            () -> configurationData.getValue(nonExistentProperty));

        // then
        assertThat(ex.getMessage(), equalTo("No value exists for property with path 'my.bogus.path'. "
            + "This may happen if the property belongs to a SettingsHolder class which was not passed to the settings manager."));
    }

    @Test
    void shouldReturnValuesMap() {
        // given
        ConfigurationDataImpl configurationData = new ConfigurationDataImpl(Collections.emptyList(), Collections.emptyMap());

        // when
        int initialValuesSize = configurationData.getValues().size();
        configurationData.setValue(newProperty("test", "foo"), "bar");

        // then
        assertThat(initialValuesSize, equalTo(0));
        assertThat(configurationData.getValues().keySet(), contains("test"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldInitializeAllPropertiesAndSetAllValuesValidToTrue() {
        // given
        PropertyReader reader = mock(PropertyReader.class);
        Property<String> property1 = mock(Property.class);
        given(property1.determineValue(reader)).willReturn(PropertyValue.withValidValue("test"));
        given(property1.isValidValue(anyString())).willReturn(true);
        Property<Double> property2 = mock(Property.class);
        given(property2.determineValue(reader)).willReturn(PropertyValue.withValidValue(3.14159));
        given(property2.isValidValue(anyDouble())).willReturn(true);

        ConfigurationData configurationData = new ConfigurationDataImpl(Arrays.asList(property1, property2), Collections.emptyMap());

        // when
        configurationData.initializeValues(reader);

        // then
        assertThat(configurationData.areAllValuesValidInResource(), equalTo(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldInitializeAllPropertiesAndSetAllValuesValidToFalse() {
        // given
        PropertyReader reader = mock(PropertyReader.class);
        Property<String> property1 = mock(Property.class);
        given(property1.determineValue(reader)).willReturn(PropertyValue.withValidValue("test"));
        given(property1.isValidValue(anyString())).willReturn(true);
        Property<Double> property2 = mock(Property.class);
        given(property2.determineValue(reader)).willReturn(PropertyValue.withValueRequiringRewrite(3.14159));
        given(property2.isValidValue(anyDouble())).willReturn(true);

        ConfigurationData configurationData = new ConfigurationDataImpl(Arrays.asList(property1, property2), Collections.emptyMap());

        // when
        configurationData.initializeValues(reader);

        // then
        assertThat(configurationData.areAllValuesValidInResource(), equalTo(false));
    }

    private static Map<String, List<String>> createSampleCommentsMap() {
        CommentsConfiguration commentsConfiguration = new CommentsConfiguration();
        commentsConfiguration.setComment("test", "test section comment");
        commentsConfiguration.setComment("test.first", "First thing");
        commentsConfiguration.setComment("test.second", "Second thing", "Comes after first");
        return commentsConfiguration.getAllComments();
    }
}
