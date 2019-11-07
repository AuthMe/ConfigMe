package ch.jalu.configme.resource;

import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.resource.rootcommentsamples.GroupPropertyHolder;
import ch.jalu.configme.resource.rootcommentsamples.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static ch.jalu.configme.TestUtils.createTemporaryFile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

/**
 * Tests that comments on the root path are included into the YAML export.
 *
 * @see <a href="https://github.com/AuthMe/ConfigMe/issues/25">Issue #25</a>
 */
class YamlFileResourceTopCommentTest {

    @TempDir
    public Path temporaryFolder;

    private Path file;

    @BeforeEach
    void initEmptyFile() {
        file = createTemporaryFile(temporaryFolder);
    }

    @Test
    void shouldIncludeCommentFromAnnotation() throws IOException {
        // given
        PropertyResource resource = new YamlFileResource(file.toFile());

        // when
        SettingsManagerBuilder.withResource(resource)
            .configurationData(GroupPropertyHolder.class)
            .useDefaultMigrationService()
            .create();

        // then
        assertThat(Files.readAllLines(file), contains(
            "# Group configuration number",
            "worlds: ",
            "- world",
            "default-gamemode: CREATIVE"
        ));
    }

    @Test
    void shouldIncludeRootCommentFromSectionCommentsMethod() throws IOException {
        // given
        PropertyResource resource = new YamlFileResource(file.toFile());

        // when
        SettingsManagerBuilder.withResource(resource)
            .configurationData(TestConfig.class)
            .useDefaultMigrationService()
            .create();

        // then
        assertThat(Files.readAllLines(file), contains(
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
