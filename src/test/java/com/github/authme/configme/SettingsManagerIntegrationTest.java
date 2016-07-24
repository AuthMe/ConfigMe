package com.github.authme.configme;

import com.github.authme.configme.properties.Property;
import com.github.authme.configme.propertymap.PropertyMap;
import com.github.authme.configme.resource.PropertyResource;
import com.github.authme.configme.resource.YamlFileResource;
import com.github.authme.configme.samples.TestConfiguration;
import com.github.authme.configme.samples.TestEnum;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.authme.configme.properties.PropertyInitializer.newProperty;
import static com.github.authme.configme.samples.TestSettingsMigrationServices.alwaysFulfilled;
import static com.github.authme.configme.samples.TestSettingsMigrationServices.checkAllPropertiesPresent;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Integration test for {@link SettingsManager} and {@link YamlFileResource}.
 */
public class SettingsManagerIntegrationTest {

    private static final String COMPLETE_FILE = "/config-sample.yml";
    private static final String INCOMPLETE_FILE = "/config-incomplete-sample.yml";
    private static final String DIFFICULT_FILE = "/config-difficult-values.yml";
    
    private static final PropertyMap propertyMap = TestConfiguration.generatePropertyMap();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldReadAllProperties() throws IOException {
        // given
        File config = copyFileFromResources(COMPLETE_FILE);
        PropertyResource resource = new YamlFileResource(config);
        // keep track of the initial length / last modified and make sure it's the same at the end
        // simple way to check that the file wasn't written to again
        long initialLength = config.length();
        long lastModified = config.lastModified();


        // when / then
        SettingsManager settings = new SettingsManager(propertyMap, resource, checkAllPropertiesPresent());
        Map<Property<?>, Object> expected = new HashMap<>();
        expected.put(TestConfiguration.DURATION_IN_SECONDS, 22);
        expected.put(TestConfiguration.SYSTEM_NAME, "Custom sys name");
        expected.put(TestConfiguration.RATIO_ORDER, TestEnum.FIRST);
        expected.put(TestConfiguration.RATIO_FIELDS, Arrays.asList("Australia", "Burundi", "Colombia"));
        expected.put(TestConfiguration.VERSION_NUMBER, 2492);
        expected.put(TestConfiguration.SKIP_BORING_FEATURES, false);
        expected.put(TestConfiguration.BORING_COLORS, Arrays.asList("beige", "gray"));
        expected.put(TestConfiguration.DUST_LEVEL, 2);
        expected.put(TestConfiguration.USE_COOL_FEATURES, true);
        expected.put(TestConfiguration.COOL_OPTIONS, Arrays.asList("Dinosaurs", "Explosions", "Big trucks"));

        for (Map.Entry<Property<?>, Object> entry : expected.entrySet()) {
            assertThat("Property '" + entry.getKey().getPath() + "' has expected value",
                settings.getProperty(entry.getKey()), equalTo(entry.getValue()));
        }

        assertThat(initialLength, equalTo(config.length()));
        assertThat(lastModified, equalTo(config.lastModified()));
    }

    @Test
    public void shouldWriteMissingProperties() {
        // given/when
        File file = copyFileFromResources(INCOMPLETE_FILE);
        YamlFileResource resource = new YamlFileResource(file);
        // Expectation: File is rewritten to since it does not have all configurations
        new SettingsManager(propertyMap, resource, checkAllPropertiesPresent());

        // Load the settings again -> checks that what we wrote can be loaded again
        resource.reload();

        // then
        SettingsManager settings = new SettingsManager(propertyMap, resource, checkAllPropertiesPresent());
        Map<Property<?>, Object> expected = new HashMap<>();
        expected.put(TestConfiguration.DURATION_IN_SECONDS, 22);
        expected.put(TestConfiguration.SYSTEM_NAME, "[TestDefaultValue]");
        expected.put(TestConfiguration.RATIO_ORDER, TestEnum.SECOND);
        expected.put(TestConfiguration.RATIO_FIELDS, Arrays.asList("Australia", "Burundi", "Colombia"));
        expected.put(TestConfiguration.VERSION_NUMBER, 32046);
        expected.put(TestConfiguration.SKIP_BORING_FEATURES, false);
        expected.put(TestConfiguration.BORING_COLORS, Collections.EMPTY_LIST);
        expected.put(TestConfiguration.DUST_LEVEL, -1);
        expected.put(TestConfiguration.USE_COOL_FEATURES, false);
        expected.put(TestConfiguration.COOL_OPTIONS, Arrays.asList("Dinosaurs", "Explosions", "Big trucks"));
        for (Map.Entry<Property<?>, Object> entry : expected.entrySet()) {
            assertThat("Property '" + entry.getKey().getPath() + "' has expected value",
                settings.getProperty(entry.getKey()), equalTo(entry.getValue()));
        }
    }

