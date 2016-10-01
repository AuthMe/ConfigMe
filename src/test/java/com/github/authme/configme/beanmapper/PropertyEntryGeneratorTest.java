package com.github.authme.configme.beanmapper;

import com.github.authme.configme.beanmapper.command.Command;
import com.github.authme.configme.beanmapper.command.CommandConfig;
import com.github.authme.configme.beanmapper.command.ExecutionDetails;
import com.github.authme.configme.beanmapper.command.Executor;
import com.github.authme.configme.knownproperties.PropertyEntry;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import static com.github.authme.configme.beanmapper.command.Executor.CONSOLE;
import static com.github.authme.configme.beanmapper.command.Executor.USER;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link PropertyEntryGenerator}.
 */
public class PropertyEntryGeneratorTest {

    @Test
    public void shouldCreatePropertyEntriesForCommandConfig() {
        // given
        ExecutionDetails kickExecution = createExecution(CONSOLE, 0.4, true, "player.kick", "is.admin");
        Command kickCommand = createCommand("kick", kickExecution, "name");
        ExecutionDetails msgExecution = createExecution(USER, 1.0, false, "player.msg");
        Command msgCommand = createCommand("msg", msgExecution, "name", "message");
        ExecutionDetails vanishExecution = createExecution(USER, 0.1, true, "player.vanish");
        Command vanishCommand = createCommand("vanish", vanishExecution);

        CommandConfig config = new CommandConfig();
        config.setDuration(11);
        config.setCommands(new LinkedHashMap<String, Command>());
        config.getCommands().put("kick", kickCommand);
        config.getCommands().put("msg", msgCommand);
        config.getCommands().put("vanish", vanishCommand);
        BeanProperty<CommandConfig> property = new BeanProperty<>(CommandConfig.class, "cmd", new CommandConfig());

        // when
        List<PropertyEntry> entries = new PropertyEntryGenerator().generate(property, config);

        // then
        List<String> expectedPaths = expectedCommandPaths("kick", "msg", "vanish");
        expectedPaths.add("cmd.duration");
        assertThat(toPaths(entries), containsInAnyOrder(expectedPaths.toArray()));
        checkExecutionDetails(entries, "kick", kickExecution);
        checkExecutionDetails(entries, "msg", msgExecution);
        checkExecutionDetails(entries, "vanish", vanishExecution);
        assertEquals(getPropertyValue(entries, "cmd.duration"), 11);
    }

    private static List<String> toPaths(List<PropertyEntry> entries) {
        List<String> paths = new ArrayList<>(entries.size());
        for (PropertyEntry entry : entries) {
            paths.add(entry.getProperty().getPath());
        }
        return paths;
    }

    private static List<String> expectedCommandPaths(String... commands) {
        String root = "cmd.commands.";
        String[] children = {"command", "arguments", "execution.executor", "execution.optional",
            "execution.importance", "execution.privileges"};
        List<String> paths = new ArrayList<>(children.length * commands.length);
        for (String command : commands) {
            String cmdRoot = root + command + ".";
            for (String child : children) {
                paths.add(cmdRoot + child);
            }
        }
        return paths;
    }

    @SuppressWarnings("unchecked")
    private static void checkExecutionDetails(List<PropertyEntry> entries, String name, ExecutionDetails details) {
        String root = "cmd.commands." + name + ".execution.";
        assertEquals(getPropertyValue(entries, root + "executor"), details.getExecutor());
        assertEquals(getPropertyValue(entries, root + "optional"), details.isOptional());
        assertEquals(getPropertyValue(entries, root + "importance"), details.getImportance());
        assertThat((List<String>) getPropertyValue(entries, root + "privileges"),
            contains(details.getPrivileges().toArray()));
    }

    private static Object getPropertyValue(List<PropertyEntry> entries, String path) {
        for (PropertyEntry entry : entries) {
            if (entry.getProperty().getPath().equals(path)) {
                return entry.getProperty().getValue(null);
            }
        }
        throw new IllegalArgumentException(path);
    }

    private static Command createCommand(String name, ExecutionDetails executionDetails, String... arguments) {
        Command command = new Command();
        command.setCommand(name);
        command.setExecution(executionDetails);
        command.setArguments(Arrays.asList(arguments));
        return command;
    }

    private static ExecutionDetails createExecution(Executor executor, double importance, boolean isOptional,
                                                    String... privileges) {
        ExecutionDetails execution = new ExecutionDetails();
        execution.setImportance(importance);
        execution.setOptional(isOptional);
        execution.setExecutor(executor);
        execution.setPrivileges(new LinkedHashSet<>(Arrays.asList(privileges)));
        return execution;
    }

}
