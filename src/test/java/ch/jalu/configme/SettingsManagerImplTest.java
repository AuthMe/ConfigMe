package ch.jalu.configme;

import ch.jalu.configme.beanmapper.worldgroup.GameMode;
import ch.jalu.configme.beanmapper.worldgroup.Group;
import ch.jalu.configme.beanmapper.worldgroup.WorldGroupConfig;
import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.migration.MigrationService;
import ch.jalu.configme.properties.BeanProperty;
import ch.jalu.configme.properties.OptionalProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.resource.PropertyReader;
import ch.jalu.configme.resource.PropertyResource;
import ch.jalu.configme.resource.YamlFileResource;
import ch.jalu.configme.samples.TestConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ch.jalu.configme.TestUtils.copyFileFromResources;
import static ch.jalu.configme.TestUtils.verifyException;
import static ch.jalu.configme.configurationdata.ConfigurationDataBuilder.createConfiguration;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

/**
 * Test for {@link SettingsManagerImpl}.
 */
@ExtendWith(MockitoExtension.class)
class SettingsManagerImplTest {

    private final ConfigurationData configurationData = createConfiguration(Arrays.asList(
        newProperty("demo.prop", 3),
        newProperty("demo.prop2", "test"),
        newProperty("demo.prop3", 0)));

    @Mock
    private PropertyResource resource;

    @Mock
    private PropertyReader reader;

    @Mock
    private MigrationService migrationService;

    @TempDir
    public Path temporaryFolder;

    @Test
    void shouldCheckMigrationServiceOnStartup() {
        // given
        given(resource.createReader()).willReturn(reader);
        given(migrationService.checkAndMigrate(reader, configurationData)).willReturn(false);

        // when
        new SettingsManagerImpl(resource, configurationData, migrationService);

        // then
        verifyWasMigrationServiceChecked();
        verify(resource, only()).createReader();
    }

    @Test
    void shouldSaveAfterPerformingMigrations() {
        // given
        given(resource.createReader()).willReturn(reader);
        given(migrationService.checkAndMigrate(reader, configurationData)).willReturn(true);

        // when
        new SettingsManagerImpl(resource, configurationData, migrationService);

        // then
        verifyWasMigrationServiceChecked();
        verify(resource).exportProperties(configurationData);
    }

    @Test
    void shouldGetProperty() {
        // given
        given(resource.createReader()).willReturn(reader);
        SettingsManager manager = createManager();
        Property<String> property = typedMock();
        String propValue = "Hello world";
        given(property.isValidValue(propValue)).willReturn(true);
        configurationData.setValue(property, propValue);

        // when
        String result = manager.getProperty(property);

        // then
        assertThat(result, equalTo(propValue));
    }

    @Test
    void shouldSetProperty() {
        // given
        given(resource.createReader()).willReturn(reader);
        SettingsManager manager = createManager();
        Property<String> property = typedMock();
        String value = "Hello there";
        given(property.isValidValue(value)).willReturn(true);

        // when
        manager.setProperty(property, value);

        // then
        assertThat(configurationData.getValue(property), equalTo(value));
        verify(property).isValidValue(value);
    }

    @Test
    void shouldPerformReload() {
        // given
        ConfigurationData configurationData = mock(ConfigurationData.class);
        SettingsManager manager = new SettingsManagerImpl(resource, configurationData, migrationService);
        reset(resource, configurationData, migrationService);
        given(resource.createReader()).willReturn(reader);
        given(migrationService.checkAndMigrate(reader, configurationData)).willReturn(false);

        // when
        manager.reload();

        // then
        verify(resource).createReader();
        verify(configurationData).initializeValues(reader);
        verify(migrationService).checkAndMigrate(reader, configurationData);
    }

    @Test
    void shouldHandleNullMigrationService() {
        // given
        given(resource.createReader()).willReturn(reader);
        List<Property<?>> properties = configurationData.getProperties();
        ConfigurationData configurationData = createConfiguration(properties);

        // when
        SettingsManagerImpl manager = new SettingsManagerImpl(resource, configurationData, null);

        // then
        assertThat(manager.getConfigurationData(), sameInstance(configurationData));
    }

