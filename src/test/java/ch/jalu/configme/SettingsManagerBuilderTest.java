package ch.jalu.configme;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.migration.MigrationService;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.resource.PropertyReader;
import ch.jalu.configme.resource.PropertyResource;
import ch.jalu.configme.resource.YamlFileResource;
import ch.jalu.configme.samples.TestConfiguration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static ch.jalu.configme.TestUtils.copyFileFromResources;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Test for {@link SettingsManagerBuilder}.
 */
public class SettingsManagerBuilderTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldCreateSettingsManager() {
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
    public void shouldCreateSettingsManagerWithYamlFileAndDefaultMigrationService() throws IOException {
        // given
        File folder = temporaryFolder.newFolder();
        File configFile = new File(folder, "config.yml");
        ConfigurationData configurationData = mock(ConfigurationData.class);

        // when
        SettingsManagerImpl settingsManager = (SettingsManagerImpl) SettingsManagerBuilder.withYamlFile(configFile)
            .configurationData(configurationData)
            .create();

        // then
        assertThat(configFile.exists(), equalTo(true));
        assertThat(settingsManager.getPropertyResource(), instanceOf(YamlFileResource.class));
        assertThat(settingsManager.getConfigurationData(), equalTo(configurationData));
        assertThat(settingsManager.getMigrationService().getClass(), equalTo(PlainMigrationService.class));
        verify(configurationData).initializeValues(any(PropertyReader.class));
    }

    @Test
    public void shouldSupportNullAsMigrationServiceAndCreateConfigurationDataFromClasses() {
        // given
        PropertyResource resource = mock(PropertyResource.class);
        given(resource.createReader()).willReturn(mock(PropertyReader.class));

        // when
        SettingsManagerImpl settingsManager = (SettingsManagerImpl) SettingsManagerBuilder.withResource(resource)
            .configurationData(TestConfiguration.class)
            .migrationService(null)
            .create();

        // then
        assertThat(settingsManager.getPropertyResource(), equalTo(resource));
        assertThat(settingsManager.getConfigurationData().getProperties(), hasSize(10));
        assertThat(settingsManager.getMigrationService(), nullValue());
    }

    /**
     * Similar to {@link #shouldCreateSettingsManagerWithYamlFileAndDefaultMigrationService}
     * but also checks the writing and everything.
     */
    @Test
    public void shouldCreateManagerWithYamlShorthandAndMigrateConfigFile() {
        // given
        File file = copyFileFromResources("/config-incomplete-sample.yml", temporaryFolder);
        long fileLength = file.length();

        // when
        SettingsManagerImpl manager = (SettingsManagerImpl) SettingsManagerBuilder.withYamlFile(file)
            .configurationData(TestConfiguration.class)
            .create();

        // then
        // check that file was written to (migration services notices incomplete file)
        assertThat(file.length(), greaterThan(fileLength));

        PropertyReader reader = manager.getPropertyResource().createReader();
        // Value which already existed in file
        assertThat(TestConfiguration.DURATION_IN_SECONDS.determineValue(reader), equalTo(22));
        // Value which has newly been written to -> check with Property#determineValue to make sure it was saved in the config file
        assertThat(TestConfiguration.RATIO_ORDER.determineValue(reader), equalTo(TestConfiguration.RATIO_ORDER.getDefaultValue()));
    }
}
