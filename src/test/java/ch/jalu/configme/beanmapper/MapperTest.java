package ch.jalu.configme.beanmapper;

import ch.jalu.configme.beanmapper.command.Command;
import ch.jalu.configme.beanmapper.command.CommandConfig;
import ch.jalu.configme.beanmapper.command.ExecutionDetails;
import ch.jalu.configme.beanmapper.command.Executor;
import ch.jalu.configme.beanmapper.command.optionalproperties.ComplexCommand;
import ch.jalu.configme.beanmapper.command.optionalproperties.ComplexCommandConfig;
import ch.jalu.configme.beanmapper.transformer.Transformers;
import ch.jalu.configme.beanmapper.typeissues.GenericCollection;
import ch.jalu.configme.beanmapper.typeissues.MapWithNonStringKeys;
import ch.jalu.configme.beanmapper.typeissues.UnsupportedCollection;
import ch.jalu.configme.beanmapper.typeissues.UntypedCollection;
import ch.jalu.configme.beanmapper.typeissues.UntypedMap;
import ch.jalu.configme.beanmapper.worldgroup.GameMode;
import ch.jalu.configme.beanmapper.worldgroup.Group;
import ch.jalu.configme.beanmapper.worldgroup.WorldGroupConfig;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.resource.PropertyResource;
import ch.jalu.configme.resource.YamlFileResource;
import ch.jalu.configme.samples.TestEnum;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import java.util.Objects;
import java.util.Optional;

import static ch.jalu.configme.TestUtils.getJarFile;
import static ch.jalu.configme.TestUtils.verifyException;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link Mapper}.
 */
public class MapperTest {

    @Test
    public void shouldCreateWorldGroups() {
        // given
        PropertyResource resource = new YamlFileResource(getJarFile("/beanmapper/worlds.yml"));
        Mapper mapper = ConfigMeMapper.getSingleton();
        String path = "";

        // when
        WorldGroupConfig result = mapper.convertToBean(path, resource, WorldGroupConfig.class);

        // then
        assertThat(result, not(nullValue()));
        assertThat(result.getGroups().keySet(), contains("default", "creative"));
        Group survival = result.getGroups().get("default");
        assertThat(survival.getWorlds(), contains("world", "world_nether", "world_the_end"));
        assertThat(survival.getDefaultGamemode(), equalTo(GameMode.SURVIVAL));
        Group creative = result.getGroups().get("creative");
        assertThat(creative.getWorlds(), contains("creative"));
        assertThat(creative.getDefaultGamemode(), equalTo(GameMode.CREATIVE));
    }

    @Test
    public void shouldCreateCommands() {
        // given
        PropertyResource resource = new YamlFileResource(getJarFile("/beanmapper/commands.yml"));
        Mapper mapper = ConfigMeMapper.getSingleton();

        // when
        CommandConfig config = mapper.convertToBean("commandconfig", resource, CommandConfig.class);

        // then
        assertThat(config.getDuration(), equalTo(13));
        assertThat(config.getCommands().keySet(), contains("save", "refresh", "open"));

        Command saveCommand = config.getCommands().get("save");
        assertThat(saveCommand.getCommand(), equalTo("save"));
        assertThat(saveCommand.getArguments(), empty());
        assertThat(saveCommand, hasExecution(Executor.CONSOLE, false, 1.0));
        assertThat(saveCommand.getExecution().getPrivileges(), contains("action.save"));

        Command refreshCommand = config.getCommands().get("refresh");
        assertThat(refreshCommand.getCommand(), equalTo("refresh"));
        assertThat(refreshCommand.getArguments(), contains("force", "async"));
        assertThat(refreshCommand, hasExecution(Executor.CONSOLE, true, 0.4));
        assertThat(refreshCommand.getExecution().getPrivileges(), contains("page.view", "action.refresh"));

        Command openCommand = config.getCommands().get("open");
        assertThat(openCommand.getCommand(), equalTo("open"));
        assertThat(openCommand.getArguments(), contains("f", "x", "z"));
        assertThat(openCommand, hasExecution(Executor.USER, false, 0.7));
        assertThat(openCommand.getExecution().getPrivileges(), contains("page.view"));
    }

    @Test
    public void shouldSkipInvalidEntry() {
        // given
        PropertyResource resource = new YamlFileResource(getJarFile("/beanmapper/worlds_invalid.yml"));
        Mapper mapper = ConfigMeMapper.getSingleton();

        // when
        WorldGroupConfig config = mapper.convertToBean("", resource, WorldGroupConfig.class);

        // then
        assertThat(config, not(nullValue()));
        assertThat(config.getGroups().keySet(), contains("creative"));
    }

