package ch.jalu.configme.neo.beanmapper;

import ch.jalu.configme.neo.beanmapper.command.Command;
import ch.jalu.configme.neo.beanmapper.command.CommandConfig;
import ch.jalu.configme.neo.beanmapper.command.ExecutionDetails;
import ch.jalu.configme.neo.beanmapper.command.Executor;
import ch.jalu.configme.neo.beanmapper.command.optionalproperties.ComplexCommand;
import ch.jalu.configme.neo.beanmapper.command.optionalproperties.ComplexCommandConfig;
import ch.jalu.configme.neo.beanmapper.command.optionalproperties.ComplexOptionalTypeConfig;
import ch.jalu.configme.neo.beanmapper.typeissues.GenericCollection;
import ch.jalu.configme.neo.beanmapper.typeissues.MapWithNonStringKeys;
import ch.jalu.configme.neo.beanmapper.typeissues.UnsupportedCollection;
import ch.jalu.configme.neo.beanmapper.typeissues.UntypedCollection;
import ch.jalu.configme.neo.beanmapper.typeissues.UntypedMap;
import ch.jalu.configme.neo.beanmapper.worldgroup.GameMode;
import ch.jalu.configme.neo.beanmapper.worldgroup.Group;
import ch.jalu.configme.neo.beanmapper.worldgroup.WorldGroupConfig;
import ch.jalu.configme.neo.resource.PropertyReader;
import ch.jalu.configme.neo.resource.PropertyResource;
import ch.jalu.configme.neo.resource.YamlFileResource;
import ch.jalu.configme.neo.samples.TestEnum;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Objects;
import java.util.Optional;

import static ch.jalu.configme.TestUtils.getJarFile;
import static ch.jalu.configme.TestUtils.verifyException;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link MapperImpl}.
 */
public class MapperImplTest {

    // TODO: tests for toExportValue

    @Test
    public void shouldCreateWorldGroups() {
        // given
        PropertyReader reader = createReaderFromFile("/beanmapper/worlds.yml");
        MapperImpl mapperImpl = new MapperImpl();
        String path = "";

        // when
        WorldGroupConfig result = mapperImpl.convertToBean(reader, path, WorldGroupConfig.class);

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
        PropertyReader reader = createReaderFromFile("/beanmapper/commands.yml");
        MapperImpl mapperImpl = new MapperImpl();

        // when
        CommandConfig config = mapperImpl.convertToBean(reader, "commandconfig", CommandConfig.class);

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
        PropertyReader reader = createReaderFromFile("/beanmapper/worlds_invalid.yml");
        MapperImpl mapperImpl = new MapperImpl();

        // when
        WorldGroupConfig config = mapperImpl.convertToBean(reader, "", WorldGroupConfig.class);

        // then
        assertThat(config, not(nullValue()));
        assertThat(config.getGroups().keySet(), contains("creative"));
    }

//
//    @Test
//    public void shouldHandleInvalidErrors() {
//        // given
//        PropertyResource resource = new YamlFileResource(getJarFile("/beanmapper/commands_invalid.yml"));
//        MapperImpl mapper = ConfigMeMapper.getSingleton();
//
//        // when
//        CommandConfig config = mapper.convertToBean("commandconfig", resource, CommandConfig.class);
//
//        // then
//        assertThat(config, not(nullValue()));
//        assertThat(config.getCommands().keySet(), contains("refresh", "open", "cancel"));
//        Command cancelCommand = config.getCommands().get("cancel");
//        assertThat(cancelCommand.getArguments(), empty());
//        assertThat(cancelCommand.getExecution().getPrivileges(), hasSize(4));
//    }
//
    @Test
    public void shouldReturnNullForUnavailableSection() {
        // given
        PropertyReader reader = createReaderFromFile("/beanmapper/commands.yml");
        MapperImpl mapper = new MapperImpl();

        // when
        CommandConfig result = mapper.convertToBean(reader, "does-not-exist", CommandConfig.class);

        // then
        assertThat(result, nullValue());
    }

    @Test
    public void shouldThrowForMapWithNonStringKeyType() {
        // given
        PropertyReader reader = createReaderFromFile("/beanmapper/typeissues/mapconfig.yml");
        MapperImpl mapper = new MapperImpl();

        // when / then
        verifyException(
            () -> mapper.convertToBean(reader, "", MapWithNonStringKeys.class),
            ConfigMeMapperException.class,
            "The key type of maps may only be of String type");
    }

