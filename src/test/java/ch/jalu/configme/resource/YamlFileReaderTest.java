package ch.jalu.configme.resource;

import ch.jalu.configme.TestUtils;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.samples.TestConfiguration;
import ch.jalu.configme.samples.TestEnum;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static ch.jalu.configme.TestUtils.verifyException;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link YamlFileReader}.
 */
public class YamlFileReaderTest {

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
        verifyException(() -> new YamlFileReader(file),
            ConfigMeException.class, "Top-level is not a map");
    }

    @Test
    public void shouldWrapIOException() throws IOException {
        // given
        File folder = temporaryFolder.newFolder();
        File file = new File(folder, "test");

        // when / then
        verifyException(() -> new YamlFileReader(file),
            ConfigMeException.class, "Could not read file");
    }

    @Test
    public void shouldReadAllProperties() {
        // given
        File config = copyFileFromResources(COMPLETE_FILE);

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
        expected.put(TestConfiguration.DUST_LEVEL, 2);
        expected.put(TestConfiguration.USE_COOL_FEATURES, true);
        expected.put(TestConfiguration.COOL_OPTIONS, Arrays.asList("Dinosaurs", "Explosions", "Big trucks"));

        for (Map.Entry<Property<?>, Object> entry : expected.entrySet()) {
            assertThat("Property '" + entry.getKey().getPath() + "' has expected value",
                entry.getKey().determineValue(reader), equalTo(entry.getValue()));
        }
    }

    @Test
    public void shouldRetrieveTypedValues() {
        // given
        File file = copyFileFromResources(COMPLETE_FILE);
        PropertyReader reader = new YamlFileReader(file);

        // when / then
        assertThat(reader.getBoolean(TestConfiguration.DURATION_IN_SECONDS.getPath()), nullValue());
        assertThat(reader.getString(TestConfiguration.DURATION_IN_SECONDS.getPath()), nullValue());
        assertThat(reader.getDouble(TestConfiguration.DURATION_IN_SECONDS.getPath()), equalTo(22.0));
        assertThat(reader.getDouble(TestConfiguration.SKIP_BORING_FEATURES.getPath()), nullValue());
    }

    @Test
    public void shouldReadValuesAndHandleAbsentOnes() {
        // given
        File file = copyFileFromResources(INCOMPLETE_FILE);

        // when
        PropertyReader reader = new YamlFileReader(file);

        // then
        assertThat(reader.getInt(TestConfiguration.DURATION_IN_SECONDS.getPath()), equalTo(22));
        assertThat(reader.getBoolean(TestConfiguration.SKIP_BORING_FEATURES.getPath()), nullValue());
        assertThat(reader.getList("some.absent.path"), nullValue());
        assertThat(reader.getString(TestConfiguration.SKIP_BORING_FEATURES.getPath() + ".more.path"), nullValue());
    }

    @Test
    public void shouldReturnIfReaderContainsValue() {
        // given
        File file = copyFileFromResources(INCOMPLETE_FILE);
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
    public void shouldReturnRootForEmptyString() {
        // given
        File file = copyFileFromResources(COMPLETE_FILE);
        PropertyReader reader = new YamlFileReader(file);

        // when
        Object result = reader.getObject("");

        // then
        assertThat(result, instanceOf(Map.class));
        assertThat(((Map<String, ?>) result).keySet(), containsInAnyOrder("test", "sample", "version", "features"));
    }

    @Test
    public void shouldReturnNullForUnknownPath() {
        // given
        File file = copyFileFromResources(COMPLETE_FILE);
        PropertyReader reader = new YamlFileReader(file);

        // when / then
        assertThat(reader.getObject("sample.ratio.wrong.dunno"), nullValue());
        assertThat(reader.getObject(TestConfiguration.RATIO_ORDER.getPath() + ".child"), nullValue());
    }

    private File copyFileFromResources(String path) {
        return TestUtils.copyFileFromResources(path, temporaryFolder);
    }
}
