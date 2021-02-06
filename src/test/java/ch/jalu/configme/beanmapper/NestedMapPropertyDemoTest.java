package ch.jalu.configme.beanmapper;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.TestUtils;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.PropertyInitializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

/**
 * Demonstrates how maps can be nested into each other by using a bean property.
 *
 * @see <a href="https://github.com/AuthMe/ConfigMe/issues/179">#179: Create a Map of Map</a>
 */
class NestedMapPropertyDemoTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldLoadMap() throws IOException {
        // given
        String yaml = "path: "
            + "\n  map1:"
            + "\n    child-map1:"
            + "\n      text: Text"
            + "\n    child-map2:"
            + "\n      text: Map 2 text"
            + "\n  map2:"
            + "\n    title:"
            + "\n      text: Third entry";
        Path tempFile = TestUtils.createTemporaryFile(tempDir);
        Files.write(tempFile, yaml.getBytes());

        // when
        SettingsManager settingsManager = SettingsManagerBuilder.withYamlFile(tempFile)
            .configurationData(MyTestSettings.class)
            .create();

        // then
        TextEntryHolder texts = settingsManager.getProperty(MyTestSettings.TEXTS);
        assertThat(texts.getPath().keySet(), contains("map1", "map2"));
        assertThat(texts.getPath().get("map1").keySet(), contains("child-map1", "child-map2"));
        assertThat(texts.getPath().get("map1").get("child-map1").getText(), equalTo("Text"));
        assertThat(texts.getPath().get("map1").get("child-map2").getText(), equalTo("Map 2 text"));
    }

    public static class MyTestSettings implements SettingsHolder {

        public static final Property<TextEntryHolder> TEXTS =
            PropertyInitializer.newBeanProperty(TextEntryHolder.class, "", new TextEntryHolder());

        private MyTestSettings() {
        }
    }

    public static class TextEntryHolder {

        private Map<String, Map<String, TextEntry>> path;

        public Map<String, Map<String, TextEntry>> getPath() {
            return path;
        }

        public void setPath(Map<String, Map<String, TextEntry>> textEntries) {
            this.path = textEntries;
        }
    }

    public static class TextEntry {

        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
