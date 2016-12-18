package ch.jalu.configme.beanmapper.typeissues;

import java.util.Deque;

/**
 * Class with unsupported collection type.
 */
public class UnsupportedCollection {

    private String name = "";
    private Deque<Double> collection;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Deque<Double> getCollection() {
        return collection;
    }

    public void setCollection(Deque<Double> collection) {
        this.collection = collection;
    }
}
