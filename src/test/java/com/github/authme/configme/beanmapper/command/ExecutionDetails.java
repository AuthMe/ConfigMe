package com.github.authme.configme.beanmapper.command;

/**
 * Command execution details.
 */
public class ExecutionDetails {

    private Executor executor;
    private boolean optional;
    private Double importance;

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
}
