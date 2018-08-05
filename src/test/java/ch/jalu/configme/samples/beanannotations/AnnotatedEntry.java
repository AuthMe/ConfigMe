package ch.jalu.configme.samples.beanannotations;

import ch.jalu.configme.beanmapper.ExportName;

/**
 * "Entry" bean class with annotated properties.
 */
public class AnnotatedEntry {

    private long id;
    private boolean hasId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean getHasId() {
        return hasId;
    }

    @ExportName("has-id")
    public void setHasId(boolean hasId) {
        this.hasId = hasId;
    }
}
