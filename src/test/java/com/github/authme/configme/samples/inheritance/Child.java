package com.github.authme.configme.samples.inheritance;

/**
 * Child class.
 */
public class Child extends Middle {

    private boolean isTemporary;
    private int importance;

    @Override
    public boolean isTemporary() {
        return isTemporary;
    }

    @Override
    public void setTemporary(boolean temporary) {
        isTemporary = temporary;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }
}
