package ch.jalu.configme.resource;

import ch.jalu.configme.TestUtils;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.samples.TestConfiguration;
import ch.jalu.configme.samples.TestEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static ch.jalu.configme.TestUtils.createTemporaryFile;
import static ch.jalu.configme.TestUtils.isValidValueOf;
import static ch.jalu.configme.TestUtils.verifyException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

/**
 * Test for {@link YamlFileReader}.
 */
class YamlFileReaderTest {

    private static final String COMPLETE_FILE = "/config-sample.yml";
    private static final String INCOMPLETE_FILE = "/config-incomplete-sample.yml";

    @TempDir
    public Path temporaryFolder;

    @Test
    void shouldThrowForAbsentYamlMap() throws IOException {
        // given
        Path file = temporaryFolder.resolve("temp-file");
        Files.write(file, "123".getBytes());

        // when / then
        verifyException(() -> new YamlFileReader(file),
            ConfigMeException.class, "Top-level is not a map");
    }

    @Test
    void shouldWrapIOException() {
        // given
        Path file = temporaryFolder.resolve("test");

        // when / then
        verifyException(() -> new YamlFileReader(file),
            ConfigMeException.class, "Could not read file");
    }

    @Test
    void shouldReadAllProperties() {
        // given
        Path config = copyFileFromResources(COMPLETE_FILE);

        // when
        PropertyReader reader = new YamlFileReader(config);

        // then
        Map<Property<?>, Object> expected = new HashMap<>();
        expected.put(TestConfiguration.DURATION_IN_SECONDS, 22);
        expected.put(TestConfiguration.SYSTEM_NAME, "Custom sys name");
        expected.put(TestConfiguration.RATIO_ORDER, TestEnum.FIRST);
        expected.put(TestConfiguration.RATIO_FIELDS, Arrays.asList("Australia", "Burundi", "Colombia"));
        expected.put(TestConfiguration.VERSION_NUMBER, 2492);
        expected.put(TestConfiguration.SKIP_BORING_FEATURES, false);
        expected.put(TestConfiguration.BORING_COLORS, Arrays.asList("beige", "gray"));
        expected.put(TestConfiguration.DUST_LEVEL, 2.4);
        expected.put(TestConfiguration.USE_COOL_FEATURES, true);
        expected.put(TestConfiguration.COOL_OPTIONS, Arrays.asList("Dinosaurs", "Explosions", "Big trucks"));
        expected.put(TestConfiguration.FORBIDDEN_NAMES, new LinkedHashSet<>(Arrays.asList("admin", "staff", "moderator")));

        for (Map.Entry<Property<?>, Object> entry : expected.entrySet()) {
            assertThat("Property '" + entry.getKey().getPath() + "' has expected value",
                entry.getKey().determineValue(reader), isValidValueOf(entry.getValue()));
        }
    }

    @Test
    void shouldRetrieveTypedValues() {
        // given
        Path file = copyFileFromResources(COMPLETE_FILE);
        PropertyReader reader = new YamlFileReader(file);

        // when / then
        assertThat(reader.getBoolean(TestConfiguration.DURATION_IN_SECONDS.getPath()), nullValue());
        assertThat(reader.getString(TestConfiguration.DURATION_IN_SECONDS.getPath()), nullValue());
        assertThat(reader.getDouble(TestConfiguration.DURATION_IN_SECONDS.getPath()), equalTo(22.0));
        assertThat(reader.getDouble(TestConfiguration.SKIP_BORING_FEATURES.getPath()), nullValue());
    }

    @Test
    void shouldReadValuesAndHandleAbsentOnes() {
        // given
        Path file = copyFileFromResources(INCOMPLETE_FILE);

        // when
        PropertyReader reader = new YamlFileReader(file);

        // then
        assertThat(reader.getInt(TestConfiguration.DURATION_IN_SECONDS.getPath()), equalTo(22));
        assertThat(reader.getBoolean(TestConfiguration.SKIP_BORING_FEATURES.getPath()), nullValue());
        assertThat(reader.getList("some.absent.path"), nullValue());
        assertThat(reader.getString(TestConfiguration.SKIP_BORING_FEATURES.getPath() + ".more.path"), nullValue());
    }

