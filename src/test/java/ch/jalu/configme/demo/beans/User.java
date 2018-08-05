package ch.jalu.configme.demo.beans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Bean class for user data.
 */
public class User {

    private String name;
    private Location homeLocation;
    private Map<String, Location> savedLocations = new HashMap<>();
    private Set<String> nicknames = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getHomeLocation() {
        return homeLocation;
    }

    public void setHomeLocation(Location homeLocation) {
        this.homeLocation = homeLocation;
    }

    public Map<String, Location> getSavedLocations() {
        return savedLocations;
    }

    public void setSavedLocations(Map<String, Location> savedLocations) {
        this.savedLocations = savedLocations;
    }

    public Set<String> getNicknames() {
        return nicknames;
    }

    public void setNicknames(Set<String> nicknames) {
        this.nicknames = nicknames;
    }
}