    /** Verifies that "difficult cases" such as apostrophes in strings etc. are handled properly. */
    @Test
    public void shouldProperlyExportAnyValues() {
        // given
        File file = copyFileFromResources(DIFFICULT_FILE);
        YamlFileResource resource = new YamlFileResource(file);

        // Additional string properties
        List<Property<String>> additionalProperties = Arrays.asList(
            newProperty("more.string1", "it's a text with some \\'apostrophes'"),
            newProperty("more.string2", "\tthis one\nhas some\nnew '' lines-test")
        );
        for (Property<?> property : additionalProperties) {
            propertyMap.put(property, new String[0]);
        }

        // when
        new SettingsManager(propertyMap, resource, checkAllPropertiesPresent());
        // reload the file as settings should have been rewritten
        resource = Mockito.spy(new YamlFileResource(file));

        // then
        SettingsManager settings = new SettingsManager(propertyMap, resource, checkAllPropertiesPresent());
        assertThat(resource.contains(TestConfiguration.DUST_LEVEL.getPath()), equalTo(true));

        Map<Property<?>, Object> expected = new HashMap<>();
        expected.put(TestConfiguration.DURATION_IN_SECONDS, 20);
        expected.put(TestConfiguration.SYSTEM_NAME, "A 'test' name");
        expected.put(TestConfiguration.RATIO_ORDER, TestEnum.FOURTH);
        expected.put(TestConfiguration.RATIO_FIELDS, Arrays.asList("Australia\\", "\tBurundi'", "Colombia?\n''"));
        expected.put(TestConfiguration.VERSION_NUMBER, -1337);
        expected.put(TestConfiguration.SKIP_BORING_FEATURES, false);
        expected.put(TestConfiguration.BORING_COLORS, Arrays.asList("it's a difficult string!", "gray\nwith new lines\n"));
        expected.put(TestConfiguration.DUST_LEVEL, -1);
        expected.put(TestConfiguration.USE_COOL_FEATURES, true);
        expected.put(TestConfiguration.COOL_OPTIONS, Collections.EMPTY_LIST);
        expected.put(additionalProperties.get(0), additionalProperties.get(0).getDefaultValue());
        expected.put(additionalProperties.get(1), additionalProperties.get(1).getDefaultValue());

        for (Map.Entry<Property<?>, Object> entry : expected.entrySet()) {
            assertThat("Property '" + entry.getKey().getPath() + "' has expected value",
                settings.getProperty(entry.getKey()), equalTo(entry.getValue()));
        }
        verify(resource, never()).exportProperties(any(PropertyMap.class));
    }

    @Test
    public void shouldReloadSettings() throws IOException {
        // given
        File file = temporaryFolder.newFile();
        YamlFileResource resource = new YamlFileResource(file);
        Files.copy(TestUtils.getJarPath(COMPLETE_FILE), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        SettingsManager settings = new SettingsManager(propertyMap, resource, alwaysFulfilled());

        // when
        assertThat(settings.getProperty(TestConfiguration.RATIO_ORDER),
            equalTo(TestConfiguration.RATIO_ORDER.getDefaultValue()));
        settings.reload();

        // then
        assertThat(settings.getProperty(TestConfiguration.RATIO_ORDER), equalTo(TestEnum.FIRST));
    }

    private File copyFileFromResources(String path) {
        try {
            Path source = TestUtils.getJarPath(path);
            File destination = temporaryFolder.newFile();
            Files.copy(source, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return destination;
        } catch (IOException e) {
            throw new IllegalStateException("Could not copy test file", e);
        }
    }
}
