package ch.jalu.configme.beanmapper;

import ch.jalu.configme.beanmapper.command.Command;
import ch.jalu.configme.beanmapper.command.CommandConfig;
import ch.jalu.configme.beanmapper.command.ExecutionDetails;
import ch.jalu.configme.beanmapper.command.Executor;
import ch.jalu.configme.beanmapper.command.optionalproperties.ComplexCommand;
import ch.jalu.configme.beanmapper.command.optionalproperties.ComplexCommandConfig;
import ch.jalu.configme.beanmapper.command.optionalproperties.ComplexOptionalTypeConfig;
import ch.jalu.configme.beanmapper.leafvaluehandler.LeafValueHandler;
import ch.jalu.configme.beanmapper.propertydescription.BeanDescriptionFactory;
import ch.jalu.configme.beanmapper.typeissues.GenericCollection;
import ch.jalu.configme.beanmapper.typeissues.MapWithNonStringKeys;
import ch.jalu.configme.beanmapper.typeissues.UnsupportedCollection;
import ch.jalu.configme.beanmapper.typeissues.UntypedCollection;
import ch.jalu.configme.beanmapper.typeissues.UntypedMap;
import ch.jalu.configme.beanmapper.worldgroup.GameMode;
import ch.jalu.configme.beanmapper.worldgroup.Group;
import ch.jalu.configme.beanmapper.worldgroup.WorldGroupConfig;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.resource.PropertyReader;
import ch.jalu.configme.resource.YamlFileReader;
import ch.jalu.configme.samples.TestEnum;
import ch.jalu.configme.utils.TypeInformation;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

import static ch.jalu.configme.TestUtils.getJarPath;
import static ch.jalu.configme.TestUtils.verifyException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link MapperImpl}.
 */
class MapperImplTest {

