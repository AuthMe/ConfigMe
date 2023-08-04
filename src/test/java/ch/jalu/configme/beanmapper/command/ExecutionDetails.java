package ch.jalu.configme.beanmapper.command;

import ch.jalu.configme.Comment;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Command execution details.
 */
public class ExecutionDetails {

    private Executor executor;
    private boolean optional;
    @Comment(value = "The higher the number, the more important", repeat = true)
    private Double importance;
    private Set<String> privileges;

    public ExecutionDetails() {
    }

    public ExecutionDetails(Executor executor, double importance, boolean isOptional, String... privileges) {
        this.executor = executor;
        this.optional = isOptional;
        this.importance = importance;
        this.privileges = new LinkedHashSet<>(Arrays.asList(privileges));
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public Double getImportance() {
        return importance;
    }

    public void setImportance(Double importance) {
        this.importance = importance;
    }

    public Set<String> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(Set<String> privileges) {
        this.privileges = privileges;
    }
}
