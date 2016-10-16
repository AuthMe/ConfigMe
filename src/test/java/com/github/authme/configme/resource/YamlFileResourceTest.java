package com.github.authme.configme.resource;

import com.github.authme.configme.SettingsManager;
import com.github.authme.configme.exception.ConfigMeException;
import com.github.authme.configme.knownproperties.ConfigurationData;
import com.github.authme.configme.knownproperties.ConfigurationDataBuilder;
import com.github.authme.configme.properties.Property;
import com.github.authme.configme.samples.TestConfiguration;
import com.github.authme.configme.samples.TestEnum;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.github.authme.configme.TestUtils.getJarPath;
import static com.github.authme.configme.properties.PropertyInitializer.newProperty;
import static com.github.authme.configme.samples.TestSettingsMigrationServices.checkAllPropertiesPresent;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Test for {@link YamlFileResource} and {@link YamlFileReader}.
 */
public class YamlFileResourceTest {

    private static final String COMPLETE_FILE = "/config-sample.yml";
    private static final String INCOMPLETE_FILE = "/config-incomplete-sample.yml";
    private static final String DIFFICULT_FILE = "/config-difficult-values.yml";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldThrowForAbsentYamlMap() throws IOException {
        // given
        File file = temporaryFolder.newFile();
        Files.write(file.toPath(), "123".getBytes());

        // when / then
        try {
            new YamlFileResource(file);
            fail("Expected exception to be thrown");
        } catch (ConfigMeException e) {
            assertThat(e.getMessage(), containsString("Top-level is not a map"));
        }
    }

    @Test
    public void shouldWrapIOException() throws IOException {
        // given
        File folder = temporaryFolder.newFolder();
        File file = new File(folder, "test");

        // when / then
        try {
            new YamlFileResource(file);
            fail("Expected exception to be thrown");
        } catch (ConfigMeException e) {
            assertThat(e.getMessage(), containsString("Could not read file"));
        }
    }

