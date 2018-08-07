package ch.jalu.configme;

import ch.jalu.configme.properties.Property;

public interface SettingsManager {

    <T> T getProperty(Property<T> property);

    <T> void setProperty(Property<T> property, T value);

    void reload();

    void save();

}
