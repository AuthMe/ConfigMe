package ch.jalu.configme;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.migration.MigrationService;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.migration.VersionMigrationService;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.PropertyInitializer;
import ch.jalu.configme.resource.PropertyReader;
import ch.jalu.configme.resource.PropertyResource;
import ch.jalu.configme.resource.YamlFileResource;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import ch.jalu.configme.samples.TestConfiguration;
import ch.jalu.configme.samples.TestVersionConfiguration;
import ch.jalu.configme.utils.MigrationUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static ch.jalu.configme.TestUtils.copyFileFromResources;
import static ch.jalu.configme.TestUtils.isValidValueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    void shouldCreateSettingsManagerFromFileObject() throws URISyntaxException {
        // given
        URL fileUrl = getClass().getClassLoader().getResource("config-sample.yml");
        File file = new File(fileUrl.toURI());

        // when
        SettingsManager settingsManager = SettingsManagerBuilder.withYamlFile(file)
            .configurationData(TestConfiguration.class)
            .create();

        // then
        assertThat(settingsManager.getProperty(TestConfiguration.SYSTEM_NAME), equalTo("Custom sys name"));
    }

    @Test
    void shouldCreateSettingsManagerFromFileObject2() throws URISyntaxException {
        // given
        URL fileUrl = getClass().getClassLoader().getResource("config-sample.yml");
        File file = new File(fileUrl.toURI());
        YamlFileResourceOptions options = YamlFileResourceOptions.builder().build();

        // when
        SettingsManager settingsManager = SettingsManagerBuilder.withYamlFile(file, options)
            .configurationData(TestConfiguration.class)
            .create();

        // then
        assertThat(settingsManager.getProperty(TestConfiguration.SYSTEM_NAME), equalTo("Custom sys name"));
    }

    /**
     * This method tests the {@link VersionMigrationService} class.
     * @author gamerover98
     */
    @Test
    void shouldMigrateFromVersion1ToVersion2() throws IOException {
        // given
        Path file = copyFileFromResources("/versions/config-old-version-sample.yml", temporaryFolder);
        long initialFileSize = Files.size(file);

        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(TestVersionConfiguration.class);
        MigrationService migrationService = getVersionMigrationService(TestVersionConfiguration.VERSION_NUMBER);

        // when
        SettingsManagerImpl manager = (SettingsManagerImpl) SettingsManagerBuilder.withYamlFile(file)
            .configurationData(configurationData)
            .migrationService(migrationService)
            .create();

        // then
        // check that file was written to (migration services notices incomplete file)
        assertThat(Files.size(file), greaterThan(initialFileSize));

        PropertyReader reader = manager.getPropertyResource().createReader();

        assertThat(TestVersionConfiguration.VERSION_NUMBER.determineValue(reader).getValue(), equalTo(2));
        assertThat(TestVersionConfiguration.SHELF_POPATOES.determineValue(reader).getValue(), equalTo(4));
        assertThat(TestVersionConfiguration.SHELF_TOMATOES.determineValue(reader).getValue(), equalTo(10));
    }

    /**
     * This method tests the {@link VersionMigrationService} class.
     * @author gamerover98
     */
    @Test
    void shouldNotMigrateAndKeepConfigValues() throws IOException {
        // given
        Path file = copyFileFromResources("/versions/config-current-version-sample.yml", temporaryFolder);
        long initialFileSize = Files.size(file);

        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(TestVersionConfiguration.class);
        MigrationService migrationService = getVersionMigrationService(TestVersionConfiguration.VERSION_NUMBER);

        // when
        SettingsManagerImpl manager = (SettingsManagerImpl) SettingsManagerBuilder.withYamlFile(file)
            .configurationData(configurationData)
            .migrationService(migrationService)
            .create();

        // then
        // the file won't change.
        assertThat(Files.size(file), equalTo(initialFileSize));

        PropertyReader reader = manager.getPropertyResource().createReader();

        assertThat(TestVersionConfiguration.VERSION_NUMBER.determineValue(reader).getValue(), equalTo(2));
        assertThat(TestVersionConfiguration.SHELF_POPATOES.determineValue(reader).getValue(), equalTo(4));
        assertThat(TestVersionConfiguration.SHELF_TOMATOES.determineValue(reader).getValue(), equalTo(10));
    }

    /**
     * This test reads the "config-next-version-sample.yml" file containing a "version: 3" property.
     * The {@link VersionMigrationService} should set the version to 2, leaving the rest to
     * the {@link SettingsHolder}.
     *
     * Author: gamerover98
     */
    @Test
    void shouldNotMigrateFromNextVersionButResetConfig() {
        // given
        Path file = copyFileFromResources("/versions/config-next-version-sample.yml", temporaryFolder);

        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(TestVersionConfiguration.class);
        MigrationService migrationService = getVersionMigrationService(TestVersionConfiguration.VERSION_NUMBER);

        // when
        SettingsManagerImpl manager = (SettingsManagerImpl) SettingsManagerBuilder.withYamlFile(file)
            .configurationData(configurationData)
            .migrationService(migrationService)
            .create();

        // then
        // the file is reset to the current version.
        PropertyReader reader = manager.getPropertyResource().createReader();

        // the version has been changed from 3 to 2, and the file has been restored to the current SettingsHolder version.
        assertThat(TestVersionConfiguration.VERSION_NUMBER.determineValue(reader).getValue(), equalTo(2));
        // The values of shelf.potatoes and shelf.tomatoes remain the same as their YAML route hasn't changed.
        assertThat(TestVersionConfiguration.SHELF_POPATOES.determineValue(reader).getValue(), equalTo(100));
        assertThat(TestVersionConfiguration.SHELF_TOMATOES.determineValue(reader).getValue(), equalTo(200));
    }

    /**
     * This test reads the "config-invalid-version-sample.yml" file which contains a "version: -12345" property.
     * The {@link VersionMigrationService} should attempt to migrate, but no tasks are executed.
     * Then, the version property should be reset to the default version number, and the rest is handled by
     * the {@link SettingsHolder}.
     *
     * Author: gamerover98
     */
    @Test
    void shouldNotMigrateFromOldVersionButResetConfig() {
        // given
        Path file = copyFileFromResources("/versions/config-invalid-version-sample.yml", temporaryFolder);

        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(TestVersionConfiguration.class);
        MigrationService migrationService = getVersionMigrationService(TestVersionConfiguration.VERSION_NUMBER);

        // when
        SettingsManagerImpl manager = (SettingsManagerImpl) SettingsManagerBuilder.withYamlFile(file)
            .configurationData(configurationData)
            .migrationService(migrationService)
            .create();

        // then
        // the file is reset to the current version.
        PropertyReader reader = manager.getPropertyResource().createReader();

        // the version has been changed from 3 to 2, and the file has been restored to the current SettingsHolder version.
        assertThat(TestVersionConfiguration.VERSION_NUMBER.determineValue(reader).getValue(), equalTo(2));
        // the values of shelf.potatoes and shelf.tomatoes have been reset as they didn't match the old YAML routes
        assertThat(TestVersionConfiguration.SHELF_POPATOES.determineValue(reader).getValue(), equalTo(40));
        assertThat(TestVersionConfiguration.SHELF_TOMATOES.determineValue(reader).getValue(), equalTo(100));
    }

    /**
     * @return the not-null instance of a {@link VersionMigrationService} for test purposes.
     * @author gamerover98
     */
    @NotNull
    private static VersionMigrationService getVersionMigrationService(@NotNull Property<Integer> verionProperty) {
        return new VersionMigrationService(
            verionProperty,
            Collections.singletonList(
                new VersionMigrationService.Migration() {

                    @Override
                    public int fromVersion() {
                        return 1;
                    }

                    @Override
                    public int toVersion() {
                        return 2;
                    }

                    @Override
                    public void migrate(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
                        Property<Integer> oldPotatoesProperty = PropertyInitializer.newProperty("potatoes", 4);
                        Property<Integer> oldTomatoesProperty = PropertyInitializer.newProperty("tomatoes", 10);

                        Property<Integer> newPotatoesProperty = PropertyInitializer.newProperty("shelf.potatoes", 4);
                        Property<Integer> newTomatoesProperty = PropertyInitializer.newProperty("shelf.tomatoes", 10);

                        MigrationUtils.moveProperty(oldPotatoesProperty, newPotatoesProperty, reader, configurationData);
                        MigrationUtils.moveProperty(oldTomatoesProperty, newTomatoesProperty, reader, configurationData);
                    }
                }
            ));
    }
}
