package com.github.authme.configme.demo;

import com.github.authme.configme.SettingsHolder;
import com.github.authme.configme.properties.Property;

import static com.github.authme.configme.properties.PropertyInitializer.newProperty;

/**
 * Sample file with property fields. You can have multiple classes with property fields
 * to separate the properties thematically as your configuration grows.
 */
public class TitleConfig implements SettingsHolder {

    public static final Property<String> TITLE_TEXT =
        newProperty("title.text", "Test");

    public static final Property<Integer> TITLE_SIZE =
        newProperty("title.size", 12);

    public static final Property<String> SUBTITLE_TEXT =
        newProperty("subtitle.text", "hello world");

    public static final Property<Integer> SUBTITLE_SIZE =
        newProperty("subtitle.size", 10);

    public static final Property<Color> SUBTITLE_COLOR =
        newProperty(Color.class, "subtitle.color", Color.ORANGE);

    private TitleConfig() {
    }
}
