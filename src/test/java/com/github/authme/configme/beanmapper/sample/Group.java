package com.github.authme.configme.beanmapper.sample;

import java.util.List;

/**
 * World group.
 */
public class Group {

    private List<String> worlds;
    private GameMode defaultGamemode;

    public void setWorlds(List<String> worlds) {
        this.worlds = worlds;
    }

    public List<String> getWorlds() {
        return worlds;
    }

    public void setDefaultGamemode(GameMode defaultGamemode) {
        this.defaultGamemode = defaultGamemode;
    }

    public GameMode getDefaultGamemode() {
        return defaultGamemode;
    }
}
