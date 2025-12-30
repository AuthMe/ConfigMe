package ch.jalu.configme.resource;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.TestUtils;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.StringProperty;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class YamlFileResourceFooterCommentTest {

    @TempDir
    public Path tempFolder;

    @Test
    void shouldIncludeFooterCommentInYamlWithMultipleProperties() throws IOException {
        // given
        Path file = TestUtils.createTemporaryFile(tempFolder);

        YamlFileResource yamlResource = new YamlFileResource(file);
        ConfigurationData configurationData =
            ConfigurationDataBuilder.createConfiguration(RootPropertyHolderWithFooter.class);
        configurationData.setValue(RootPropertyHolderWithFooter.PROPERTY_NAME, "ExampleProperty");
        configurationData.setValue(RootPropertyHolderWithFooter.PROPERTY_NAME2, "ExampleProperty2");

        // when
        yamlResource.exportProperties(configurationData);

        // then
        List<String> lines = Files.readAllLines(file);
        assertThat(lines, contains(
            "# Header comment line 1",
            "# Header comment line 2",
            "PropertyName: ExampleProperty",
            "PropertyName2: ExampleProperty2",
            "# Footer comment line 1",
            "# Footer comment line 2"
        ));
    }


    @Test
    void shouldIncludeFooterCommentForSingleRootProperty() throws IOException {
        // given
        Path file = TestUtils.createTemporaryFile(tempFolder);

        YamlFileResource yamlResource = new YamlFileResource(file);
        ConfigurationData configurationData =
            ConfigurationDataBuilder.createConfiguration(RootPropertyHolderSingleRootProperty.class);
        configurationData.setValue(RootPropertyHolderSingleRootProperty.ROOT_PROPERTY, "RootValue");

        // when
        yamlResource.exportProperties(configurationData);

        // then
        List<String> lines = Files.readAllLines(file);
        assertThat(lines, contains(
            "# Header comment line 1",
            "# Header comment line 2",
            "RootValue",
            "# Footer comment line 1",
            "# Footer comment line 2"
        ));
    }

    public static final class RootPropertyHolderSingleRootProperty implements SettingsHolder {

        public static final Property<String> ROOT_PROPERTY =
            new StringProperty("", "DefaultValue");

        private RootPropertyHolderSingleRootProperty() {
        }

        @Override
        public void registerComments(@NotNull CommentsConfiguration conf) {
            conf.setHeaderComments("Header comment line 1", "Header comment line 2");
            conf.setFooterComments("Footer comment line 1", "Footer comment line 2");
        }
    }

    public static final class RootPropertyHolderWithFooter implements SettingsHolder {

        public static final Property<String> PROPERTY_NAME =
            new StringProperty("PropertyName", "DefaultPropertyValue");

        public static final Property<String> PROPERTY_NAME2 =
            new StringProperty("PropertyName2", "DefaultPropertyValue2");

        private RootPropertyHolderWithFooter() {
        }

        @Override
        public void registerComments(@NotNull CommentsConfiguration conf) {
            conf.setHeaderComments("Header comment line 1", "Header comment line 2");
            conf.setFooterComments("Footer comment line 1", "Footer comment line 2");
        }
    }

}
