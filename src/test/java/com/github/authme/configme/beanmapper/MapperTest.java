package com.github.authme.configme.beanmapper;

import com.github.authme.configme.TestUtils;
import com.github.authme.configme.beanmapper.command.Command;
import com.github.authme.configme.beanmapper.command.CommandConfig;
import com.github.authme.configme.beanmapper.command.ExecutionDetails;
import com.github.authme.configme.beanmapper.command.Executor;
import com.github.authme.configme.beanmapper.worldgroup.GameMode;
import com.github.authme.configme.beanmapper.worldgroup.Group;
import com.github.authme.configme.resource.PropertyResource;
import com.github.authme.configme.resource.YamlFileResource;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import java.util.Map;
import java.util.Objects;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link Mapper}.
 */
public class MapperTest {

    @Test
    public void shouldCreateMap() {
        // given
        PropertyResource resource = new YamlFileResource(TestUtils.getJarFile("/beanmapper/worlds.yml"));
        Mapper mapper = new Mapper();
        String path = "groups";

        // when
        Map<String, Group> result = mapper.createMap(path, resource, Group.class);

        // then
        assertThat(result.keySet(), containsInAnyOrder("default", "creative"));
        Group survival = result.get("default");
        assertThat(survival.getWorlds(), contains("world", "world_nether", "world_the_end"));
        assertThat(survival.getDefaultGamemode(), equalTo(GameMode.SURVIVAL));
        Group creative = result.get("creative");
        assertThat(creative.getWorlds(), contains("creative"));
        assertThat(creative.getDefaultGamemode(), equalTo(GameMode.CREATIVE));
    }

    @Test
    public void shouldCreateCommands() {
        // given
        PropertyResource resource = new YamlFileResource(TestUtils.getJarFile("/beanmapper/commands.yml"));
        Mapper mapper = new Mapper();

        // when
        CommandConfig config = mapper.createBean("commandconfig", resource, CommandConfig.class);

        // then
        assertThat(config.getDuration(), equalTo(13));
        assertThat(config.getCommands(), hasSize(3));

        Command saveCommand = config.getCommands().get(0);
        assertThat(saveCommand.getCommand(), equalTo("save"));
        assertThat(saveCommand.getArguments(), empty());
        assertThat(saveCommand, hasExecution(Executor.CONSOLE, false, 1.0));
        assertThat(saveCommand.getExecution().getPrivileges(), contains("action.save"));

        Command refreshCommand = config.getCommands().get(1);
        assertThat(refreshCommand.getCommand(), equalTo("refresh"));
        assertThat(refreshCommand.getArguments(), contains("force", "async"));
        assertThat(refreshCommand, hasExecution(Executor.CONSOLE, false, 0.4));
        assertThat(refreshCommand.getExecution().getPrivileges(), containsInAnyOrder("page.view", "action.refresh"));

        Command openCommand = config.getCommands().get(2);
        assertThat(openCommand.getCommand(), equalTo("open"));
        assertThat(openCommand.getArguments(), contains("f", "x", "z"));
        // TODO: optional should = true; implement support for boolean values
        // assertThat(openCommand, hasExecution(Executor.USER, true, 0.7));
        assertThat(openCommand.getExecution().getPrivileges(), contains("page.view"));
    }

    private static Matcher<Command> hasExecution(final Executor executor, final boolean optional,
                                                 final Double importance) {
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
