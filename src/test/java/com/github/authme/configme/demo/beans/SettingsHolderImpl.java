package com.github.authme.configme.demo.beans;

import com.github.authme.configme.SettingsHolder;
import com.github.authme.configme.properties.Property;

import static com.github.authme.configme.properties.PropertyInitializer.newBeanProperty;

/**
 * Holds the settings we want to use.
 */
public final class SettingsHolderImpl implements SettingsHolder {

    public static final Property<UserBase> USER_BASE =
        newBeanProperty(UserBase.class, "userdata", new UserBase());

    public static final Property<Country> COUNTRY =
        newBeanProperty(Country.class, "country", new Country());

    private SettingsHolderImpl() {
    }
}
