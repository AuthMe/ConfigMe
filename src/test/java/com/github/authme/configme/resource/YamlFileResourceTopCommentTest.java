package com.github.authme.configme.resource;

import com.github.authme.configme.SettingsManager;
import com.github.authme.configme.migration.PlainMigrationService;
import com.github.authme.configme.resource.rootcommentsamples.GroupPropertyHolder;
import com.github.authme.configme.resource.rootcommentsamples.TestConfig;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.hamcrest.Matchers.equalTo;
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
        new SettingsManager(resource, new PlainMigrationService(), GroupPropertyHolder.class);

        // then
        assertThat(new String(Files.readAllBytes(file.toPath())), equalTo(
            "\n"
            + "# Group configuration number\n"
            + "defaultGamemode: 'CREATIVE'\n"
            + "worlds: \n"
            + "- 'world'"
        ));
    }

    @Test
    public void shouldIncludeRootCommentFromSectionCommentsMethod() throws IOException {
        // given
        PropertyResource resource = new YamlFileResource(file);

        // when
        new SettingsManager(resource, new PlainMigrationService(), TestConfig.class);

        // then
        assertThat(new String(Files.readAllBytes(file.toPath())), equalTo(
            "\n"
            + "# Root comment\n"
            + "# 'some' Section\n"
            + "# Explanation for 'some'\n"
            + "some:\n"
            + "    # Integer property\n"
            + "    test: 4\n"
            + "    # Other header\n"
            + "    other:\n"
            + "        property: 'hello'"
        ));
    }
}
