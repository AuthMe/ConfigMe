package ch.jalu.configme.beanmapper;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.TestUtils;
import ch.jalu.configme.properties.MapProperty;
import ch.jalu.configme.properties.types.BeanPropertyType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static ch.jalu.configme.properties.PropertyInitializer.mapProperty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

/**
 * Tests that a map property can be used in the root with a bean type as values.
 *
 * @see <a href="https://github.com/AuthMe/ConfigMe/issues/191">Issue #191</a>
 */
class BeanMapOnRootLevelTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldLoadMap() throws IOException {
        // given
        String yaml = "basic:\n"
            + "\n    name: \"hello\"\n"
            + "\n    lore:\n"
            + "\n      - \"world\""
            + "\n      - \"moon\"";
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

    public static final class TestSettingsHolder implements SettingsHolder {

        public static final MapProperty<Info> INFO = mapProperty(BeanPropertyType.of(Info.class))
            .path("")
            .defaultEntry("default", new Info("default", "def"))
            .build();

        private TestSettingsHolder() {
        }
    }

    public static final class Info {

        private String name;
        private List<String> lore;

        public Info(String name, String... lore) {
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