    @Test
    public void shouldThrowForUnsupportedCollectionType() {
        // given
        PropertyReader reader = createReaderFromFile("/beanmapper/typeissues/collectionconfig.yml");
        MapperImpl mapper = new MapperImpl();

        // when / then
        verifyException(
            () -> mapper.convertToBean(reader, "", UnsupportedCollection.class),
            ConfigMeMapperException.class,
            "Unsupported collection type");
    }

    @Test
    @Ignore // TODO .
    public void shouldThrowForUntypedCollection() {
        // given
        PropertyReader reader = createReaderFromFile("/beanmapper/typeissues/collectionconfig.yml");
        MapperImpl mapper = new MapperImpl();

        // when / then
        verifyException(
            () -> mapper.convertToBean(reader, "", UntypedCollection.class),
            ConfigMeMapperException.class,
            "has no generic type");
    }

    @Test
    @Ignore // TODO .
    public void shouldThrowForUntypedMap() {
        // given
        PropertyReader reader = createReaderFromFile("/beanmapper/typeissues/mapconfig.yml");
        MapperImpl mapper = new MapperImpl();

        // when / then
        verifyException(
            () -> mapper.convertToBean(reader, "", UntypedMap.class),
            ConfigMeMapperException.class,
            "type '?' at index 1 not recognized");
    }

    @Test
    @Ignore // TODO .
    public void shouldThrowForCollectionWithGenerics() {
        // given
        PropertyReader reader = createReaderFromFile("/beanmapper/typeissues/collectionconfig.yml");
        MapperImpl mapper = new MapperImpl();

        // when / then
        verifyException(
            () -> mapper.convertToBean(reader, "", GenericCollection.class),
            ConfigMeMapperException.class,
            "at index 0 not recognized");
    }

    @Test
    public void shouldReturnNullForUnmappableMandatoryField() {
        // given
        PropertyReader reader = createReaderFromFile("/beanmapper/commands_invalid_2.yml");
        MapperImpl mapper = new MapperImpl();

        // when
        CommandConfig result = mapper.convertToBean(reader, "commandconfig", CommandConfig.class);

        // then
        assertThat(result, nullValue());
    }

    @Test
    public void shouldReturnNullForMissingSection() {
        // given
        PropertyReader reader = createReaderFromFile("/empty_file.yml");
        MapperImpl mapper = new MapperImpl();

        // when
        CommandConfig result = mapper.convertToBean(reader, "commands", CommandConfig.class);

        // then
        assertThat(result, nullValue());
    }

    @Test
    public void shouldHandleEmptyOptionalFields() {
        // given
        PropertyReader reader = createReaderFromFile("/beanmapper/commands.yml");
        MapperImpl mapper = new MapperImpl();

        // when
        ComplexCommandConfig result = mapper.convertToBean(reader, "commandconfig", ComplexCommandConfig.class);

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
        PropertyReader reader = createReaderFromFile("/beanmapper/optionalproperties/complex-commands.yml");
        MapperImpl mapper = new MapperImpl();

        // when
        ComplexCommandConfig result = mapper.convertToBean(reader, "commandconfig", ComplexCommandConfig.class);

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

    @Test
    @Ignore // TODO .
    public void shouldHandleComplexOptionalType() {
        // given
        PropertyReader reader = createReaderFromFile("/beanmapper/commands.yml");
        MapperImpl mapper = new MapperImpl();

        // when
        ComplexOptionalTypeConfig result = mapper.convertToBean(reader, "", ComplexOptionalTypeConfig.class);

        // then
        assertThat(result, not(nullValue()));
        assertThat(result.getCommandconfig().isPresent(), equalTo(true));
        assertThat(result.getCommandconfig().get().keySet(), containsInAnyOrder("duration", "commands"));
    }

    @Test
    public void shouldReturnEmptyOptionalForEmptyFile() {
        // given
        PropertyReader reader = createReaderFromFile("/empty_file.yml");
        MapperImpl mapper = new MapperImpl();

        // when
        ComplexOptionalTypeConfig result = mapper.convertToBean(reader, "", ComplexOptionalTypeConfig.class);

        // then
        assertThat(result, not(nullValue()));
        assertThat(result.getCommandconfig(), equalTo(Optional.empty()));
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

    private static PropertyReader createReaderFromFile(String file) {
        PropertyResource resource = new YamlFileResource(getJarFile(file));
        return resource.createReader();
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
