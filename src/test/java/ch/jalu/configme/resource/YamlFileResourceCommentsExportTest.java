package ch.jalu.configme.resource;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.TestUtils;
import ch.jalu.configme.beanmapper.command.Command;
import ch.jalu.configme.beanmapper.command.ExecutionDetails;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.properties.BeanProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.convertresult.ValueWithComments;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static ch.jalu.configme.beanmapper.command.Executor.CONSOLE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

/**
 * Test cases for {@link YamlFileResource} and the handling of comments from different sources.
 */
class YamlFileResourceCommentsExportTest {

    @TempDir
    public Path tempFolder;

    @Test
    void shouldCombineConfiguredCommentWithExportValueComment() throws IOException {
        // given
        Path file = TestUtils.createTemporaryFile(tempFolder);

        Command command = new Command();
        command.setCommand("help");
        command.setExecution(new ExecutionDetails(CONSOLE, 0.8, false, "op"));

        YamlFileResource yamlResource = new YamlFileResource(file);
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(RootPropertyHolder.class);
        configurationData.setValue(RootPropertyHolder.COMMAND, command);

        // when
        yamlResource.exportProperties(configurationData);

        // then
        assertThat(Files.readAllLines(file), contains(
            "# Define the command here.",
            "",
            "# Fill out all values.",
            "# By default, help is run",
            "command: help",
            "arguments: []",
            "execution:",
            "    executor: CONSOLE",
            "    optional: false",
            "    # The higher the number, the more important",
            "    importance: 0.8",
            "    privileges:",
            "    - op"
        ));
    }

    @Test
    void shouldCombineConfiguredCommentWithExportValueComment2() throws IOException {
        // given
        Path file = TestUtils.createTemporaryFile(tempFolder);

        Command command = new Command();
        command.setCommand("help");
        command.setExecution(new ExecutionDetails(CONSOLE, 0.8, false, "op"));

        YamlFileResource yamlResource = new YamlFileResource(file);
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(RootPropertyHolder2.class);
        configurationData.setValue(RootPropertyHolder2.COMMAND2, command);

        // when
        yamlResource.exportProperties(configurationData);

        // then
        assertThat(Files.readAllLines(file), contains(
            "# Command to run",
            "",
            "# Don't forget to save!",
            "# This command is run on startup",
            "command: help",
            "arguments: []",
            "execution:",
            "    executor: CONSOLE",
            "    optional: false",
            "    # The higher the number, the more important",
            "    importance: 0.8",
            "    privileges:",
            "    - op"
        ));
    }

    public static final class RootPropertyHolder implements SettingsHolder {

        public static final Property<Command> COMMAND = new BeanWithExportCommentProperty<>(
            Command.class, "", new Command(), "By default, help is run");

        @Override
        public void registerComments(@NotNull CommentsConfiguration conf) {
            conf.setComment("", "Define the command here.", "\n", "Fill out all values.");
        }
    }

    public static final class RootPropertyHolder2 implements SettingsHolder {

        @Comment({"Command to run", "\n", "Don't forget to save!"})
        public static final Property<Command> COMMAND2 = new BeanWithExportCommentProperty<>(
            Command.class, "", new Command(), "This command is run on startup");

    }

    public static final class BeanWithExportCommentProperty<T> extends BeanProperty<T> {

        private final String comment;

        public BeanWithExportCommentProperty(@NotNull Class<T> beanType, @NotNull String path, @NotNull T defaultValue,
                                             @NotNull String comment) {
            super(beanType, path, defaultValue);
            this.comment = comment;
        }

        @Override
        public @Nullable Object toExportValue(@NotNull T value) {
            Object exportValue = super.toExportValue(value);
            if (exportValue == null) {
                return null;
            }
            return new ValueWithComments(exportValue, Collections.singletonList(comment));
        }
    }
}
