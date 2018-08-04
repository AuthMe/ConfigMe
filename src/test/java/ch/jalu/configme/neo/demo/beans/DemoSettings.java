package ch.jalu.configme.neo.demo.beans;

import ch.jalu.configme.neo.SettingsHolder;
import ch.jalu.configme.neo.properties.Property;

import java.util.ArrayList;

import static ch.jalu.configme.neo.properties.PropertyInitializer.newBeanProperty;

/**
 * Holds the settings we want to use.
 */
public final class DemoSettings implements SettingsHolder {

    public static final Property<UserBase> USER_BASE =
        newBeanProperty(UserBase.class, "userdata", initDefaultUserBase());

    public static final Property<Country> COUNTRY =
        newBeanProperty(Country.class, "country", initDefaultCountry());

    private DemoSettings() {
    }

    /**
     * @return user base with default values
     */
    private static UserBase initDefaultUserBase() {
        User user = new User();
        user.setName("");
        user.setHomeLocation(new Location());

        UserBase userBase = new UserBase();
        userBase.setBobby(user);
        userBase.setRichie(user);
        userBase.setLionel(user);
        userBase.setVersion(0);
        return userBase;
    }

    /**
     * @return country object with default values
     */
    private static Country initDefaultCountry() {
        Country country = new Country();
        country.setName("");
        country.setNeighbors(new ArrayList<>());
        return country;
    }
}
