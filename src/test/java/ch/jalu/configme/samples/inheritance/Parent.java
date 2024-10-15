package ch.jalu.configme.samples.inheritance;

/**
 * Parent class.
 */
public class Parent {

    private long id;
    private transient boolean temporary;


    // Intentionally not named like getters because methods are ignored by the mapper

    public long readId() {
        return id;
    }

    public void writeId(long id) {
        this.id = id;
    }

    public boolean readTemporary() {
        return temporary;
    }

    public void writeTemporary(boolean temp) {
        this.temporary = temp;
    }
}
