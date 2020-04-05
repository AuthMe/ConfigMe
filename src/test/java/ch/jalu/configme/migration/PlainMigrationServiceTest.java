package ch.jalu.configme.migration;

import ch.jalu.configme.TestUtils;
import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.properties.IntegerProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.resource.PropertyReader;
import ch.jalu.configme.resource.YamlFileReader;
import ch.jalu.configme.samples.TestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;

import static ch.jalu.configme.configurationdata.ConfigurationDataBuilder.createConfiguration;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Test for {@link PlainMigrationService}.
 */
@ExtendWith(MockitoExtension.class)
class PlainMigrationServiceTest {

    private static final String COMPLETE_CONFIG = "/config-sample.yml";
    private static final String INCOMPLETE_CONFIG = "/config-incomplete-sample.yml";

    private ConfigurationData configurationData;

    @Spy
    private PlainMigrationService service;

    @TempDir
    public Path temporaryFolder;

    @BeforeEach
    void setUpConfigurationData() {
        configurationData = createConfiguration(TestConfiguration.class);
    }

    @Test
    void shouldReturnNoSaveNecessaryForAllPropertiesPresent() {
        // given
        PropertyReader reader = createReaderSpy(COMPLETE_CONFIG);
        configurationData.initializeValues(reader);

        // when
        boolean result = service.checkAndMigrate(reader, configurationData);

        // then
        assertThat(result, equalTo(false));
        verify(service).performMigrations(reader, configurationData);
    }

    @Test
    void shouldReturnTrueForMissingProperty() {
        // given
        PropertyReader reader = createReaderSpy(INCOMPLETE_CONFIG);

        // when
        boolean result = service.checkAndMigrate(reader, configurationData);

        // then
        assertThat(result, equalTo(true));
        // Verify that performMigrations was called; it should be called before our generic property check
        verify(service).performMigrations(reader, configurationData);
    }

    @Test
    void shouldPassResourceToExtendedMethod() {
        // given
        PropertyReader reader = createReaderSpy(COMPLETE_CONFIG);
        PlainMigrationServiceTestExtension service = Mockito.spy(new PlainMigrationServiceTestExtension());

        // when
        boolean result = service.checkAndMigrate(reader, configurationData);

        // then
        assertThat(result, equalTo(true));
        verify(service).performMigrations(reader, configurationData);
        verify(reader).contains("old.property");
    }

    @Test
    void shouldResetNegativeIntegerProperties() {
        // given
        PropertyReader reader = createReaderSpy(COMPLETE_CONFIG);
        PlainMigrationServiceTestExtension service = new PlainMigrationServiceTestExtension();

        // when
        boolean result = service.checkAndMigrate(reader, configurationData);

        // then
        assertThat(result, equalTo(true));
        assertThat(configurationData.getValue(TestConfiguration.DURATION_IN_SECONDS), equalTo(0));
    }

    @Test
    void shouldMoveProperty() {
        // given
        PropertyReader reader = createReaderSpy(INCOMPLETE_CONFIG);
        ConfigurationData configurationData = mock(ConfigurationData.class);

        // when
        boolean result = PlainMigrationService.moveProperty(
            TestConfiguration.DURATION_IN_SECONDS, TestConfiguration.VERSION_NUMBER, reader, configurationData);

        // then
        assertThat(result, equalTo(true));
        verify(configurationData).setValue(TestConfiguration.VERSION_NUMBER, 22);
    }

    @Test
    void shouldNotMovePropertyIfAlreadyExists() {
        // given
        PropertyReader reader = createReaderSpy(COMPLETE_CONFIG);
        ConfigurationData configurationData = mock(ConfigurationData.class);

        // when
        boolean result = PlainMigrationService.moveProperty(
            TestConfiguration.DURATION_IN_SECONDS, TestConfiguration.VERSION_NUMBER, reader, configurationData);

        // then
        assertThat(result, equalTo(true)); // still true because value is present at path
        verifyNoInteractions(configurationData);
    }

    @Test
    void shouldReturnFalseIfOldPathDoesNotExist() {
        // given
        PropertyReader reader = createReaderSpy(INCOMPLETE_CONFIG);
        ConfigurationData configurationData = mock(ConfigurationData.class);
        Property<Integer> oldProperty = new IntegerProperty("path.does.not.exist", 3);

        // when
        boolean result = PlainMigrationService.moveProperty(
            oldProperty, TestConfiguration.VERSION_NUMBER, reader, configurationData);

        // then
        assertThat(result, equalTo(false));
        verifyNoInteractions(configurationData);
    }

    private PropertyReader createReaderSpy(String file) {
        // It's a little difficult to set up mock behavior for all cases, so use a YML file from test/resources
        Path copy = TestUtils.copyFileFromResources(file, temporaryFolder);
        return Mockito.spy(new YamlFileReader(copy));
    }

    private static class PlainMigrationServiceTestExtension extends PlainMigrationService {

        @Override
        protected boolean performMigrations(PropertyReader reader, ConfigurationData configurationData) {
            // If contains -> return true = migration is necessary
            if (reader.contains("old.property")) {
                return MIGRATION_REQUIRED;
            }

            // Set any int property to 0 if its value is above 20
            boolean hasChange = false;
            for (Property<?> property : configurationData.getProperties()) {
                if (property instanceof IntegerProperty) {
                    Property<Integer> intProperty = (Property<Integer>) property;
                    if (intProperty.determineValue(reader).getValue() > 20) {
                        configurationData.setValue(intProperty, 0);
                        hasChange = true;
                    }
                }
            }
            return hasChange ? MIGRATION_REQUIRED : NO_MIGRATION_NEEDED;
        }
    }

}
