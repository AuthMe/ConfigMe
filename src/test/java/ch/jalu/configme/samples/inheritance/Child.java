package ch.jalu.configme.samples.inheritance;

/**
 * Child class.
 */
public class Child extends Middle {

    private int importance;
    private boolean temporary;

    public int readImportance() {
        return importance;
    }

    public void writeImportance(int importance) {
        this.importance = importance;
    }

    public boolean readChildTemporary() {
        return temporary;
    }

    public void writeChildTemporary(boolean temporary) {
        this.temporary = temporary;
    }

}