    @Test
    public void shouldReadAllProperties() {
        // given
        File config = copyFileFromResources(COMPLETE_FILE);

        // when
        PropertyResource resource = new YamlFileResource(config);

        // then
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
                entry.getKey().getValue(resource), equalTo(entry.getValue()));
        }
    }

    @Test
    public void shouldWriteMissingProperties() {
        // given
        File file = copyFileFromResources(INCOMPLETE_FILE);
        YamlFileResource resource = new YamlFileResource(file);
        ConfigurationData configurationData = ConfigurationDataBuilder.getAllProperties(TestConfiguration.class);

        // when
        resource.exportProperties(configurationData);

        // then
        // Load file again to make sure what we wrote can be read again
        resource = new YamlFileResource(file);
        Map<Property<?>, Object> expected = new HashMap<>();
        expected.put(TestConfiguration.DURATION_IN_SECONDS, 22);
        expected.put(TestConfiguration.SYSTEM_NAME, "[TestDefaultValue]");
        expected.put(TestConfiguration.RATIO_ORDER, "SECOND");
        expected.put(TestConfiguration.RATIO_FIELDS, Arrays.asList("Australia", "Burundi", "Colombia"));
        expected.put(TestConfiguration.VERSION_NUMBER, 32046);
        expected.put(TestConfiguration.SKIP_BORING_FEATURES, false);
        expected.put(TestConfiguration.BORING_COLORS, Collections.EMPTY_LIST);
        expected.put(TestConfiguration.DUST_LEVEL, -1);
        expected.put(TestConfiguration.USE_COOL_FEATURES, false);
        expected.put(TestConfiguration.COOL_OPTIONS, Arrays.asList("Dinosaurs", "Explosions", "Big trucks"));
        for (Map.Entry<Property<?>, Object> entry : expected.entrySet()) {
            // Check with resource#getObject to make sure the values were persisted to the file
            // If we go through Property objects they may fall back to their default values
            String propertyPath = entry.getKey().getPath();
            assertThat("Property '" + propertyPath + "' has expected value",
                resource.getObject(propertyPath), equalTo(entry.getValue()));
        }
    }

    /** Verifies that "difficult cases" such as apostrophes in strings etc. are handled properly. */
    @Test
    public void shouldProperlyExportAnyValues() {
        // given
        File file = copyFileFromResources(DIFFICULT_FILE);
        YamlFileResource resource = new YamlFileResource(file);

        // Properties
        List<Property<?>> properties = new ArrayList<>(Arrays.asList(
            newProperty("more.string1", "it's a text with some \\'apostrophes'"),
            newProperty("more.string2", "\tthis one\nhas some\nnew '' lines-test")));
        properties.addAll(ConfigurationDataBuilder.getAllProperties(TestConfiguration.class).getProperties());
        ConfigurationData configData = new ConfigurationData(properties);

        // when
        new SettingsManager(resource, checkAllPropertiesPresent(), configData);
        // Save and load again
        resource.exportProperties(configData);
        resource.reload();

        // then
        assertThat(resource.getObject(TestConfiguration.DUST_LEVEL.getPath()), not(nullValue()));

        Map<Property<?>, Object> expected = new HashMap<>();
        expected.put(TestConfiguration.DURATION_IN_SECONDS, 20);
        expected.put(TestConfiguration.SYSTEM_NAME, "A 'test' name");
        expected.put(TestConfiguration.RATIO_ORDER, "FOURTH");
        expected.put(TestConfiguration.RATIO_FIELDS, Arrays.asList("Australia\\", "\tBurundi'", "Colombia?\n''"));
        expected.put(TestConfiguration.VERSION_NUMBER, -1337);
        expected.put(TestConfiguration.SKIP_BORING_FEATURES, false);
        expected.put(TestConfiguration.BORING_COLORS, Arrays.asList("it's a difficult string!", "gray\nwith new lines\n"));
        expected.put(TestConfiguration.DUST_LEVEL, -1);
        expected.put(TestConfiguration.USE_COOL_FEATURES, true);
        expected.put(TestConfiguration.COOL_OPTIONS, Collections.EMPTY_LIST);
        expected.put(properties.get(0), properties.get(0).getDefaultValue());
        expected.put(properties.get(1), properties.get(1).getDefaultValue());

        for (Map.Entry<Property<?>, Object> entry : expected.entrySet()) {
            assertThat("Property '" + entry.getKey().getPath() + "' has expected value",
                resource.getObject(entry.getKey().getPath()), equalTo(entry.getValue()));
        }
    }

    @Test
    public void shouldReloadValues() throws IOException {
        // given
        File file = temporaryFolder.newFile();
        YamlFileResource resource = new YamlFileResource(file);
        Files.copy(getJarPath(COMPLETE_FILE), file.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // when
        assertThat(TestConfiguration.RATIO_ORDER.getValue(resource), equalTo(TestEnum.SECOND)); // default value
        resource.reload();

        // then
        assertThat(TestConfiguration.RATIO_ORDER.getValue(resource), equalTo(TestEnum.FIRST));
    }

    @Test
    public void shouldRetrieveTypedValues() {
        // given
        File file = copyFileFromResources(COMPLETE_FILE);
        YamlFileResource resource = new YamlFileResource(file);

        // when / then
        assertThat(resource.getBoolean(TestConfiguration.DURATION_IN_SECONDS.getPath()), nullValue());
        assertThat(resource.getString(TestConfiguration.DURATION_IN_SECONDS.getPath()), nullValue());
        assertThat(resource.getDouble(TestConfiguration.DURATION_IN_SECONDS.getPath()), equalTo(22.0));
        assertThat(resource.getDouble(TestConfiguration.SKIP_BORING_FEATURES.getPath()), nullValue());
    }

    @Test
    public void shouldSetValuesButNotPersist() {
        // given
        File file = copyFileFromResources(INCOMPLETE_FILE);
        YamlFileResource resource = new YamlFileResource(file);

        // when
        assertThat(TestConfiguration.RATIO_ORDER.getValue(resource), equalTo(TestEnum.SECOND)); // default value
        resource.setValue(TestConfiguration.RATIO_ORDER.getPath(), TestEnum.THIRD);
        resource.setValue(TestConfiguration.SKIP_BORING_FEATURES.getPath(), true);

        // then
        assertThat(TestConfiguration.RATIO_ORDER.getValue(resource), equalTo(TestEnum.THIRD));
        assertThat(TestConfiguration.SKIP_BORING_FEATURES.getValue(resource), equalTo(true));

        // when (2) - reload without saving, so will fallback to default again
        resource.reload();

        // then
        assertThat(TestConfiguration.RATIO_ORDER.getValue(resource), equalTo(TestEnum.SECOND));
        assertThat(TestConfiguration.SKIP_BORING_FEATURES.getValue(resource), equalTo(false));
    }

    @Test
    public void shouldReturnIfResourceContainsValue() {
        // given
        File file = copyFileFromResources(INCOMPLETE_FILE);
        PropertyResource resource = new YamlFileResource(file);

        // when
        boolean presentPropertyResult = resource.contains(TestConfiguration.DURATION_IN_SECONDS.getPath());
        boolean absentPropertyResult = resource.contains(TestConfiguration.SKIP_BORING_FEATURES.getPath());

        // then
        assertThat(presentPropertyResult, equalTo(true));
        assertThat(absentPropertyResult, equalTo(false));
    }

    @Test
    public void shouldWrapIoExceptionInConfigMeException() throws IOException {
        // given
        File file = copyFileFromResources(INCOMPLETE_FILE);
        PropertyResource resource = new YamlFileResource(file);
        file.delete();
        // Hacky: the only way we can easily provoke an IOException is by deleting the file and creating a folder
        // with the same name...
        temporaryFolder.newFolder(file.getName());

        // when / then
        try {
            resource.exportProperties(ConfigurationDataBuilder.getAllProperties(TestConfiguration.class));
            fail("Expected ConfigMeException to be thrown");
        } catch (ConfigMeException e) {
            assertThat(e.getCause(), instanceOf(IOException.class));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnRootForEmptyString() throws IOException {
        // given
        File file = copyFileFromResources(COMPLETE_FILE);
        PropertyResource resource = new YamlFileResource(file);

        // when
        Object result = resource.getObject("");

        // then
        assertThat(result, instanceOf(Map.class));
        assertThat(((Map<String, ?>) result).keySet(), containsInAnyOrder("test", "sample", "version", "features"));
    }

    @Test
    public void shouldExportConfigurationWithExpectedComments() throws IOException {
        // given
        File file = copyFileFromResources(COMPLETE_FILE);
        PropertyResource resource = new YamlFileResource(file);
        ConfigurationData configurationData = ConfigurationDataBuilder.getAllProperties(TestConfiguration.class);

        // when
        resource.exportProperties(configurationData);

        // then
        // In some cases we have a space before a new line. The IDE strips ending whitespace automatically and so there
        // is a mismatch because of those spaces; this pattern will remove any ending space to fix this.
        // Similarly, we add a new line because IntelliJ keeps adding a new line to the saved file.
        Pattern pattern = Pattern.compile("( )$", Pattern.MULTILINE);
        String writtenFileContents = new String(Files.readAllBytes(file.toPath())) + "\n";
        assertThat(pattern.matcher(writtenFileContents).replaceAll(""),
            equalTo(new String(Files.readAllBytes(getJarPath("/config-expected-export.yml")))));
    }

    private File copyFileFromResources(String path) {
        try {
            Path source = getJarPath(path);
            File destination = temporaryFolder.newFile();
            Files.copy(source, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return destination;
        } catch (IOException e) {
            throw new IllegalStateException("Could not copy test file", e);
        }
    }
}
