package ch.jalu.configme.samples.inheritance;

import java.beans.Transient;

/**
 * Child class.
 */
public class Child extends Middle {

    private int importance;

    @Override
    public boolean isTemporary() {
        return super.isTemporary();
    }

    @Override
    @Transient(false)
    public void setTemporary(boolean temporary) {
        super.setTemporary(temporary);
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }
}
