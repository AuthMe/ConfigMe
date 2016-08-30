package com.github.authme.configme.demo;

import com.github.authme.configme.SettingsManager;
import com.github.authme.configme.TestUtils;
import com.github.authme.configme.migration.PlainMigrationService;
import com.github.authme.configme.propertymap.PropertyEntry;
import com.github.authme.configme.propertymap.SettingsFieldRetriever;
import com.github.authme.configme.resource.PropertyResource;
import com.github.authme.configme.resource.YamlFileResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Demo application that creates a "Welcome" HTML file based on some configurable properties.
 */
public class WelcomeWriter {

    private File configFile;

    public static void main(String... args) {
        System.out.println("Generating HTML as per the config.yml file");
        System.out.println("------------------");
        WelcomeWriter writer = new WelcomeWriter();
        System.out.println(writer.generateWelcomeFile());
        System.out.println("------------------");
        System.out.println("Copied config file to '" + writer.configFile + "'");
    }

    /**
     * Generates the welcome file based on the settings.
     *
     * @return generated HTML welcome message
     */
    public String generateWelcomeFile() {
        SettingsManager settings = initSettings();
        String welcomeFilePattern = ""
            + "<h1 style='font-size: %dpt'>%s</h1>"
            + "%n<span style='color: %s; font-size: %dpt'>%s</span>";

        Color subtitleColor = settings.getProperty(TitleConfig.SUBTITLE_COLOR);
        return String.format(welcomeFilePattern, settings.getProperty(TitleConfig.TITLE_SIZE),
            settings.getProperty(TitleConfig.TITLE_TEXT), subtitleColor.hexCode,
            settings.getProperty(TitleConfig.SUBTITLE_SIZE), settings.getProperty(TitleConfig.SUBTITLE_TEXT));
    }

    /**
     * @return the temporary config file
     */
    public File getConfigFile() {
        return configFile;
    }

    /**
     * Initializes the settings manager.
     *
     * @return the settings manager
     */
    private SettingsManager initSettings() {
        List<PropertyEntry> knownProperties = SettingsFieldRetriever.getAllProperties(TitleConfig.class);
        // Copy the demo/config.yml instead of using it directly so it doesn't get overridden
        configFile = copyFileFromJar("/demo/config.yml");
        PropertyResource resource = new YamlFileResource(configFile);
        return new SettingsManager(knownProperties, resource, new PlainMigrationService());
    }

    /**
     * Copies a file from the codebase's resources to a temporary file.
     *
     * @param path the path to copy
     * @return the copied file
     */
    private static File copyFileFromJar(String path) {
        try {
            File file = File.createTempFile("configme-", "-democonfig.yml");
            Files.copy(TestUtils.class.getResourceAsStream(path), file.toPath(), REPLACE_EXISTING);
            return file;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
