package ch.jalu.configme.demo.beans;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.TestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Runnable demo showing how bean properties can be used.
 */
public class BeanPropertiesDemo {

    private Path configFile;

    public BeanPropertiesDemo() {
        configFile = copyFileFromJar("/demo/bean_demo_config.yml");
    }

    public static void main(String... args) {
        BeanPropertiesDemo demo = new BeanPropertiesDemo();

        System.out.println(demo.generateUserInfo());
        System.out.println("Copied config file to '" + demo.configFile + "'");
    }

    public String generateUserInfo() {
        SettingsManager settingsManager = SettingsManagerBuilder.withYamlFile(configFile)
            .configurationData(DemoSettings.class).create();
        UserBase userBase = settingsManager.getProperty(DemoSettings.USER_BASE);

        User richie = userBase.getRichie();
        String savedLocationInfo = richie.getSavedLocations().entrySet().stream()
            .map(entry -> entry.getKey() + " " + entry.getValue())
            .collect(Collectors.joining(", "));
        String info = "Saved locations of Richie: " + savedLocationInfo;

        info += "\nNicknames of Bob: " + String.join(", ", userBase.getBobby().getNicknames());

        Country country = settingsManager.getProperty(DemoSettings.COUNTRY);
        info += "\nCountry '" + country.getName() + "' has neighbors: " + String.join(", ", country.getNeighbors());
        return info;
    }

    /**
     * @return the config file
     */
    public Path getConfigFile() {
        return configFile;
    }

    /**
     * Copies a file from the codebase's resources to a temporary file.
     *
     * @param path the path to copy
     * @return the copied file
     */
    private static Path copyFileFromJar(String path) {
        try {
            Path tempFile = Files.createTempFile("configme-", "-beandemoconfig.yml");
            Files.copy(TestUtils.class.getResourceAsStream(path), tempFile, REPLACE_EXISTING);
            return tempFile;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
