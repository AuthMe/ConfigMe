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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.jalu.configme.TestUtils.getJarPath;
import static ch.jalu.configme.TestUtils.isErrorValueOf;
import static ch.jalu.configme.TestUtils.isValidValueOf;
import static ch.jalu.configme.configurationdata.ConfigurationDataBuilder.createConfiguration;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link YamlFileResource}.
 */
@ExtendWith(MockitoExtension.class)
class YamlFileResourceTest {

    private static final String COMPLETE_FILE = "/config-sample.yml";
    private static final String INCOMPLETE_FILE = "/config-incomplete-sample.yml";
    private static final String DIFFICULT_FILE = "/config-difficult-values.yml";

    @TempDir
    public Path temporaryFolder;

    @Test
    void shouldWriteMissingProperties() {
        // given
        Path file = copyFileFromResources(INCOMPLETE_FILE);
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
        expected.put(TestConfiguration.RATIO_FIELDS, asList("Australia", "Burundi", "Colombia"));
        expected.put(TestConfiguration.VERSION_NUMBER, 32046);
        expected.put(TestConfiguration.SKIP_BORING_FEATURES, false);
        expected.put(TestConfiguration.BORING_COLORS, Collections.EMPTY_LIST);
        expected.put(TestConfiguration.DUST_LEVEL, -1.1);
        expected.put(TestConfiguration.USE_COOL_FEATURES, false);
        expected.put(TestConfiguration.COOL_OPTIONS, asList("Dinosaurs", "Explosions", "Big trucks"));
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
    void shouldProperlyExportAnyValues() {
        // given
        Path file = copyFileFromResources(DIFFICULT_FILE);
        YamlFileResource resource = new YamlFileResource(file);

        // Properties
        List<Property<?>> properties = new ArrayList<>(asList(
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
        expected.put(TestConfiguration.RATIO_FIELDS, asList("Australia\\", "\tBurundi'", "Colombia?\n''"));
        expected.put(TestConfiguration.VERSION_NUMBER, -1337);
        expected.put(TestConfiguration.SKIP_BORING_FEATURES, false);
        expected.put(TestConfiguration.BORING_COLORS, asList("it's a difficult string!", "gray\nwith new lines\n"));
        expected.put(TestConfiguration.DUST_LEVEL, -1.1);
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
    void shouldReloadValues() throws IOException {
        // given
        Path file = temporaryFolder.resolve("file");
        Files.createFile(file);
        YamlFileResource resource = new YamlFileResource(file);

        // when
        PropertyReader readerBeforeCopy = resource.createReader();
        Files.copy(getJarPath(COMPLETE_FILE), file, StandardCopyOption.REPLACE_EXISTING);
        PropertyReader readerAfterCopy = resource.createReader();

        // then
        assertThat(TestConfiguration.RATIO_ORDER.determineValue(readerBeforeCopy), isErrorValueOf(TestEnum.SECOND)); // default value
        assertThat(TestConfiguration.RATIO_ORDER.determineValue(readerAfterCopy), isValidValueOf(TestEnum.FIRST));
    }


    @Test
    void shouldWrapIoExceptionInConfigMeException() throws IOException {
        // given
        Path file = copyFileFromResources(INCOMPLETE_FILE);
        PropertyResource resource = new YamlFileResource(file);
        ConfigurationData configurationData = createConfiguration(TestConfiguration.class);
        configurationData.initializeValues(resource.createReader());
        Files.delete(file);
        // Hacky: the only way we can easily provoke an IOException is by deleting the file and creating a folder
        // with the same name...
        Path childFolder = temporaryFolder.resolve(file.getFileName().toString());
        Files.createDirectory(childFolder);

        // when / then
        try {
            resource.exportProperties(configurationData);
            fail("Expected ConfigMeException to be thrown");
        } catch (ConfigMeException e) {
            assertThat(e.getCause(), instanceOf(IOException.class));
        }
    }

    @Test
    void shouldExportConfigurationWithExpectedComments() throws IOException {
        // given
        Path file = copyFileFromResources(COMPLETE_FILE);
        PropertyResource resource = new YamlFileResource(file);
        ConfigurationData configurationData = createConfiguration(TestConfiguration.class);
        configurationData.initializeValues(resource.createReader());

        // when
        resource.exportProperties(configurationData);

        // then
        // The IDE likes manipulating the whitespace in the expected file. As long as it's handled outside of an IDE
        // this test should be fine.
        assertThat(Files.readAllLines(file),
            equalTo(Files.readAllLines(getJarPath("/config-export-expected.yml"))));
    }

    @Test
    void shouldSkipAbsentOptionalProperty() throws IOException {
        // given
        ConfigurationData configurationData = createConfiguration(asList(
            new OptionalProperty<>(TestConfiguration.DURATION_IN_SECONDS),
            new OptionalProperty<>(TestConfiguration.RATIO_ORDER)));
        Path file = copyFileFromResources(INCOMPLETE_FILE);
        PropertyResource resource = new YamlFileResource(file);
        configurationData.initializeValues(resource.createReader());

        // when
        resource.exportProperties(configurationData);

        // then
        List<String> exportedLines = Files.readAllLines(file);
        assertThat(exportedLines, contains(
            "test:",
            "    duration: 22"
        ));
    }

    @Test
    void shouldExportAllPresentOptionalProperties() throws IOException {
        // given
        ConfigurationData configurationData = createConfiguration(asList(
            new OptionalProperty<>(TestConfiguration.DURATION_IN_SECONDS),
            new OptionalProperty<>(TestConfiguration.RATIO_ORDER)));
        Path file = copyFileFromResources(COMPLETE_FILE);
        PropertyResource resource = new YamlFileResource(file);
        configurationData.initializeValues(resource.createReader());

        // when
        resource.exportProperties(configurationData);

        // then
        List<String> exportedLines = Files.readAllLines(file);
        assertThat(exportedLines, contains(
            "test:",
            "    duration: 22",
            "sample:",
            "    ratio:",
            "        order: FIRST"
        ));
    }

    @Test
    void shouldExportEmptyMap() throws IOException {
        // given
        CommandConfig config = new CommandConfig();
        config.setDuration(3);
        config.setCommands(Collections.emptyMap());

        Path file = copyFileFromResources("/beanmapper/commands.yml");
        YamlFileResource resource = new YamlFileResource(file);

        Property<CommandConfig> commandConfigProperty =
            new BeanProperty<>("config", CommandConfig.class, new CommandConfig());
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(singletonList(commandConfigProperty));
        configurationData.setValue(commandConfigProperty, config);

        // when
        resource.exportProperties(configurationData);

        // then
        List<String> exportedLines = Files.readAllLines(file);
        assertThat(exportedLines, contains(
            "config:",
            "    commands: {}",
            "    duration: 3"
        ));
    }

    @Test
    void shouldExportWithUtf8() throws IOException {
        // given
        Path file = copyFileFromResources("/charsets/utf8_sample.yml");
        YamlFileResource resource = new YamlFileResource(file);

        Property<String> firstProp = newProperty("first", "");
        Property<String> secondProp = newProperty("second", "");
        Property<String> thirdProp = newProperty("third", "");
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(asList(firstProp, secondProp, thirdProp));
        configurationData.initializeValues(resource.createReader());
        configurationData.setValue(secondProp, "თბილისი");

        // when
        resource.exportProperties(configurationData);

        // then
        List<String> exportedLines = Files.readAllLines(file);
        assertThat(exportedLines, contains(
            "first: Санкт-Петербург",
            "second: თბილისი",
            "third: 错误的密码"
        ));
    }

    @Test
    void shouldExportWithIso88591() throws IOException {
        // given
        Path file = copyFileFromResources("/charsets/iso-8859-1_sample.yml");

        YamlFileResourceOptions options = YamlFileResourceOptions.builder()
            .charset(StandardCharsets.ISO_8859_1)
            .build();
        YamlFileResource resource = new YamlFileResource(file, options);

        Property<String> firstProp = newProperty("elem.first", "");
        Property<String> secondProp = newProperty("elem.second", "");
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(asList(firstProp, secondProp));
        configurationData.initializeValues(resource.createReader());
        configurationData.setValue(secondProp, "awq ôÖ ÿõ 1234");

        // when
        resource.exportProperties(configurationData);

        // then
        List<String> exportedLines = Files.readAllLines(file, StandardCharsets.ISO_8859_1);
        assertThat(exportedLines, contains(
            "elem:",
            "    first: test Ã ö û þ",
            "    second: awq ôÖ ÿõ 1234"
        ));
    }

    @Test
    void shouldReturnFieldsOfResource() {
        // given
        Path configFile = mock(Path.class);
        YamlFileResourceOptions options = mock(YamlFileResourceOptions.class);
        YamlFileResource resource = new YamlFileResource(configFile, options);

        // when
        Path returnedPath = resource.getPath();
        YamlFileResourceOptions returnedOptions = resource.getOptions();

        // then
        assertThat(returnedPath, sameInstance(configFile));
        assertThat(returnedOptions, sameInstance(options));
    }

    @Test
    void shouldExportWithCustomIndentationSize() throws IOException {
        // given
        Path file = copyFileFromResources("/config-sample.yml");

        YamlFileResourceOptions options = YamlFileResourceOptions.builder()
            .indentationSize(2)
            .build();
        YamlFileResource resource = new YamlFileResource(file, options);

        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(TestConfiguration.class);
        configurationData.initializeValues(resource.createReader());

        // when
        resource.exportProperties(configurationData);

        // then
        List<String> exportedLines = Files.readAllLines(file, StandardCharsets.ISO_8859_1);
        assertThat(exportedLines, contains(
            "# Test section",
            "test:",
            "  # Duration in seconds",
            "  duration: 22",
            "  # The system name",
            "  systemName: Custom sys name",
            "# Sample section",
            "sample:",
            "  ratio:",
            "    order: FIRST",
            "    fields:",
            "    - Australia",
            "    - Burundi",
            "    - Colombia",
            "# The version number",
            "# This is just a random number",
            "version: 2492",
            "features:",
            "  # Plain boring features",
            "  boring:",
            "    # Skip boring features?",
            "    skip: false",
            "    # Add some boring colors here (gray, beige, ...)",
            "    colors:",
            "    - beige",
            "    - gray",
            "    dustLevel: 2.4",
            "  ",
            "  # Cool features",
            "",
            "    # Contains cool settings",
            "  cool:",
            "    # Enable cool features?",
            "    enabled: true",
            "    # List of cool options to use",
            "    options:",
            "    - Dinosaurs",
            "    - Explosions",
            "    - Big trucks",
            "security:",
            "  # Forbidden names",
            "  forbiddenNames:",
            "  - admin",
            "  - staff",
            "  - moderator"
        ));
    }

    private Path copyFileFromResources(String path) {
        return TestUtils.copyFileFromResources(path, temporaryFolder);
    }
}
