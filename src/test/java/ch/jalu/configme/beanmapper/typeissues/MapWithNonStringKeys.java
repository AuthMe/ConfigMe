package ch.jalu.configme.beanmapper.typeissues;

import java.util.Map;

/**
 * Map with a key type other than String.
 */
public class MapWithNonStringKeys {

    private String name = "";
    private Map<Integer, Integer> map;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Integer, Integer> getMap() {
        return map;
    }

    public void setMap(Map<Integer, Integer> map) {
        this.map = map;
    }
}
