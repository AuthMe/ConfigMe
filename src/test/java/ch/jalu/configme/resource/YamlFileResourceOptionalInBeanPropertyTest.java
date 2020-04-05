package ch.jalu.configme.resource;


import ch.jalu.configme.beanmapper.Mapper;
import ch.jalu.configme.beanmapper.MapperImpl;
import ch.jalu.configme.beanmapper.command.ExecutionDetails;
import ch.jalu.configme.beanmapper.command.Executor;
import ch.jalu.configme.beanmapper.command.optionalproperties.ComplexCommand;
import ch.jalu.configme.beanmapper.command.optionalproperties.ComplexCommandConfig;
import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.properties.BeanProperty;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;

import static ch.jalu.configme.TestUtils.copyFileFromResources;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

/**
 * Tests that bean properties with {@code Optional} fields can be exported properly.
 *
 * @see <a href="https://github.com/AuthMe/ConfigMe/issues/51">Issue #51</a>
 */
class YamlFileResourceOptionalInBeanPropertyTest {

    private static BeanProperty<ComplexCommandConfig> commandConfigProperty = new BeanProperty<>(
        ComplexCommandConfig.class, "commandconfig", new ComplexCommandConfig());

    @TempDir
    public Path temporaryFolder;

    @Test
    void shouldSaveOptionalFieldsProperly() {
        // given
        Path file = copyFileFromResources("/beanmapper/optionalproperties/complex-commands.yml", temporaryFolder);
        PropertyResource resource = new YamlFileResource(file);
        PropertyReader reader = resource.createReader();
        Mapper mapper = new MapperImpl();
        ComplexCommandConfig result = mapper.convertToBean(reader.getObject("commandconfig"), ComplexCommandConfig.class, new ConvertErrorRecorder());
        result.getCommands().put("shutdown", createShutdownCommand());
        ConfigurationData configurationData = createConfigurationData();
        configurationData.setValue(commandConfigProperty, result);

        // when
        resource.exportProperties(configurationData);

        // then
        PropertyResource resourceAfterSave = new YamlFileResource(file);
        ConvertErrorRecorder errorRecorderAfterSave = new ConvertErrorRecorder();
        ComplexCommandConfig commandConfig = mapper.convertToBean(
            resourceAfterSave.createReader().getObject("commandconfig"), ComplexCommandConfig.class, errorRecorderAfterSave);
        assertThat(errorRecorderAfterSave.isFullyValid(), equalTo(true));

        assertThat(commandConfig.getCommands().keySet(),
            containsInAnyOrder("shutdown", "greet", "block_invalid", "log_admin", "launch"));
        ComplexCommand shutDownCmd = commandConfig.getCommands().get("shutdown");
        assertThat(shutDownCmd.getCommand(), equalTo("app shutdown"));
        assertThat(shutDownCmd.getDoubleOptional(), equalTo(Optional.of(3.0)));
        assertThat(shutDownCmd.getExecution().isOptional(), equalTo(true));

        assertThat(commandConfig.getCommands().get("greet").getCommand(), equalTo("hello $user"));
        assertThat(commandConfig.getCommands().get("block_invalid").getExecution().getPrivileges(), contains("system.kick"));
    }

    private static ConfigurationData createConfigurationData() {
        return ConfigurationDataBuilder.createConfiguration(Collections.singletonList(commandConfigProperty));
    }

    private static ComplexCommand createShutdownCommand() {
        ExecutionDetails executionDetails = new ExecutionDetails();
        executionDetails.setExecutor(Executor.CONSOLE);
        executionDetails.setOptional(true);
        executionDetails.setImportance(1.0);
        executionDetails.setPrivileges(Collections.emptySet());

        ComplexCommand command = new ComplexCommand();
        command.setExecution(executionDetails);
        command.setCommand("app shutdown");
        command.setDoubleOptional(Optional.of(3.0));
        command.setNameHasLength(Optional.empty());
        command.setNameStartsWith(Optional.empty());
        command.setTestEnumProperty(Optional.empty());
        return command;
    }
}
