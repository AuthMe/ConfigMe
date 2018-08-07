package ch.jalu.configme.resource;

import ch.jalu.configme.TestUtils;
import ch.jalu.configme.beanmapper.command.CommandConfig;
import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.BeanProperty;
import ch.jalu.configme.properties.OptionalProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.samples.TestConfiguration;
import ch.jalu.configme.samples.TestEnum;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.jalu.configme.TestUtils.getJarPath;
import static ch.jalu.configme.configurationdata.ConfigurationDataBuilder.createConfiguration;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Test for {@link YamlFileResource}.
 */
public class YamlFileResourceTest {

    private static final String COMPLETE_FILE = "/config-sample.yml";
    private static final String INCOMPLETE_FILE = "/config-incomplete-sample.yml";
    private static final String DIFFICULT_FILE = "/config-difficult-values.yml";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldWriteMissingProperties() {
        // given
        File file = copyFileFromResources(INCOMPLETE_FILE);
        YamlFileResource resource = new YamlFileResource(file);
        ConfigurationData configurationData = createConfiguration(TestConfiguration.class);
        configurationData.initializeValues(resource.createReader());

        // when
        resource.exportProperties(configurationData);

        // then
        // Load file again to make sure what we wrote can be read again
        resource = new YamlFileResource(file);
        PropertyReader reader = resource.createReader();
        Map<Property<?>, Object> expected = new HashMap<>();
        expected.put(TestConfiguration.DURATION_IN_SECONDS, 22);
        expected.put(TestConfiguration.SYSTEM_NAME, "[TestDefaultValue]");
        expected.put(TestConfiguration.RATIO_ORDER, TestEnum.SECOND.name());
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
               reader.getObject(propertyPath), equalTo(entry.getValue()));
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
        properties.addAll(createConfiguration(TestConfiguration.class).getProperties());
        ConfigurationData configData = createConfiguration(properties);
        configData.initializeValues(resource.createReader());

        // when
        resource.exportProperties(configData);

        // then
        PropertyReader reader = resource.createReader();
        assertThat(reader.getObject(TestConfiguration.DUST_LEVEL.getPath()), not(nullValue()));

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
                reader.getObject(entry.getKey().getPath()), equalTo(entry.getValue()));
        }
    }

    @Test
    public void shouldReloadValues() throws IOException {
        // given
        File file = temporaryFolder.newFile();
        YamlFileResource resource = new YamlFileResource(file);

        // when
        PropertyReader readerBeforeCopy = resource.createReader();
        Files.copy(getJarPath(COMPLETE_FILE), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        PropertyReader readerAfterCopy = resource.createReader();

        // then
        assertThat(TestConfiguration.RATIO_ORDER.determineValue(readerBeforeCopy), equalTo(TestEnum.SECOND)); // default value
        assertThat(TestConfiguration.RATIO_ORDER.determineValue(readerAfterCopy), equalTo(TestEnum.FIRST));
    }


    @Test
    public void shouldWrapIoExceptionInConfigMeException() throws IOException {
        // given
        File file = copyFileFromResources(INCOMPLETE_FILE);
        PropertyResource resource = new YamlFileResource(file);
        ConfigurationData configurationData = createConfiguration(TestConfiguration.class);
        configurationData.initializeValues(resource.createReader());
        file.delete();
        // Hacky: the only way we can easily provoke an IOException is by deleting the file and creating a folder
        // with the same name...
        temporaryFolder.newFolder(file.getName());

        // when / then
        try {
            resource.exportProperties(configurationData);
            fail("Expected ConfigMeException to be thrown");
        } catch (ConfigMeException e) {
            assertThat(e.getCause(), instanceOf(IOException.class));
        }
    }

    @Test
    public void shouldExportConfigurationWithExpectedComments() throws IOException {
        // given
        File file = copyFileFromResources(COMPLETE_FILE);
        PropertyResource resource = new YamlFileResource(file);
        ConfigurationData configurationData = createConfiguration(TestConfiguration.class);
        configurationData.initializeValues(resource.createReader());

        // when
        resource.exportProperties(configurationData);

        // then
        // The IDE likes manipulating the whitespace in the expected file. As long as it's handled outside of an IDE
        // this test should be fine.
        assertThat(Files.readAllLines(file.toPath()),
            equalTo(Files.readAllLines(getJarPath("/config-export-expected.yml"))));
    }

    @Test
    public void shouldSkipAbsentOptionalProperty() throws IOException {
        // given
        ConfigurationData configurationData = createConfiguration(Arrays.asList(
            new OptionalProperty<>(TestConfiguration.DURATION_IN_SECONDS),
            new OptionalProperty<>(TestConfiguration.RATIO_ORDER)));
        File file = copyFileFromResources(INCOMPLETE_FILE);
        PropertyResource resource = new YamlFileResource(file);
        configurationData.initializeValues(resource.createReader());

        // when
        resource.exportProperties(configurationData);

        // then
        List<String> exportedLines = Files.readAllLines(file.toPath());
        assertThat(exportedLines, contains(
            "",
            "test:",
            "    duration: 22"
        ));
    }

    @Test
    public void shouldExportAllPresentOptionalProperties() throws IOException {
        // given
        ConfigurationData configurationData = createConfiguration(Arrays.asList(
            new OptionalProperty<>(TestConfiguration.DURATION_IN_SECONDS),
            new OptionalProperty<>(TestConfiguration.RATIO_ORDER)));
        File file = copyFileFromResources(COMPLETE_FILE);
        PropertyResource resource = new YamlFileResource(file);
        configurationData.initializeValues(resource.createReader());

        // when
        resource.exportProperties(configurationData);

        // then
        List<String> exportedLines = Files.readAllLines(file.toPath());
        assertThat(exportedLines, contains(
            "",
            "test:",
            "    duration: 22",
            "sample:",
            "    ratio:",
            "        order: FIRST"
        ));
    }

    @Test
    public void shouldExportEmptyMap() throws IOException {
        // given
        CommandConfig config = new CommandConfig();
        config.setDuration(3);
        config.setCommands(Collections.emptyMap());

        File file = copyFileFromResources("/beanmapper/commands.yml");
        YamlFileResource resource = new YamlFileResource(file);

        Property<CommandConfig> commandConfigProperty =
            new BeanProperty<>(CommandConfig.class, "config", new CommandConfig());
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(singletonList(commandConfigProperty));
        configurationData.setValue(commandConfigProperty, config);

        // when
        resource.exportProperties(configurationData);

        // then
        List<String> exportedLines = Files.readAllLines(file.toPath());
        assertThat(exportedLines, contains(
            "",
            "config:",
            "    commands: {}",
            "    duration: 3"
        ));
    }


    private File copyFileFromResources(String path) {
        return TestUtils.copyFileFromResources(path, temporaryFolder);
    }
}