    @Test
    void shouldAllowToSetBeanPropertyValue() {
        // given
        BeanProperty<WorldGroupConfig> worldGroups = new BeanProperty<>(WorldGroupConfig.class, "worlds", new WorldGroupConfig());
        PropertyResource resource = new YamlFileResource(copyFileFromResources("/beanmapper/worlds.yml", temporaryFolder));
        ConfigurationData configurationData = createConfiguration(Collections.singletonList(worldGroups));
        SettingsManager manager = new SettingsManagerImpl(resource, configurationData, null);
        WorldGroupConfig worldGroupConfig = createTestWorldConfig();

        // when
        manager.setProperty(worldGroups, worldGroupConfig);

        // then
        assertThat(manager.getProperty(worldGroups), equalTo(worldGroupConfig));
    }

    @Test
    void shouldProperlySaveBeanPropertyValueSetAfterwards() {
        // given
        BeanProperty<WorldGroupConfig> worldGroups = new BeanProperty<>(WorldGroupConfig.class, "groups", new WorldGroupConfig());
        Path file = copyFileFromResources("/beanmapper/worlds.yml", temporaryFolder);
        ConfigurationData configurationData = createConfiguration(Collections.singletonList(worldGroups));
        SettingsManager manager = new SettingsManagerImpl(
            new YamlFileResource(file), configurationData, null);
        WorldGroupConfig worldGroupConfig = createTestWorldConfig();
        manager.setProperty(worldGroups, worldGroupConfig);

        // when
        manager.save();
        manager = new SettingsManagerImpl(new YamlFileResource(file), configurationData, null);

        // then
        WorldGroupConfig loadedValue = manager.getProperty(worldGroups);
        assertThat(loadedValue.getGroups().keySet(), contains("easy", "hard"));
        assertThat(loadedValue.getGroups().get("easy").getDefaultGamemode(), equalTo(GameMode.CREATIVE));
        assertThat(loadedValue.getGroups().get("easy").getWorlds(), contains("easy1", "easy2"));
    }

    @Test
    void shouldSetOptionalPropertyCorrectly() {
        // given
        Path file = copyFileFromResources("/config-sample.yml", temporaryFolder);
        PropertyResource resource = new YamlFileResource(file);
        SettingsManager settingsManager =
            new SettingsManagerImpl(resource, createConfiguration(TestConfiguration.class), null);
        OptionalProperty<Integer> intOptional = new OptionalProperty<>(newProperty("version", 65));

        // when
        settingsManager.setProperty(intOptional, Optional.empty());

        // then
        assertThat(settingsManager.getProperty(intOptional), equalTo(Optional.empty()));

        // when (2)
        settingsManager.setProperty(intOptional, Optional.of(43));

        // then (2)
        assertThat(settingsManager.getProperty(intOptional), equalTo(Optional.of(43)));
    }

    @Test
    void shouldThrowExceptionForInvalidValue() {
        // given
        given(resource.createReader()).willReturn(reader);
        Property<String> property = typedMock();
        String value = "test";
        given(property.isValidValue(value)).willReturn(false);

        // when / then
        verifyException(() -> createManager().setProperty(property, value),
            ConfigMeException.class,
            "Invalid value for property '" + property + "'");
        // Note: the exception is actually thrown by ConfigurationDataImpl but with this test we ensure
        // that exceptions thrown by configuration data are passed up the calling hierarchy
    }

    private void verifyWasMigrationServiceChecked() {
        verify(migrationService, only()).checkAndMigrate(reader, configurationData);
    }

    private SettingsManager createManager() {
        given(migrationService.checkAndMigrate(reader, configurationData)).willReturn(false);
        SettingsManager manager = new SettingsManagerImpl(resource, configurationData, migrationService);
        reset(migrationService);
        return manager;
    }

    private static WorldGroupConfig createTestWorldConfig() {
        Group easyGroup = new Group();
        easyGroup.setDefaultGamemode(GameMode.CREATIVE);
        easyGroup.setWorlds(Arrays.asList("easy1", "easy2"));
        Group hardGroup = new Group();
        hardGroup.setDefaultGamemode(GameMode.SURVIVAL);
        hardGroup.setWorlds(Arrays.asList("hard1", "hard2"));

        Map<String, Group> groups = new LinkedHashMap<>();
        groups.put("easy", easyGroup);
        groups.put("hard", hardGroup);
        WorldGroupConfig worldGroupConfig = new WorldGroupConfig();
        worldGroupConfig.setGroups(groups);
        return worldGroupConfig;
    }

    @SuppressWarnings("unchecked")
    private static <T> Property<T> typedMock() {
        return mock(Property.class);
    }
}