    @Test
    void shouldCreateWorldGroups() {
        // given
        PropertyReader reader = createReaderFromFile("/beanmapper/worlds.yml");
        MapperImpl mapperImpl = new MapperImpl();
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when
        WorldGroupConfig result =
            mapperImpl.convertToBean(reader.getObject(""), WorldGroupConfig.class, errorRecorder);

        // then
        assertThat(errorRecorder.isFullyValid(), equalTo(true));
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
    void shouldCreateCommands() {
        // given
        PropertyReader reader = createReaderFromFile("/beanmapper/commands.yml");
        MapperImpl mapperImpl = new MapperImpl();
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when
        CommandConfig config =
            mapperImpl.convertToBean(reader.getObject("commandconfig"), CommandConfig.class, errorRecorder);

        // then
        assertThat(errorRecorder.isFullyValid(), equalTo(false));

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
    void shouldSkipInvalidEntry() {
        // given
        PropertyReader reader = createReaderFromFile("/beanmapper/worlds_invalid.yml");
        MapperImpl mapperImpl = new MapperImpl();
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when
        WorldGroupConfig config = mapperImpl.convertToBean(
            reader.getObject(""), WorldGroupConfig.class, errorRecorder);

        // then
        assertThat(errorRecorder.isFullyValid(), equalTo(false));
        assertThat(config, not(nullValue()));
        assertThat(config.getGroups().keySet(), contains("creative"));
    }

    @Test
    void shouldHandleInvalidErrors() {
        // given
        PropertyReader reader = createReaderFromFile("/beanmapper/commands_invalid.yml");
        MapperImpl mapper = new MapperImpl();
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when
        CommandConfig config = mapper.convertToBean(reader.getObject("commandconfig"), CommandConfig.class, errorRecorder);

        // then
        assertThat(errorRecorder.isFullyValid(), equalTo(false));

        assertThat(config, not(nullValue()));
        assertThat(config.getCommands().keySet(), contains("refresh", "open", "cancel"));
        Command cancelCommand = config.getCommands().get("cancel");
        assertThat(cancelCommand.getArguments(), empty());
        assertThat(cancelCommand.getExecution().getPrivileges(), contains("action.cancel", "true", "1.23"));
    }

    @Test
    void shouldReturnNullForUnavailableSection() {
        // given
        MapperImpl mapper = new MapperImpl();
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when
        CommandConfig result = mapper.convertToBean(null, CommandConfig.class, errorRecorder);

        // then
        assertThat(result, nullValue());
    }

    @Test
    void shouldThrowForMapWithNonStringKeyType() {
        // given
        PropertyReader reader = createReaderFromFile("/beanmapper/typeissues/mapconfig.yml");
        MapperImpl mapper = new MapperImpl();

        // when / then
        verifyException(
            () -> mapper.convertToBean(reader.getObject(""), MapWithNonStringKeys.class, new ConvertErrorRecorder()),
            ConfigMeMapperException.class,
            "The key type of maps may only be of String type");
    }

    @Test
    void shouldThrowForUnsupportedCollectionType() {
        // given
        PropertyReader reader = createReaderFromFile("/beanmapper/typeissues/collectionconfig.yml");
        MapperImpl mapper = new MapperImpl();

        // when / then
        verifyException(
            () -> mapper.convertToBean(reader.getObject(""), UnsupportedCollection.class, new ConvertErrorRecorder()),
            ConfigMeMapperException.class,
            "Unsupported collection type");
    }

    @Test
    void shouldThrowForUntypedCollection() {
        // given
        PropertyReader reader = createReaderFromFile("/beanmapper/typeissues/collectionconfig.yml");
        MapperImpl mapper = new MapperImpl();

        // when / then
        verifyException(
            () -> mapper.convertToBean(reader.getObject(""), UntypedCollection.class, new ConvertErrorRecorder()),
            ConfigMeMapperException.class,
            "The generic type 0 is not well defined, for mapping of: [Path: 'collection', type: 'interface java.util.List']");
    }

    @Test
    void shouldThrowForUntypedMap() {
        // given
        PropertyReader reader = createReaderFromFile("/beanmapper/typeissues/mapconfig.yml");
        MapperImpl mapper = new MapperImpl();

        // when / then
        verifyException(
            () -> mapper.convertToBean(reader.getObject(""), UntypedMap.class, new ConvertErrorRecorder()),
            ConfigMeMapperException.class,
            "The generic type 1 is not well defined, for mapping of: [Path: 'map', type: 'java.util.Map<java.lang.String, ?>']");
    }

    @Test
    void shouldThrowForCollectionWithGenerics() {
        // given
        PropertyReader reader = createReaderFromFile("/beanmapper/typeissues/collectionconfig.yml");
        MapperImpl mapper = new MapperImpl();

        // when / then
        verifyException(
            () -> mapper.convertToBean(reader.getObject(""), GenericCollection.class, new ConvertErrorRecorder()),
            ConfigMeMapperException.class,
            "The generic type 0 is not well defined, for mapping of: [Path: 'collection', type: 'java.util.List<? extends java.lang.String>']");
    }

    @Test
    void shouldThrowForUnsupportedMapType() {
        // given
        MapperImpl mapper = new MapperImpl();
        Class<?> type = new HashMap() { }.getClass();
        MappingContext context = createContextWithType(type);

        // when / then
        verifyException(
            () -> mapper.createMapMatchingType(context),
            ConfigMeMapperException.class,
            "Unsupported map type '" + type + "', for mapping of: [" + context.createDescription() + "]");
    }

    @Test
    void shouldCreateCorrectMapType() {
        // given
        MapperImpl mapper = new MapperImpl();
        MappingContext interfaceCtx = createContextWithType(Map.class);
        MappingContext hashCtx = createContextWithType(HashMap.class);
        MappingContext navigableCtx = createContextWithType(NavigableMap.class);
        MappingContext treeCtx = createContextWithType(TreeMap.class);

        // when / then
        assertThat(mapper.createMapMatchingType(interfaceCtx), instanceOf(LinkedHashMap.class));
        assertThat(mapper.createMapMatchingType(hashCtx), instanceOf(LinkedHashMap.class));
        assertThat(mapper.createMapMatchingType(navigableCtx), instanceOf(TreeMap.class));
        assertThat(mapper.createMapMatchingType(treeCtx), instanceOf(TreeMap.class));
    }

    @Test
    void shouldReturnNullForUnmappableMandatoryField() {
        // given
        PropertyReader reader = createReaderFromFile("/beanmapper/commands_invalid_2.yml");
        MapperImpl mapper = new MapperImpl();
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when
        CommandConfig result = mapper.convertToBean(reader.getObject("commandconfig"), CommandConfig.class, errorRecorder);

        // then
        assertThat(result, nullValue());
    }

    @Test
    void shouldReturnNullForMissingSection() {
        // given
        PropertyReader reader = createReaderFromFile("/empty_file.yml");
        MapperImpl mapper = new MapperImpl();
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when
        CommandConfig result = mapper.convertToBean(reader.getObject("commands"), CommandConfig.class, errorRecorder);

        // then
        assertThat(result, nullValue());
    }

    @Test
    void shouldHandleEmptyOptionalFields() {
        // given
        PropertyReader reader = createReaderFromFile("/beanmapper/commands.yml");
        MapperImpl mapper = new MapperImpl();
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when
        ComplexCommandConfig result =
            mapper.convertToBean(reader.getObject("commandconfig"), ComplexCommandConfig.class, errorRecorder);

        // then
        assertThat(errorRecorder.isFullyValid(), equalTo(false)); // e.g. save.arguments are missing
        assertThat(result, not(nullValue()));
        assertThat(result.getCommands().keySet(), contains("save", "refresh", "open"));
        assertAllOptionalFieldsEmpty(result.getCommands().get("save"));
        assertAllOptionalFieldsEmpty(result.getCommands().get("refresh"));
        assertAllOptionalFieldsEmpty(result.getCommands().get("open"));
    }

    @Test
    void shouldLoadConfigWithOptionalProperties() {
        // given
        PropertyReader reader = createReaderFromFile("/beanmapper/optionalproperties/complex-commands.yml");
        MapperImpl mapper = new MapperImpl();
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when
        ComplexCommandConfig result = mapper.convertToBean(
            reader.getObject("commandconfig"), ComplexCommandConfig.class, errorRecorder);

        // then
        assertThat(errorRecorder.isFullyValid(), equalTo(false));

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
    void shouldHandleComplexOptionalType() {
        // given
        PropertyReader reader = createReaderFromFile("/beanmapper/commands.yml");
        MapperImpl mapper = new MapperImpl();
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when
        ComplexOptionalTypeConfig result =
            mapper.convertToBean(reader.getObject(""), ComplexOptionalTypeConfig.class, errorRecorder);

        // then
        assertThat(errorRecorder.isFullyValid(), equalTo(true));
        assertThat(result, not(nullValue()));
        assertThat(result.getCommandconfig().isPresent(), equalTo(true));
        assertThat(result.getCommandconfig().get().keySet(), containsInAnyOrder("duration", "commands"));
    }

    @Test
    void shouldReturnEmptyOptionalForEmptyFile() {
        // given
        PropertyReader reader = createReaderFromFile("/empty_file.yml");
        MapperImpl mapper = new MapperImpl();
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when
        ComplexOptionalTypeConfig result =
            mapper.convertToBean(reader.getObject(""), ComplexOptionalTypeConfig.class, errorRecorder);

        // then
        assertThat(errorRecorder.isFullyValid(), equalTo(true));
        assertThat(result, not(nullValue()));
        assertThat(result.getCommandconfig(), equalTo(Optional.empty()));
    }

    @Test
    void shouldInvokeDefaultConstructor() {
        // given
        MapperImpl mapper = new MapperImpl();

        // when
        Object command = mapper.createBeanMatchingType(createContextWithType(Command.class));

        // then
        assertThat(command, instanceOf(Command.class));
    }

    @Test
    void shouldForwardException() {
        // given
        MapperImpl mapper = new MapperImpl();

        // when
        ConfigMeException e = assertThrows(ConfigMeException.class,
            () -> mapper.createBeanMatchingType(createContextWithType(Iterable.class)));

        // then
        assertThat(e.getMessage(), containsString("It is required to have a default constructor"));
        assertThat(e.getCause(), instanceOf(NoSuchMethodException.class));
    }

    @Test
    void shouldReturnFields() {
        // given
        BeanDescriptionFactory descriptionFactory = mock(BeanDescriptionFactory.class);
        LeafValueHandler leafValueHandler = mock(LeafValueHandler.class);
        MapperImpl mapper = new MapperImpl(descriptionFactory, leafValueHandler);

        // when
        BeanDescriptionFactory returnedDescriptionFactory = mapper.getBeanDescriptionFactory();
        LeafValueHandler returnedLeafValueHandler = mapper.getLeafValueHandler();

        // then
        assertThat(returnedDescriptionFactory, sameInstance(descriptionFactory));
        assertThat(returnedLeafValueHandler, sameInstance(leafValueHandler));
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
        return new YamlFileReader(getJarPath(file));
    }

    private static MappingContext createContextWithType(Class<?> clazz) {
        TypeInformation type = new TypeInformation(clazz);
        MappingContextImpl root = MappingContextImpl.createRoot(type, new ConvertErrorRecorder());
        return root.createChild("path.in.test", type);
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
