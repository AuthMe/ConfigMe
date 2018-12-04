package ch.jalu.configme.resource;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.properties.Property;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

/**
 * Tests the integration of comments and configurable new lines
 * ({@link YamlFileResourceOptions#numberOfLinesBeforeFunction}) in {@link YamlFileResource}.
 */
public class YamlFileResourceNewLineTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldExportWithRootAndFirstComment() throws IOException {
        // given
        File file = temporaryFolder.newFile();
        YamlFileResource resource = new YamlFileResource(file, optionsWithLinesFunction());
        ConfigurationData configurationData = configurationDataWithComments(true, true);
        configurationData.initializeValues(resource.createReader());

        // when
        resource.exportProperties(configurationData);

        // then
        assertThat(Files.readAllLines(file.toPath()), contains(
            "# Root comment",
            "# First comm",
            "# First comm 2",
            "first: 1",
            "",
            "second:",
            "    a: 2",
            "    # b comm",
            "    b: 3"));
    }

    @Test
    public void shouldExportWithoutRootAndWithFirstComment() throws IOException {
        // given
        File file = temporaryFolder.newFile();
        YamlFileResource resource = new YamlFileResource(file, optionsWithLinesFunction());
        ConfigurationData configurationData = configurationDataWithComments(false, true);
        configurationData.initializeValues(resource.createReader());

        // when
        resource.exportProperties(configurationData);

        // then
        assertThat(Files.readAllLines(file.toPath()), contains(
            "# First comm",
            "# First comm 2",
            "first: 1",
            "",
            "second:",
            "    a: 2",
            "    # b comm",
            "    b: 3"));
    }

    @Test
    public void shouldExportWithRootAndWithoutFirstComment() throws IOException {
        // given
        File file = temporaryFolder.newFile();
        YamlFileResource resource = new YamlFileResource(file, optionsWithLinesFunction());
        ConfigurationData configurationData = configurationDataWithComments(true, false);
        configurationData.initializeValues(resource.createReader());

        // when
        resource.exportProperties(configurationData);

        // then
        assertThat(Files.readAllLines(file.toPath()), contains(
            "# Root comment",
            "first: 1",
            "",
            "second:",
            "    a: 2",
            "    # b comm",
            "    b: 3"));
    }

    @Test
    public void shouldExportWithoutRootAndFirstComment() throws IOException {
        // given
        File file = temporaryFolder.newFile();
        YamlFileResource resource = new YamlFileResource(file, optionsWithLinesFunction());
        ConfigurationData configurationData = configurationDataWithComments(false, false);
        configurationData.initializeValues(resource.createReader());

        // when
        resource.exportProperties(configurationData);

        // then
        assertThat(Files.readAllLines(file.toPath()), contains(
            "first: 1",
            "",
            "second:",
            "    a: 2",
            "    # b comm",
            "    b: 3"));
    }

    @Test
    public void shouldExportWithRootCommentAndNewLineEverywhere() throws IOException {
        // given
        File file = temporaryFolder.newFile();
        YamlFileResourceOptions options = YamlFileResourceOptions.builder()
            .numberOfLinesBeforeFunction(e -> !e.isFirstElement() && e.isFirstOfGroup() ? 1 : 0)
            .build();
        YamlFileResource resource = new YamlFileResource(file, options);
        ConfigurationData configurationData = configurationDataWithComments(true, false);
        configurationData.initializeValues(resource.createReader());

        // when
        resource.exportProperties(configurationData);

        // then
        assertThat(Files.readAllLines(file.toPath()), contains(
            "# Root comment",
            "first: 1",
            "",
            "second:",
            "    a: 2",
            "",
            "    # b comm",
            "    b: 3"));
    }

    private static YamlFileResourceOptions optionsWithLinesFunction() {
        return YamlFileResourceOptions.builder()
            .numberOfLinesBeforeFunction(e -> !e.isFirstElement() && e.getIndentationLevel() == 0 ? 1 : 0)
            .build();
    }

    private static ConfigurationData configurationDataWithComments(boolean hasRootComment,
                                                                   boolean hasFirstPropertyComment) {
        CommentsConfiguration commentsConfiguration = new CommentsConfiguration();
        if (hasRootComment) {
            commentsConfiguration.setComment("", "Root comment");
        }
        if (hasFirstPropertyComment) {
            commentsConfiguration.setComment("first", "First comm", "First comm 2");
        }
        commentsConfiguration.setComment("second.b", "b comm");

        List<Property<?>> properties = ConfigurationDataBuilder.createConfiguration(TestConfiguration.class).getProperties();
        return ConfigurationDataBuilder.createConfiguration(properties, commentsConfiguration);
    }

    public static class TestConfiguration implements SettingsHolder {

        public static final Property<Integer> FIRST = newProperty("first", 1);

        public static final Property<Integer> SECOND_A = newProperty("second.a", 2);

        public static final Property<Integer> SECOND_B = newProperty("second.b", 3);

    }
}
