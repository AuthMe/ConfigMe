package ch.jalu.configme;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.migration.MigrationService;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.resource.PropertyResource;
import ch.jalu.configme.resource.YamlFileResource;
import ch.jalu.configme.utils.Utils;

import java.io.File;
import java.util.Objects;

/**
 * Initializes {@link SettingsManager} instances.
 */
public class SettingsManagerBuilder {

    private final PropertyResource resource;
    private ConfigurationData configurationData;
    private MigrationService migrationService = new PlainMigrationService();

    private SettingsManagerBuilder(PropertyResource resource) {
        this.resource = resource;
    }

    public static SettingsManagerBuilder withYamlFile(File file) {
        Utils.createFileIfNotExists(file);
        return new SettingsManagerBuilder(new YamlFileResource(file));
    }

    public static SettingsManagerBuilder withResource(PropertyResource resource) {
        return new SettingsManagerBuilder(resource);
    }

    @SafeVarargs
    public final SettingsManagerBuilder configurationData(Class<? extends SettingsHolder>... classes) {
        this.configurationData = ConfigurationDataBuilder.createConfiguration(classes);
        return this;
    }

    public SettingsManagerBuilder configurationData(ConfigurationData configurationData) {
        this.configurationData = configurationData;
        return this;
    }

    public SettingsManagerBuilder migrationService(MigrationService migrationService) {
        this.migrationService = migrationService;
        return this;
    }

    public SettingsManager create() {
        Objects.requireNonNull(configurationData, "configurationData");
        Objects.requireNonNull(resource, "resource");
        return new SettingsManagerImpl(resource, configurationData, migrationService);
    }
}
