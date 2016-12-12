package com.github.authme.configme.samples.inheritance;

/**
 * Parent class.
 */
public class Parent {

    private long id;
    private transient boolean isTemporary;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isTemporary() {
        return isTemporary;
    }

    public void setTemporary(boolean temporary) {
        isTemporary = temporary;
    }
}
