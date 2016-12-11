package com.github.authme.configme;

import com.github.authme.configme.beanmapper.worldgroup.GameMode;
import com.github.authme.configme.beanmapper.worldgroup.Group;
import com.github.authme.configme.beanmapper.worldgroup.WorldGroupConfig;
import com.github.authme.configme.configurationdata.ConfigurationData;
import com.github.authme.configme.migration.MigrationService;
import com.github.authme.configme.properties.BeanProperty;
import com.github.authme.configme.properties.Property;
import com.github.authme.configme.resource.PropertyResource;
import com.github.authme.configme.resource.YamlFileResource;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.github.authme.configme.TestUtils.containsAll;
import static com.github.authme.configme.properties.PropertyInitializer.newProperty;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Test for {@link SettingsManager}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SettingsManagerTest {

    private final ConfigurationData configurationData = new ConfigurationData(Arrays.asList(
        newProperty("demo.prop", 3), newProperty("demo.prop2", "test"), newProperty("demo.prop3", 0)));

    @Mock
    private PropertyResource resource;

    @Mock
    private MigrationService migrationService;

    @Captor
    private ArgumentCaptor<List<Property<?>>> knownPropertiesCaptor;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldCheckMigrationServiceOnStartup() {
        // given
        given(migrationService.checkAndMigrate(eq(resource), anyList())).willReturn(false);

        // when
        new SettingsManager(resource, migrationService, configurationData);

        // then
        verifyWasMigrationServiceChecked();
        verifyZeroInteractions(resource);
    }

    @Test
    public void shouldSaveAfterPerformingMigrations() {
        // given
        given(migrationService.checkAndMigrate(eq(resource), anyList())).willReturn(true);

        // when
        new SettingsManager(resource, migrationService, configurationData);

        // then
        verifyWasMigrationServiceChecked();
        verify(resource).exportProperties(configurationData);
    }

    @Test
    public void shouldGetProperty() {
        // given
        SettingsManager manager = createManager();
        Property<String> property = typedMock();
        String propValue = "Hello world";
        given(property.getValue(resource)).willReturn(propValue);

        // when
        String result = manager.getProperty(property);

        // then
        verify(property).getValue(resource);
        assertThat(result, equalTo(propValue));
    }

    @Test
    public void shouldSetProperty() {
        // given
        SettingsManager manager = createManager();
        Property<String> property = typedMock();
        String propertyPath = "property.path.test";
        given(property.getPath()).willReturn(propertyPath);
        String value = "Hello there";

        // when
        manager.setProperty(property, value);

        // then
        verify(resource).setValue(propertyPath, value);
    }

    @Test
    public void shouldPerformReload() {
        // given
        SettingsManager manager = createManager();
        given(migrationService.checkAndMigrate(eq(resource), anyList())).willReturn(false);

        // when
        manager.reload();

        // then
        verify(resource).reload();
        verifyWasMigrationServiceChecked();
    }

    @Test
    public void shouldHandleNullMigrationService() {
        // given
        List<Property<?>> properties = configurationData.getProperties();

        // when
        SettingsManager manager = SettingsManager.createWithProperties(resource, null, properties);

        // then
        assertThat(manager, not(nullValue()));
        assertThat(manager.configurationData.getProperties(), hasSize(configurationData.getProperties().size()));
    }

    @Test
    public void shouldAllowToSetBeanPropertyValue() {
        // given
        BeanProperty<WorldGroupConfig> worldGroups = new BeanProperty<>(WorldGroupConfig.class, "worlds", new WorldGroupConfig());
        PropertyResource resource = new YamlFileResource(copyFromResources("/beanmapper/worlds.yml"));
        SettingsManager manager = SettingsManager.createWithProperties(resource, null, Collections.singletonList(worldGroups));
        WorldGroupConfig worldGroupConfig = createTestWorldConfig();

        // when
        manager.setProperty(worldGroups, worldGroupConfig);

        // then
        assertThat(manager.getProperty(worldGroups), equalTo(worldGroupConfig));
    }

    @Test
    public void shouldProperlySaveBeanPropertyValueSetAfterwards() {
        // given
        BeanProperty<WorldGroupConfig> worldGroups = new BeanProperty<>(WorldGroupConfig.class, "groups", new WorldGroupConfig());
        File file = copyFromResources("/beanmapper/worlds.yml");
        SettingsManager manager =
            SettingsManager.createWithProperties(new YamlFileResource(file), null, Collections.singletonList(worldGroups));
        WorldGroupConfig worldGroupConfig = createTestWorldConfig();
        manager.setProperty(worldGroups, worldGroupConfig);

        // when
        manager.save();
        manager = SettingsManager.createWithProperties(new YamlFileResource(file), null, Collections.singletonList(worldGroups));

        // then
        WorldGroupConfig loadedValue = manager.getProperty(worldGroups);
        assertThat(loadedValue.getGroups().keySet(), contains("easy", "hard"));
        assertThat(loadedValue.getGroups().get("easy").getDefaultGamemode(), equalTo(GameMode.CREATIVE));
        assertThat(loadedValue.getGroups().get("easy").getWorlds(), contains("easy1", "easy2"));
    }

    private void verifyWasMigrationServiceChecked() {
        verify(migrationService, only()).checkAndMigrate(eq(resource), knownPropertiesCaptor.capture());
        assertThat(knownPropertiesCaptor.getValue(), containsAll(configurationData.getProperties()));
    }

    private SettingsManager createManager() {
        given(migrationService.checkAndMigrate(resource, configurationData.getProperties())).willReturn(false);
        SettingsManager manager = new SettingsManager(resource, migrationService, configurationData);
        reset(migrationService);
        return manager;
    }

    private File copyFromResources(String path) {
        Path source = TestUtils.getJarPath(path);
        File destination;
        try {
            destination = temporaryFolder.newFile();
            Files.copy(source, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Could not copy file from JAR", e);
        }
        return destination;
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