    @Test
    void shouldReturnIfReaderContainsValue() {
        // given
        Path file = copyFileFromResources(INCOMPLETE_FILE);
        PropertyReader reader = new YamlFileReader(file);

        // when
        boolean presentPropertyResult = reader.contains(TestConfiguration.DURATION_IN_SECONDS.getPath());
        boolean absentPropertyResult = reader.contains(TestConfiguration.SKIP_BORING_FEATURES.getPath());

        // then
        assertThat(presentPropertyResult, equalTo(true));
        assertThat(absentPropertyResult, equalTo(false));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnRootForEmptyString() {
        // given
        Path file = copyFileFromResources(COMPLETE_FILE);
        PropertyReader reader = new YamlFileReader(file);

        // when
        Object result = reader.getObject("");

        // then
        assertThat(result, instanceOf(Map.class));
        assertThat(((Map<String, ?>) result).keySet(), containsInAnyOrder("test", "sample", "version", "features", "security"));
    }

    @Test
    void shouldReturnNullForUnknownPath() {
        // given
        Path file = copyFileFromResources(COMPLETE_FILE);
        YamlFileReader reader = new YamlFileReader(file);

        // when / then
        assertThat(reader.getObject("sample.ratio.wrong.dunno"), nullValue());
        assertThat(reader.getObject(TestConfiguration.RATIO_ORDER.getPath() + ".child"), nullValue());
        assertThat(reader.getRoot().keySet(), containsInAnyOrder("test", "sample", "version", "features", "security"));
    }

    @Test
    void shouldWrapYamlException() throws IOException {
        // given
        String invalidYaml = "test:\n   'broken quote";
        Path file = createTemporaryFile(temporaryFolder);
        Files.write(file, invalidYaml.getBytes());

        // when / then
        verifyException(() -> new YamlFileReader(file.toFile()),
            ConfigMeException.class, "YAML error while trying to load file");
    }

    @Test
    void shouldHandleEmptyFile() {
        // given
        Path configFile = createTemporaryFile(temporaryFolder);
        YamlFileReader reader = new YamlFileReader(configFile);

        // when
        Path result = reader.getPath();
        File resultFile = reader.getFile();

        // then
        assertThat(result, sameInstance(configFile));
        assertThat(resultFile, equalTo(configFile.toFile()));
        assertThat(reader.getRoot(), anEmptyMap());
    }

    @Test
    void shouldReadWithUtf8() {
        // given
        Path file = copyFileFromResources("/charsets/utf8_sample.yml");
        YamlFileReader reader = new YamlFileReader(file);

        // when / then
        assertThat(reader.getString("first"), equalTo("Санкт-Петербург"));
        assertThat(reader.getString("second"), equalTo("շերտիկներն"));
        assertThat(reader.getString("third"), equalTo("错误的密码"));
    }

    @Test
    void shouldReadWithCustomCharset() {
        // given
        Path file = copyFileFromResources("/charsets/iso-8859-1_sample.yml");
        YamlFileReader reader = new YamlFileReader(file, StandardCharsets.ISO_8859_1);

        // when / then
        assertThat(reader.getString("elem.first"), equalTo("test Ã ö û þ"));
        assertThat(reader.getString("elem.second"), equalTo("øå Æ"));
    }

    @Test
    void shouldReturnKeysOfFile() {
        // given
        Path file = copyFileFromResources(COMPLETE_FILE);
        YamlFileReader reader = new YamlFileReader(file);

        // when
        Set<String> keys = reader.getKeys(false);

        // then
        assertThat(keys, contains("test", "test.duration", "test.systemName",
            "sample", "sample.ratio", "sample.ratio.order", "sample.ratio.fields",
            "version",
            "features", "features.boring", "features.boring.skip", "features.boring.colors", "features.boring.dustLevel",
                        "features.cool", "features.cool.enabled", "features.cool.options",
            "security", "security.forbiddenNames"));
    }

    @Test
    void shouldReturnLeafNodeKeysInFile() {
        // given
        Path file = copyFileFromResources(COMPLETE_FILE);
        YamlFileReader reader = new YamlFileReader(file);

        // when
        Set<String> keys = reader.getKeys(true);

        // then
        assertThat(keys, contains("test.duration", "test.systemName",
            "sample.ratio.order", "sample.ratio.fields",
            "version",
            "features.boring.skip", "features.boring.colors", "features.boring.dustLevel",
            "features.cool.enabled", "features.cool.options",
            "security.forbiddenNames"));
    }

    @Test
    void shouldTreatEmptyMapsAsLeafNodes() {
        // given
        Path file = copyFileFromResources("/beanmapper/nested_chat_component_complex_expected.yml");
        YamlFileReader reader = new YamlFileReader(file);

        // when
        Set<String> keys = reader.getKeys(true);

        // then
        assertThat(keys, hasSize(24));
        assertThat(keys, hasItems(
            "message-key.conditionalElem.conditionals.low.conditionals", // empty map
            "message-key.conditionalElem.conditionals.med.color",
            "message-key.conditionalElem.conditionals.high.conditionalElem.bold",
            "message-key.conditionalElem.conditionals.high.conditionalElem.conditionals", // empty map
            "message-key.conditionalElem.conditionals.high.conditionalElem.extra", // empty collection
            "message-key.conditionalElem.extra", // empty collection
            "message-key.extra", // empty collection
            "message-key.text"));
    }

    @Test
    void shouldReturnChildrenPathsOfGivenPath() {
        // given
        Path file = copyFileFromResources(COMPLETE_FILE);
        YamlFileReader reader = new YamlFileReader(file);

        // when
        Set<String> keys = reader.getChildKeys("features.boring");

        // then
        assertThat(keys, contains("features.boring.skip", "features.boring.colors", "features.boring.dustLevel"));
    }

    @Test
    void shouldReturnChildrenPathsOfRoot() {
        // given
        Path file = copyFileFromResources(COMPLETE_FILE);
        YamlFileReader reader = new YamlFileReader(file);

        // when
        Set<String> keys = reader.getChildKeys("");

        // then
        assertThat(keys, contains("test", "sample", "version", "features", "security"));
    }

    @Test
    void shouldReturnEmptySetForNonExistentOrLeafValue() {
        // given
        Path file = copyFileFromResources(COMPLETE_FILE);
        YamlFileReader reader = new YamlFileReader(file);

        // when
        Set<String> bogusChildren = reader.getChildKeys("bogus");
        Set<String> leafChildren = reader.getChildKeys("features.boring.colors");

        // then
        assertThat(bogusChildren, empty());
        assertThat(leafChildren, empty());
    }

    private Path copyFileFromResources(String path) {
        return TestUtils.copyFileFromResources(path, temporaryFolder);
    }
}
