package com.github.authme.configme.demo.beans;

import com.github.authme.configme.SettingsManager;
import com.github.authme.configme.TestUtils;
import com.github.authme.configme.configurationdata.ConfigurationDataBuilder;
import com.github.authme.configme.migration.PlainMigrationService;
import com.github.authme.configme.resource.YamlFileResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Runnable demo showing how bean properties can be used.
 */
public class BeanPropertiesDemo {

    private File configFile;

    public BeanPropertiesDemo() {
        configFile = copyFileFromJar("/demo/bean_demo_config.yml");
    }

    public static void main(String... args) {
        BeanPropertiesDemo demo = new BeanPropertiesDemo();

        System.out.println(demo.generateUserInfo());
    }

    public String generateUserInfo() {
        SettingsManager settingsManager = new SettingsManager(new YamlFileResource(configFile),
            new PlainMigrationService(), ConfigurationDataBuilder.collectData(SettingsHolderImpl.class));
        UserBase userBase = settingsManager.getProperty(SettingsHolderImpl.USER_BASE);

        String info = "Available users: " + userBase.getUsers().keySet();

        User richie = userBase.getUsers().get("richie");
        String savedLocationInfo = richie.getSavedLocations().entrySet().stream()
            .map(entry -> entry.getKey() + "( " + entry.getValue().getLatitude() + ", " + entry.getValue().getLongitude() + ")")
            .collect(Collectors.joining(", "));
        info += "\nSaved locations of Richie: " + savedLocationInfo;

        info += "\nNicknames of Bob: " + userBase.getUsers().get("bobby").getNicknames();

        Country country = settingsManager.getProperty(SettingsHolderImpl.COUNTRY);
        info += "\nCountry '" + country.getName() + "' has neighbors: " + String.join(", ", country.getNeighbors());
        return info;
    }


    /**
     * Copies a file from the codebase's resources to a temporary file.
     *
     * @param path the path to copy
     * @return the copied file
     */
    private static File copyFileFromJar(String path) {
        try {
            File file = File.createTempFile("configme-", "-beandemoconfig.yml");
            Files.copy(TestUtils.class.getResourceAsStream(path), file.toPath(), REPLACE_EXISTING);
            return file;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
