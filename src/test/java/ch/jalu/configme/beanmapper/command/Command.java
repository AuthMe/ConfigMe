package ch.jalu.configme.beanmapper.command;

import java.util.Collections;
import java.util.List;

/**
 * Command.
 */
public class Command {

    private String command;
    private List<String> arguments = Collections.emptyList();
    private ExecutionDetails execution;


    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    public ExecutionDetails getExecution() {
        return execution;
    }

    public void setExecution(ExecutionDetails execution) {
        this.execution = execution;
    }
}
