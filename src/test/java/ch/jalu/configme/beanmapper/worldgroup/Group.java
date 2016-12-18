package ch.jalu.configme.beanmapper.worldgroup;

import ch.jalu.configme.beanmapper.ExportName;

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

    @ExportName("default-gamemode")
    public GameMode getDefaultGamemode() {
        return defaultGamemode;
    }
}