    @Test(expected = ConfigMeMapperException.class)
    public void shouldThrowForInvalidValue() {
        // given
        PropertyResource resource = new YamlFileResource(getJarFile("/beanmapper/worlds_invalid.yml"));
        Mapper mapper = new Mapper(MappingErrorHandler.Impl.THROWING, new BeanDescriptionFactory(), Transformers.getDefaultTransformers());

        // when
        mapper.convertToBean("", resource, WorldGroupConfig.class);

        // then - expect exception to be thrown
    }

    @Test
    public void shouldHandleInvalidErrors() {
        // given
        PropertyResource resource = new YamlFileResource(getJarFile("/beanmapper/commands_invalid.yml"));
        Mapper mapper = ConfigMeMapper.getSingleton();

        // when
        CommandConfig config = mapper.convertToBean("commandconfig", resource, CommandConfig.class);

        // then
        assertThat(config, not(nullValue()));
        assertThat(config.getCommands().keySet(), contains("refresh", "open", "cancel"));
        Command cancelCommand = config.getCommands().get("cancel");
        assertThat(cancelCommand.getArguments(), empty());
        assertThat(cancelCommand.getExecution().getPrivileges(), hasSize(4));
    }

    @Test
    public void shouldReturnNullForUnavailableSection() {
        // given
        PropertyResource resource = new YamlFileResource(getJarFile("/beanmapper/commands.yml"));
        Mapper mapper = ConfigMeMapper.getSingleton();

        // when
        CommandConfig result = mapper.convertToBean("does-not-exist", resource, CommandConfig.class);

        // then
        assertThat(result, nullValue());
    }

    @Test
    public void shouldThrowForMapWithNonStringKeyType() {
        // given
        PropertyResource resource = new YamlFileResource(getJarFile("/beanmapper/typeissues/mapconfig.yml"));
        Mapper mapper = ConfigMeMapper.getSingleton();

        // when / then
        verifyException(
            () -> mapper.convertToBean("", resource, MapWithNonStringKeys.class),
            ConfigMeMapperException.class,
            "The key type of maps may only be of String type");
    }

    @Test
    public void shouldThrowForUnsupportedCollectionType() {
        // given
        PropertyResource resource = new YamlFileResource(getJarFile("/beanmapper/typeissues/collectionconfig.yml"));
        Mapper mapper = ConfigMeMapper.getSingleton();

        // when / then
        verifyException(
            () -> mapper.convertToBean("", resource, UnsupportedCollection.class),
            ConfigMeMapperException.class,
            "Unsupported collection type");
    }

    @Test
    public void shouldThrowForUntypedCollection() {
        // given
        PropertyResource resource = new YamlFileResource(getJarFile("/beanmapper/typeissues/collectionconfig.yml"));
        Mapper mapper = ConfigMeMapper.getSingleton();

        // when / then
        verifyException(
            () -> mapper.convertToBean("", resource, UntypedCollection.class),
            ConfigMeException.class,
            "has no generic type");
    }

    @Test
    public void shouldThrowForUntypedMap() {
        // given
        PropertyResource resource = new YamlFileResource(getJarFile("/beanmapper/typeissues/mapconfig.yml"));
        Mapper mapper = ConfigMeMapper.getSingleton();

        // when / then
        verifyException(
            () -> mapper.convertToBean("", resource, UntypedMap.class),
            ConfigMeException.class,
            "does not have a concrete generic type");
    }

    @Test
    public void shouldThrowForCollectionWithGenerics() {
        // given
        PropertyResource resource = new YamlFileResource(getJarFile("/beanmapper/typeissues/collectionconfig.yml"));
        Mapper mapper = ConfigMeMapper.getSingleton();

        // when / then
        verifyException(
            () -> mapper.convertToBean("", resource, GenericCollection.class),
            ConfigMeException.class,
            "does not have a concrete generic type");
    }

    @Test
    public void shouldReturnNullForUnmappableMandatoryField() {
        // given
        PropertyResource resource = new YamlFileResource(getJarFile("/beanmapper/commands_invalid_2.yml"));
        Mapper mapper = ConfigMeMapper.getSingleton();

        // when
        CommandConfig result = mapper.convertToBean("commandconfig", resource, CommandConfig.class);

        // then
        assertThat(result, nullValue());
    }

    @Test
    public void shouldReturnNullForMissingSection() {
        // given
        PropertyResource resource = new YamlFileResource(getJarFile("/empty_file.yml"));
        Mapper mapper = ConfigMeMapper.getSingleton();

        // when
        CommandConfig result = mapper.convertToBean("commands", resource, CommandConfig.class);

        // then
        assertThat(result, nullValue());
    }

