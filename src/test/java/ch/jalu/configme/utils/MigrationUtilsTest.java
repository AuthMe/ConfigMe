package ch.jalu.configme.utils;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.resource.PropertyReader;
import ch.jalu.configme.samples.TestConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Test for {@link MigrationUtils}.
 */
@ExtendWith(MockitoExtension.class)
class MigrationUtilsTest {

    @Test
    void shouldMoveProperty() {
        // given
        ConfigurationData configurationData = mock(ConfigurationData.class);
        Property<Integer> oldProperty = TestConfiguration.DURATION_IN_SECONDS;
        Property<Integer> newProperty = TestConfiguration.VERSION_NUMBER;

        PropertyReader reader = mock(PropertyReader.class);
        given(reader.contains(oldProperty.getPath())).willReturn(true);
        given(reader.contains(newProperty.getPath())).willReturn(false);
        given(reader.getObject(oldProperty.getPath())).willReturn(22);

        // when
        boolean result = MigrationUtils.moveProperty(oldProperty, newProperty, reader, configurationData);

        // then
        assertThat(result, equalTo(true));
        verify(configurationData).setValue(newProperty, 22);
    }

    @Test
    void shouldNotMovePropertyIfAlreadyExists() {
        // given
        ConfigurationData configurationData = mock(ConfigurationData.class);
        Property<Integer> oldProperty = TestConfiguration.DURATION_IN_SECONDS;
        Property<Integer> newProperty = TestConfiguration.VERSION_NUMBER;

        PropertyReader reader = mock(PropertyReader.class);
        given(reader.contains(oldProperty.getPath())).willReturn(true);
        given(reader.contains(newProperty.getPath())).willReturn(true);

        // when
        boolean result = MigrationUtils.moveProperty(oldProperty, newProperty, reader, configurationData);

        // then
        assertThat(result, equalTo(true)); // still true because value is present at path
        verifyNoInteractions(configurationData);
    }

    @Test
    void shouldReturnFalseIfOldPathDoesNotExist() {
        // given
        ConfigurationData configurationData = mock(ConfigurationData.class);
        Property<Integer> oldProperty = TestConfiguration.DURATION_IN_SECONDS;
        Property<Integer> newProperty = TestConfiguration.VERSION_NUMBER;

        PropertyReader reader = mock(PropertyReader.class);
        given(reader.contains(oldProperty.getPath())).willReturn(false);

        // when
        boolean result = MigrationUtils.moveProperty(oldProperty, newProperty, reader, configurationData);

        // then
        assertThat(result, equalTo(false));
        verifyNoInteractions(configurationData);
        verify(reader, only()).contains(oldProperty.getPath());
    }
}
