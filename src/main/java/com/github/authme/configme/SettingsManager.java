package com.github.authme.configme;

import com.github.authme.configme.migration.MigrationService;
import com.github.authme.configme.properties.Property;
import com.github.authme.configme.propertymap.PropertyMap;
import com.github.authme.configme.utils.CollectionUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The new settings manager.
 */
public class SettingsManager {

    private static final String INDENTATION = "    ";

    private final File configFile;
    private final PropertyMap propertyMap;
    private final MigrationService migrationService;
    private FileConfiguration configuration;

    /**
     * Constructor. Checks the given {@link FileConfiguration} object for completeness.
     *
     * @param configFile The configuration file
     * @param propertyMap Collection of all available settings
     * @param migrationService Migration service to check the settings file with
     */
    public SettingsManager(File configFile, PropertyMap propertyMap, MigrationService migrationService) {
        this.configuration = YamlConfiguration.loadConfiguration(configFile);
        this.configFile = configFile;
        this.propertyMap = propertyMap;
        this.migrationService = migrationService;
        validateAndLoadOptions();
    }

    /**
     * Constructor for testing purposes, allowing more options.
     *
     * @param configuration The FileConfiguration object to use
     * @param configFile The file to write to
     * @param propertyMap The property map whose properties should be verified for presence, or null to skip this
     * @param migrationService Migration service, or null to skip migration checks
     */
    protected SettingsManager(FileConfiguration configuration, File configFile,
                    PropertyMap propertyMap, MigrationService migrationService) {
        this.configuration = configuration;
        this.configFile = configFile;
        this.propertyMap = propertyMap;
        this.migrationService = migrationService;

        if (propertyMap != null && migrationService != null) {
            validateAndLoadOptions();
        }
    }

    /**
     * Gets the given property from the configuration.
     *
     * @param property The property to retrieve
     * @param <T> The property's type
     * @return The property's value
     */
    public <T> T getProperty(Property<T> property) {
        return property.getFromFile(configuration);
    }

    /**
     * Sets a new value for the given property.
     *
     * @param property The property to modify
     * @param value The new value to assign to the property
     * @param <T> The property's type
     */
    public <T> void setProperty(Property<T> property, T value) {
        configuration.set(property.getPath(), value);
    }

    /**
     * Reloads the configuration.
     */
    public void reload() {
        configuration = YamlConfiguration.loadConfiguration(configFile);
        validateAndLoadOptions();
    }

    /**
     * Saves the config file. Use after migrating one or more settings.
     */
    public void save() {
        try (FileWriter writer = new FileWriter(configFile)) {
            Yaml simpleYaml = newYaml(false);
            Yaml singleQuoteYaml = newYaml(true);

            writer.write("");
            // Contains all but the last node of the setting, e.g. [DataSource, mysql] for "DataSource.mysql.username"
            List<String> currentPath = new ArrayList<>();
            for (Map.Entry<Property<?>, String[]> entry : propertyMap.entrySet()) {
                Property<?> property = entry.getKey();

                // Handle properties
                List<String> propertyPath = Arrays.asList(property.getPath().split("\\."));
                List<String> commonPathParts = CollectionUtils.filterCommonStart(
                    currentPath, propertyPath.subList(0, propertyPath.size() - 1));
                List<String> newPathParts = CollectionUtils.getRange(propertyPath, commonPathParts.size());

                if (commonPathParts.isEmpty()) {
                    writer.append("\n");
                }

                int indentationLevel = commonPathParts.size();
                if (newPathParts.size() > 1) {
                    for (String path : newPathParts.subList(0, newPathParts.size() - 1)) {
                        writer.append("\n")
                            .append(indent(indentationLevel))
                            .append(path)
                            .append(": ");
                        ++indentationLevel;
                    }
                }
                for (String comment : entry.getValue()) {
                    writer.append("\n")
                        .append(indent(indentationLevel))
                        .append("# ")
                        .append(comment);
                }
                writer.append("\n")
                    .append(indent(indentationLevel))
                    .append(CollectionUtils.getRange(newPathParts, newPathParts.size() - 1).get(0))
                    .append(": ")
                    .append(toYaml(property, indentationLevel, simpleYaml, singleQuoteYaml));

                currentPath = propertyPath.subList(0, propertyPath.size() - 1);
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void validateAndLoadOptions() {
        if (migrationService.checkAndMigrate(configuration, propertyMap)) {
            save();
        }
    }

    private <T> String toYaml(Property<T> property, int indent, Yaml simpleYaml, Yaml singleQuoteYaml) {
        String representation = property.toYaml(configuration, simpleYaml, singleQuoteYaml);
        String result = "";
        for (String line : representation.split("\\n")) {
            result += "\n" + indent(indent) + line;
        }
        return result;
    }

    private static Yaml newYaml(boolean useSingleQuotes) {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setAllowUnicode(true);
        if (useSingleQuotes) {
            options.setDefaultScalarStyle(DumperOptions.ScalarStyle.SINGLE_QUOTED);
        }
        return new Yaml(options);
    }

    private static String indent(int level) {
        String result = "";
        for (int i = 0; i < level; i++) {
            result += INDENTATION;
        }
        return result;
    }

}
