package ch.jalu.configme.samples.beanannotations;

import java.beans.Transient;
import java.util.Set;

/**
 * Sample bean class with annotated properties.
 */
public class AnnotatedBean {

    private String name;
    private int size;
    private Set<AnnotatedEntry> entries;
    private boolean isValid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Set<AnnotatedEntry> getEntries() {
        return entries;
    }

    public void setEntries(Set<AnnotatedEntry> entries) {
        this.entries = entries;
    }

    @Transient
    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}
