package com.github.authme.configme.beanmapper;

import com.github.authme.configme.TestUtils;
import com.github.authme.configme.beanmapper.command.Command;
import com.github.authme.configme.beanmapper.command.CommandConfig;
import com.github.authme.configme.beanmapper.command.Executor;
import com.github.authme.configme.knownproperties.PropertyEntry;
import com.github.authme.configme.resource.PropertyResource;
import com.github.authme.configme.resource.YamlFileResource;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link BeanProperty} and its integration in {@link YamlFileResource}.
 */
public class BeanPropertyTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldExportPropertyAndReimport() throws IOException {
        // given
        BeanProperty<CommandConfig> property =
            new BeanProperty<>(CommandConfig.class, "commandconfig", new CommandConfig());
        PropertyEntry entry = new PropertyEntry(property);
        File configFile = copyFileToTemporaryFolder("/beanmapper/commands.yml");
        PropertyResource resource = new YamlFileResource(configFile);

        // when
        resource.exportProperties(Collections.singletonList(entry));
        resource = new YamlFileResource(configFile);

        // then
        CommandConfig config = property.getFromResource(resource);
        assertThat(config.getCommands().keySet(), contains("save", "refresh", "open"));
        Command refreshCommand = config.getCommands().get("refresh");
        assertThat(refreshCommand.getExecution().getPrivileges(), contains("page.view", "action.refresh"));
        assertThat(refreshCommand.getExecution().getExecutor(), equalTo(Executor.CONSOLE));
        assertThat(refreshCommand.getExecution().isOptional(), equalTo(true));
        Command openCommand = config.getCommands().get("open");
        assertThat(openCommand.getArguments(), contains("f", "x", "z"));
        assertThat(openCommand.getExecution().isOptional(), equalTo(false));
        assertThat(config.getDuration(), equalTo(13));
    }

    private File copyFileToTemporaryFolder(String path) {
        Path jarPath = TestUtils.getJarPath(path);
        try {
            File tempFile = temporaryFolder.newFile();
            Files.copy(jarPath, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return tempFile;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