    @Test
    public void shouldHandleEmptyOptionalFields() {
        // given
        PropertyResource resource = new YamlFileResource(getJarFile("/beanmapper/commands.yml"));
        Mapper mapper = ConfigMeMapper.getSingleton();

        // when
        ComplexCommandConfig result = mapper.convertToBean("commandconfig", resource, ComplexCommandConfig.class);

        // then
        assertThat(result, not(nullValue()));
        assertThat(result.getCommands().keySet(), contains("save", "refresh", "open"));
        assertAllOptionalFieldsEmpty(result.getCommands().get("save"));
        assertAllOptionalFieldsEmpty(result.getCommands().get("refresh"));
        assertAllOptionalFieldsEmpty(result.getCommands().get("open"));
    }

    @Test
    public void shouldLoadConfigWithOptionalProperties() {
        // given
        PropertyResource resource = new YamlFileResource(getJarFile("/beanmapper/optionalproperties/complex-commands.yml"));
        Mapper mapper = ConfigMeMapper.getSingleton();

        // when
        ComplexCommandConfig result = mapper.convertToBean("commandconfig", resource, ComplexCommandConfig.class);

        // then
        assertThat(result, not(nullValue()));
        assertThat(result.getCommands().keySet(), contains("greet", "block_invalid", "log_admin", "launch"));

        ComplexCommand greet = result.getCommands().get("greet");
        assertThat(greet, hasExecution(Executor.CONSOLE, false, 0.5));
        assertThat(greet.getExecution().getPrivileges(), contains("user.greet"));
        assertThat(greet.getNameStartsWith(), equalTo(Optional.of("user_")));
        assertAreAllEmpty(greet.getDoubleOptional(), greet.getNameHasLength(), greet.getTestEnumProperty());

        ComplexCommand block = result.getCommands().get("block_invalid");
        assertThat(block, hasExecution(Executor.CONSOLE, false, 1.0));
        assertThat(block.getNameHasLength(), equalTo(Optional.of(80)));
        assertThat(block.getTestEnumProperty(), equalTo(Optional.of(TestEnum.SECOND)));
        assertAreAllEmpty(block.getNameStartsWith(), block.getDoubleOptional());

        ComplexCommand log = result.getCommands().get("log_admin");
        assertThat(log.getCommand(), equalTo("log $name"));
        assertThat(log, hasExecution(Executor.CONSOLE, true, 0.8));
        assertThat(log.getDoubleOptional(), equalTo(Optional.of(0.531)));
        assertAreAllEmpty(log.getTestEnumProperty(), log.getNameHasLength(), log.getNameStartsWith());

        ComplexCommand launch = result.getCommands().get("launch");
        assertThat(launch, hasExecution(Executor.USER, false, 1.0));
        assertThat(launch.getTestEnumProperty(), equalTo(Optional.of(TestEnum.FOURTH)));
        assertAreAllEmpty(launch.getDoubleOptional(), launch.getNameHasLength(), launch.getNameStartsWith());
    }

    private static void assertAllOptionalFieldsEmpty(ComplexCommand complexCommand) {
        assertAreAllEmpty(
            complexCommand.getNameStartsWith(),
            complexCommand.getNameHasLength(),
            complexCommand.getDoubleOptional(),
            complexCommand.getTestEnumProperty());
    }

    private static void assertAreAllEmpty(Optional<?>... optionals) {
        for (Optional<?> o : optionals) {
            assertThat(o, equalTo(Optional.empty()));
        }
    }

    private static Matcher<Command> hasExecution(Executor executor, boolean optional, Double importance) {
        return new TypeSafeMatcher<Command>() {
            @Override
            protected boolean matchesSafely(Command command) {
                ExecutionDetails details = command.getExecution();
                return Objects.equals(details.isOptional(), optional)
                    && Objects.equals(details.getExecutor(), executor)
                    && Objects.equals(details.getImportance(), importance);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(describeCommandExecution(executor, optional, importance));
            }

            @Override
            public void describeMismatchSafely(Command command, Description description) {
                final ExecutionDetails details = command.getExecution();
                if (details == null) {
                    description.appendText("Command with executorDetails=null");
                } else {
                    description.appendText(describeCommandExecution(
                        details.getExecutor(), details.isOptional(), details.getImportance()));
                }
            }

            private String describeCommandExecution(Executor executor, boolean optional, Double importance) {
                return String.format("Command with execution [executor:%s, optional:%b, importance:%f]",
                    executor, optional, importance);
            }
        };
    }
}
