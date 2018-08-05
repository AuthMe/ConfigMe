package ch.jalu.configme.beanmapper.typeissues;

import java.util.List;

/**
 * Class with untyped collection.
 */
public class UntypedCollection {

    private String name;
    private List collection;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List getCollection() {
        return collection;
    }

    public void setCollection(List collection) {
        this.collection = collection;
    }
}
