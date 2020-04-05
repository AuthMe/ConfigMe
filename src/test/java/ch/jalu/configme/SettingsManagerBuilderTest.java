package ch.jalu.configme;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.migration.MigrationService;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.resource.PropertyReader;
import ch.jalu.configme.resource.PropertyResource;
import ch.jalu.configme.resource.YamlFileResource;
import ch.jalu.configme.samples.TestConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static ch.jalu.configme.TestUtils.copyFileFromResources;
import static ch.jalu.configme.TestUtils.isValidValueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Test for {@link SettingsManagerBuilder}.
 */
class SettingsManagerBuilderTest {

    @TempDir
    public Path temporaryFolder;

    @Test
    void shouldCreateSettingsManager() {
        // given
        PropertyResource resource = mock(PropertyResource.class);
        PropertyReader reader = mock(PropertyReader.class);
        given(resource.createReader()).willReturn(reader);
        MigrationService migrationService = mock(MigrationService.class);
        ConfigurationData configurationData = mock(ConfigurationData.class);

        // when
        SettingsManagerImpl settingsManager = (SettingsManagerImpl) SettingsManagerBuilder.withResource(resource)
            .configurationData(configurationData)
            .migrationService(migrationService)
            .create();

        // then
        assertThat(settingsManager.getPropertyResource(), equalTo(resource));
        assertThat(settingsManager.getConfigurationData(), equalTo(configurationData));
        assertThat(settingsManager.getMigrationService(), equalTo(migrationService));
        verify(configurationData).initializeValues(reader);
        verify(migrationService).checkAndMigrate(reader, configurationData);
    }

    @Test
    void shouldCreateSettingsManagerWithYamlFileAndDefaultMigrationService() {
        // given
        Path configFile = temporaryFolder.resolve("config.yml");
        ConfigurationData configurationData = mock(ConfigurationData.class);

        // when
        SettingsManagerImpl settingsManager = (SettingsManagerImpl) SettingsManagerBuilder.withYamlFile(configFile)
            .configurationData(configurationData)
            .useDefaultMigrationService()
            .create();

        // then
        assertThat(Files.exists(configFile), equalTo(true));
        assertThat(settingsManager.getPropertyResource(), instanceOf(YamlFileResource.class));
        assertThat(settingsManager.getConfigurationData(), equalTo(configurationData));
        assertThat(settingsManager.getMigrationService().getClass(), equalTo(PlainMigrationService.class));
        verify(configurationData).initializeValues(any(PropertyReader.class));
    }

    @Test
    void shouldSupportNullAsMigrationServiceAndCreateConfigurationDataFromClasses() {
        // given
        PropertyResource resource = mock(PropertyResource.class);
        given(resource.createReader()).willReturn(mock(PropertyReader.class));

        // when
        SettingsManagerImpl settingsManager = (SettingsManagerImpl) SettingsManagerBuilder.withResource(resource)
            .configurationData(TestConfiguration.class)
            .create();

        // then
        assertThat(settingsManager.getPropertyResource(), equalTo(resource));
        assertThat(settingsManager.getConfigurationData().getProperties(), hasSize(11));
        assertThat(settingsManager.getMigrationService(), nullValue());
    }

    /**
     * Similar to {@link #shouldCreateSettingsManagerWithYamlFileAndDefaultMigrationService}
     * but also checks the writing and everything.
     */
    @Test
    void shouldCreateManagerWithYamlShorthandAndMigrateConfigFile() throws IOException {
        // given
        Path file = copyFileFromResources("/config-incomplete-sample.yml", temporaryFolder);
        long initialFileSize = Files.size(file);

        // when
        SettingsManagerImpl manager = (SettingsManagerImpl) SettingsManagerBuilder.withYamlFile(file)
            .configurationData(TestConfiguration.class)
            .useDefaultMigrationService()
            .create();

        // then
        // check that file was written to (migration services notices incomplete file)
        assertThat(Files.size(file), greaterThan(initialFileSize));

        PropertyReader reader = manager.getPropertyResource().createReader();
        // Value which already existed in file
        assertThat(TestConfiguration.DURATION_IN_SECONDS.determineValue(reader).getValue(), equalTo(22));
        // Value which has newly been written to -> check with Property#determineValue to make sure it was saved in the config file
        assertThat(TestConfiguration.RATIO_ORDER.determineValue(reader), isValidValueOf(TestConfiguration.RATIO_ORDER.getDefaultValue()));
    }
}
