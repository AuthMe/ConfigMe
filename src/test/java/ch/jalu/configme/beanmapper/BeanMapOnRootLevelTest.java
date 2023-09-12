package ch.jalu.configme.beanmapper;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.TestUtils;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.MapProperty;
import ch.jalu.configme.properties.types.BeanPropertyType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.jalu.configme.properties.PropertyInitializer.mapProperty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

/**
 * Tests that a map property can be used in the root with a bean type as values. Ensures also that the default value
 * is used when the file is entirely empty, vs. allowing empty maps if the YAML file is literally defined as an empty
 * map (namely with <code>{}</code> as contents).
 *
 * @see <a href="https://github.com/AuthMe/ConfigMe/issues/191">Issue #191</a>
 */
class BeanMapOnRootLevelTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldUseDefaultValueIfFileIsEmpty() throws IOException {
        // given
        String yaml = "";
        Path tempFile = TestUtils.createTemporaryFile(tempDir);
        Files.write(tempFile, yaml.getBytes());

        // when
        SettingsManager settingsManager = SettingsManagerBuilder.withYamlFile(tempFile)
            .configurationData(TestSettingsHolder.class)
            .create();

        // then
        Map<String, Info> result = settingsManager.getProperty(TestSettingsHolder.INFO);
        assertThat(result.keySet(), contains("basic"));
        assertThat(result.get("basic").getName(), equalTo("hello"));
        assertThat(result.get("basic").getLore(), contains("world", "moon"));
    }

    @Test
    void shouldHaveEmptyMapIfFileIsEmptyMap() throws IOException {
        // given
        String yaml = "{}";
        Path tempFile = TestUtils.createTemporaryFile(tempDir);
        Files.write(tempFile, yaml.getBytes());

        // when
        SettingsManager settingsManager = SettingsManagerBuilder.withYamlFile(tempFile)
            .configurationData(TestSettingsHolder.class)
            .create();

        // then
        Map<String, Info> result = settingsManager.getProperty(TestSettingsHolder.INFO);
        assertThat(result, anEmptyMap());
    }

    @Test
    void shouldLoadMapFromFile() throws IOException {
        // given
        String yaml = "medium:\n"
            + "\n    name: \"med\"\n"
            + "\n    lore:\n"
            + "\n      - \"Test\""
            + "\n      - \"Toast\"";
        Path tempFile = TestUtils.createTemporaryFile(tempDir);
        Files.write(tempFile, yaml.getBytes());

        // when
        SettingsManager settingsManager = SettingsManagerBuilder.withYamlFile(tempFile)
            .configurationData(TestSettingsHolder.class)
            .create();

        // then
        Map<String, Info> result = settingsManager.getProperty(TestSettingsHolder.INFO);
        assertThat(result.keySet(), contains("medium"));
        assertThat(result.get("medium").getName(), equalTo("med"));
        assertThat(result.get("medium").getLore(), contains("Test", "Toast"));
    }

    @Test
    void shouldExportValuesAsEmptyMap() throws IOException {
        // given
        String yaml = "medium:\n"
            + "\n    name: \"med\"\n"
            + "\n    lore:\n"
            + "\n      - \"Test\""
            + "\n      - \"Toast\"";
        Path tempFile = TestUtils.createTemporaryFile(tempDir);
        Files.write(tempFile, yaml.getBytes());

        SettingsManager settingsManager = SettingsManagerBuilder.withYamlFile(tempFile)
            .configurationData(TestSettingsHolder.class)
            .create();

        // when
        settingsManager.setProperty(TestSettingsHolder.INFO, new HashMap<>());
        settingsManager.save();

        // then
        assertThat(Files.readAllLines(tempFile), contains("{}"));
    }

    @Test
    void shouldExportValuesAsEmptyMapIncludingComments() throws IOException {
        // given
        String yaml = "medium:\n"
            + "\n    name: \"med\"\n"
            + "\n    lore:\n"
            + "\n      - \"Test\""
            + "\n      - \"Toast\"";
        Path tempFile = TestUtils.createTemporaryFile(tempDir);
        Files.write(tempFile, yaml.getBytes());

        SettingsManager settingsManager = SettingsManagerBuilder.withYamlFile(tempFile)
            .configurationData(TestSettingsHolderWithRootComment.class)
            .create();

        // when
        settingsManager.setProperty(TestSettingsHolder.INFO, new HashMap<>());
        settingsManager.save();

        // then
        assertThat(Files.readAllLines(tempFile),
            contains("# Define your info here below", "# You can add as many entries as you want", "{}"));
    }

    public static class TestSettingsHolder implements SettingsHolder {

        public static final MapProperty<Info> INFO = mapProperty(BeanPropertyType.of(Info.class))
            .path("")
            .addToDefaultValue("basic", new Info("hello", Arrays.asList("world", "moon")))
            .build();

        private TestSettingsHolder() {
        }
    }

    public static final class TestSettingsHolderWithRootComment extends TestSettingsHolder {

        @Override
        public void registerComments(CommentsConfiguration conf) {
            conf.setComment("",
                "Define your info here below",
                "You can add as many entries as you want");
        }
    }

    public static final class Info {

        private String name;
        private List<String> lore;

        public Info(String name, List<String> lore) {
            this.name = name;
            this.lore = lore;
        }

        public Info() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getLore() {
            return lore;
        }

        public void setLore(List<String> lore) {
            this.lore = lore;
        }
    }
}
