package ch.jalu.configme.samples.settingsholders;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.beanmapper.worldgroup.GameMode;
import ch.jalu.configme.properties.Property;

import java.util.concurrent.TimeUnit;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class SettingsHolderWithEnumPropertyComments implements SettingsHolder {

    @Comment("Time unit (SECONDS, MINUTES, HOURS)")
    public static final Property<TimeUnit> TIME_UNIT_PROP = newProperty(TimeUnit.class, "sample.timeUnit", TimeUnit.SECONDS);

    public static final Property<GameMode> GAME_MODE = newProperty(GameMode.class, "sample.gameMode", GameMode.SURVIVAL);

}
