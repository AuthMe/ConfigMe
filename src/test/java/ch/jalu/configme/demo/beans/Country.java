package ch.jalu.configme.demo.beans;

import java.beans.Transient;
import java.util.List;

/**
 * Country bean.
 */
public class Country {

    private String name;
    private int population;
    private List<String> neighbors;
    private transient boolean isTemporary;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public List<String> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(List<String> neighbors) {
        this.neighbors = neighbors;
    }

    @Transient
    public boolean isTemporary() {
        return isTemporary;
    }

    public void setTemporary(boolean temporary) {
        isTemporary = temporary;
    }
}
