package ch.jalu.configme.beanmapper.typeissues;

import java.util.Map;

/**
 * Class with untyped map.
 */
public class UntypedMap {

    private String name = "";
    private Map<String, ?> map;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, ?> getMap() {
        return map;
    }

    public void setMap(Map<String, ?> map) {
        this.map = map;
    }
}
