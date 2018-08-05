package ch.jalu.configme.beanmapper.typeissues;

import java.util.List;

/**
 * Class with collection using generics.
 */
public class GenericCollection {

    private String name = "";
    private List<? extends String> collection;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<? extends String> getCollection() {
        return collection;
    }

    public void setCollection(List<? extends String> collection) {
        this.collection = collection;
    }
}
