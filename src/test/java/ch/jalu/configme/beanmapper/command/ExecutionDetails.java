package ch.jalu.configme.beanmapper.command;

import java.util.Set;

/**
 * Command execution details.
 */
public class ExecutionDetails {

    private Executor executor;
    private boolean optional;
    private Double importance;
    private Set<String> privileges;

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
