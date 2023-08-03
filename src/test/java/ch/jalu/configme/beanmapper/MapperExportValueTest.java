package ch.jalu.configme.beanmapper;

import ch.jalu.configme.beanmapper.command.Command;
import ch.jalu.configme.beanmapper.command.CommandConfig;
import ch.jalu.configme.beanmapper.command.ExecutionDetails;
import ch.jalu.configme.properties.convertresult.ValueWithComments;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static ch.jalu.configme.beanmapper.command.Executor.CONSOLE;
import static ch.jalu.configme.beanmapper.command.Executor.USER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

/**
 * Test for {@link MapperImpl}, specifically for {@link MapperImpl#toExportValue}.
 */
class MapperExportValueTest {

    @Test
    void shouldCreatePropertyEntriesForCommandConfig() {
        // given
        ExecutionDetails kickExecution = new ExecutionDetails(CONSOLE, 0.4, true, "player.kick", "is.admin");
        Command kickCommand = createCommand("kick", kickExecution, "name");
        ExecutionDetails msgExecution = new ExecutionDetails(USER, 1.0, false, "player.msg");
        Command msgCommand = createCommand("msg", msgExecution, "name", "message");
        ExecutionDetails vanishExecution = new ExecutionDetails(USER, 0.1, true, "player.vanish");
        Command vanishCommand = createCommand("vanish", vanishExecution);

        CommandConfig config = new CommandConfig();
        config.setDuration(11);
        config.setCommands(new LinkedHashMap<>());
        config.getCommands().put("kick", kickCommand);
        config.getCommands().put("msg", msgCommand);
        config.getCommands().put("vanish", vanishCommand);
        MapperImpl mapper = new MapperImpl();

        // when
        Object exportValue = mapper.toExportValue(config);

        // then
        assertThat(exportValue, instanceOf(Map.class));
        Map<?, ?> values = (Map) exportValue;
        assertThat(values.keySet(), containsInAnyOrder("commands", "duration"));
        assertThat(values.get("duration"), equalTo(11));

        assertThat(values.get("commands"), instanceOf(Map.class));
        Map<?, ?> commandsMap = (Map) values.get("commands");
        assertThat(commandsMap.keySet(), contains("kick", "msg", "vanish"));
        checkCommandDetails(commandsMap.get("kick"), kickCommand);
        checkCommandDetails(commandsMap.get("msg"), msgCommand);
        checkCommandDetails(commandsMap.get("vanish"), vanishCommand);
    }

    @Test
    void shouldAddEmptyMapAsLeafProperty() {
        // given
        CommandConfig config = new CommandConfig();
        config.setCommands(new HashMap<>());
        config.setDuration(14);
        MapperImpl mapper = new MapperImpl();

        // when
        Object exportValue = mapper.toExportValue(config);

        // then
        assertThat(exportValue, instanceOf(Map.class));
        Map<?, ?> values = (Map) exportValue;
        assertThat(values.keySet(), containsInAnyOrder("commands", "duration"));
        assertThat(values.get("commands"), instanceOf(Map.class));
        assertThat(((Map) values.get("commands")).isEmpty(), equalTo(true));
        assertThat(values.get("duration"), equalTo(14));
    }

    @Test
    void shouldSkipNullValue() {
        // given
        Command command = new Command();
        command.setCommand("ping");
        command.setArguments(Arrays.asList("test", "toast", "taste"));
        command.setExecution(null);
        MapperImpl mapper = new MapperImpl();

        // when
        Object exportValue = mapper.toExportValue(command);

        // then
        assertThat(exportValue, instanceOf(Map.class));
        Map<?, ?> values = (Map) exportValue;
        assertThat(values.keySet(), containsInAnyOrder("command", "arguments"));
        assertThat(values.get("command"), equalTo(command.getCommand()));
        assertThat(values.get("arguments"), equalTo(command.getArguments()));
    }

    private static void checkCommandDetails(Object commandValues, Command command) {
        assertThat(commandValues, instanceOf(Map.class));
        Map<?, ?> values = (Map) commandValues;
        assertThat(values.keySet(), containsInAnyOrder("command", "arguments", "execution"));

        assertThat(values.get("command"), equalTo(command.getCommand()));
        assertThat(values.get("arguments"), equalTo(command.getArguments()));
        assertThat(values.get("execution"), instanceOf(Map.class));
        Map<?, ?> executionMap = (Map) values.get("execution");
        assertThat(executionMap.keySet(), containsInAnyOrder("executor", "optional", "importance", "privileges"));
        assertThat(executionMap.get("executor"), equalTo(command.getExecution().getExecutor().name()));
        assertThat(executionMap.get("optional"), equalTo(command.getExecution().isOptional()));

        assertThat(executionMap.get("importance"), instanceOf(ValueWithComments.class));
        ValueWithComments importance = (ValueWithComments) executionMap.get("importance");
        assertThat(importance.getComments(), contains("The higher the number, the more important"));
        assertThat(importance.getValue(), equalTo(command.getExecution().getImportance()));

        assertThat(executionMap.get("privileges"), instanceOf(Collection.class));
        assertThat((Collection<?>) executionMap.get("privileges"), contains(command.getExecution().getPrivileges().toArray()));
    }

    private static Command createCommand(String name, ExecutionDetails executionDetails, String... arguments) {
        Command command = new Command();
        command.setCommand(name);
        command.setExecution(executionDetails);
        command.setArguments(Arrays.asList(arguments));
        return command;
    }
}
