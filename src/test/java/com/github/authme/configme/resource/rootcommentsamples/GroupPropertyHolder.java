package com.github.authme.configme.resource.rootcommentsamples;

import com.github.authme.configme.Comment;
import com.github.authme.configme.SettingsHolder;
import com.github.authme.configme.beanmapper.worldgroup.GameMode;
import com.github.authme.configme.beanmapper.worldgroup.Group;
import com.github.authme.configme.properties.BeanProperty;
import com.github.authme.configme.properties.Property;

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
