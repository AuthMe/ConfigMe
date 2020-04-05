package ch.jalu.configme.properties;

import ch.jalu.configme.beanmapper.DefaultMapper;
import ch.jalu.configme.beanmapper.Mapper;
import ch.jalu.configme.beanmapper.command.Command;
import ch.jalu.configme.beanmapper.command.CommandConfig;
import ch.jalu.configme.beanmapper.command.Executor;
import ch.jalu.configme.beanmapper.worldgroup.WorldGroupConfig;
import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.resource.PropertyReader;
import ch.jalu.configme.resource.PropertyResource;
import ch.jalu.configme.resource.YamlFileResource;
import ch.jalu.configme.utils.TypeInformation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.HashMap;

import static ch.jalu.configme.TestUtils.copyFileFromResources;
import static ch.jalu.configme.TestUtils.verifyException;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link BeanProperty} and its integration with {@link YamlFileResource}.
 */
class BeanPropertyTest {

    @TempDir
    public Path temporaryFolder;

    @Test
    void shouldExportPropertyAndReimport() {
        // given
        BeanProperty<CommandConfig> property =
            new BeanProperty<>(CommandConfig.class, "commandconfig", new CommandConfig());
        Path configFile = copyFileFromResources("/beanmapper/commands.yml", temporaryFolder);
        PropertyResource resource = new YamlFileResource(configFile);
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(singletonList(property));
        configurationData.initializeValues(resource.createReader());

        // when
        resource.exportProperties(configurationData);

        // then
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();
        CommandConfig config = property.getFromReader(resource.createReader(), errorRecorder);
        assertThat(errorRecorder.isFullyValid(), equalTo(true));

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

    @Test
    void shouldExportBeanPropertyAtRootProperly() {
        // given
        BeanProperty<CommandConfig> property =
            new BeanProperty<>(CommandConfig.class, "", new CommandConfig());
        Path configFile = copyFileFromResources("/beanmapper/commands_root_path.yml", temporaryFolder);
        PropertyResource resource = new YamlFileResource(configFile);
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(singletonList(property));
        configurationData.initializeValues(resource.createReader());

        // when
        resource.exportProperties(configurationData);

        // then
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();
        CommandConfig config = property.getFromReader(resource.createReader(), errorRecorder);
        assertThat(errorRecorder.isFullyValid(), equalTo(true));

        assertThat(config.getCommands().keySet(), contains("save"));
        Command saveCommand = config.getCommands().get("save");
        assertThat(saveCommand.getExecution().getPrivileges(), contains("action.open", "action.save"));
        assertThat(saveCommand.getExecution().getExecutor(), equalTo(Executor.CONSOLE));
    }

    @Test
    void shouldUseCustomMapper() {
        // given
        Mapper mapper = mock(Mapper.class);
        String path = "cnf";
        BeanProperty<WorldGroupConfig> property = new BeanProperty<>(
            WorldGroupConfig.class, path, new WorldGroupConfig(), mapper);
        PropertyReader reader = mock(PropertyReader.class);
        Object value = new Object();
        given(reader.getObject(path)).willReturn(value);
        WorldGroupConfig groupConfig = new WorldGroupConfig();
        given(mapper.convertToBean(eq(value), eq(new TypeInformation(WorldGroupConfig.class)), any(ConvertErrorRecorder.class)))
            .willReturn(groupConfig);

        // when
        WorldGroupConfig result = property.determineValue(reader).getValue();

        // then
        assertThat(result, equalTo(groupConfig));
    }

    @Test
    void shouldAllowInstantiationWithGenerics() throws NoSuchFieldException {
        // given
        Type stringComparable = TestFields.class.getDeclaredField("comparable").getGenericType();


        // when
        BeanProperty<Comparable<String>> property = new BeanProperty<>(new TypeInformation(stringComparable),
            "path.test", "defaultValue", DefaultMapper.getInstance());

        // then
        assertThat(property.getDefaultValue(), equalTo("defaultValue"));
    }

    @Test
    void shouldThrowForObviouslyWrongDefaultValue() throws NoSuchFieldException {
        // given
        Type stringComparable = TestFields.class.getDeclaredField("comparable").getGenericType();

        // when / then
        verifyException(() -> new BeanProperty<>(new TypeInformation(stringComparable),
            "path.test", new HashMap<>(), DefaultMapper.getInstance()),
            ConfigMeException.class,
            "does not match bean type");
    }

    private static final class TestFields {
        private Comparable<String> comparable;
    }
}
