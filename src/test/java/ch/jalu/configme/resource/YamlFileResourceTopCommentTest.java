package ch.jalu.configme.resource;

import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.resource.rootcommentsamples.GroupPropertyHolder;
import ch.jalu.configme.resource.rootcommentsamples.TestConfig;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

/**
 * Tests that comments on the root path are included into the YAML export.
 *
 * @see <a href="https://github.com/AuthMe/ConfigMe/issues/25">Issue #25</a>
 */
public class YamlFileResourceTopCommentTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File file;

    @Before
    public void initEmptyFile() throws IOException {
        file = temporaryFolder.newFile();
    }

    @Test
    public void shouldIncludeCommentFromAnnotation() throws IOException {
        // given
        PropertyResource resource = new YamlFileResource(file);

        // when
        SettingsManagerBuilder.withResource(resource)
            .configurationData(GroupPropertyHolder.class)
            .create();

        // then
        assertThat(Files.readAllLines(file.toPath()), contains(
            "",
            "# Group configuration number",
            "default-gamemode: CREATIVE",
            "worlds: ",
            "- world"
        ));
    }

    @Test
    public void shouldIncludeRootCommentFromSectionCommentsMethod() throws IOException {
        // given
        PropertyResource resource = new YamlFileResource(file);

        // when
        SettingsManagerBuilder.withResource(resource)
            .configurationData(TestConfig.class)
            .create();

        // then
        assertThat(Files.readAllLines(file.toPath()), contains(
            "",
            "# Root comment",
            "# 'some' Section",
            "# Explanation for 'some'",
            "some:",
            "    # Integer property",
            "    test: 4",
            "    # Other header",
            "    other:",
            "        property: hello"
        ));
    }
}
