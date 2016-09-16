package com.github.authme.configme.beanmapper.command;

import java.util.List;

/**
 * Command configuration.
 */
public class CommandConfig {

    private List<Command> commands;
    private int duration;

    public List<Command> getCommands() {
        return commands;
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
