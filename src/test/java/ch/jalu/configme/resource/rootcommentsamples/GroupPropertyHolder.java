package ch.jalu.configme.resource.rootcommentsamples;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.beanmapper.worldgroup.GameMode;
import ch.jalu.configme.beanmapper.worldgroup.Group;
import ch.jalu.configme.properties.BeanProperty;
import ch.jalu.configme.properties.Property;

import java.util.Collections;

public final class GroupPropertyHolder implements SettingsHolder {

    @Comment("Group configuration number")
    public static final Property<Group> GROUP = new BeanProperty<>(Group.class, "", buildDefaultGroup());

    private GroupPropertyHolder() {
    }

    private static Group buildDefaultGroup() {
        Group group = new Group();
        group.setWorlds(Collections.singletonList("world"));
        group.setDefaultGamemode(GameMode.CREATIVE);
        return group;
    }
}
