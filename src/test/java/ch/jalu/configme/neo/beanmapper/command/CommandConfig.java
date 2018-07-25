package ch.jalu.configme.neo.beanmapper.command;

import java.util.Map;

/**
 * Command configuration.
 */
public class CommandConfig {

    private Map<String, Command> commands;
    private int duration;

    public Map<String, Command> getCommands() {
        return commands;
    }

    public void setCommands(Map<String, Command> commands) {
        this.commands = commands;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
