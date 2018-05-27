package ch.jalu.configme.neo;

import ch.jalu.configme.neo.configurationdata.ConfigurationData;
import ch.jalu.configme.neo.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.neo.migration.MigrationService;
import ch.jalu.configme.neo.migration.PlainMigrationService;
import ch.jalu.configme.neo.registry.DefaultValueRegistry;
import ch.jalu.configme.neo.registry.ValuesRegistry;
import ch.jalu.configme.neo.resource.PropertyResource;
import ch.jalu.configme.neo.resource.YamlFileResource;
import ch.jalu.configme.utils.Utils;

import java.io.File;
import java.util.Objects;

/**
 * Initializes {@link SettingsManager} instances.
 */
public class SettingsManagerBuilder {

    private ConfigurationData configurationData;
    private PropertyResource resource;
    private MigrationService migrationService = new PlainMigrationService();
    private ValuesRegistry valuesRegistry = new DefaultValueRegistry();

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

    public SettingsManagerBuilder propertyResource(PropertyResource resource) {
        this.resource = resource;
        return this;
    }

    public SettingsManagerBuilder configurationData(Class<? extends SettingsHolder>... classes) {
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

    public SettingsManagerBuilder valuesRegistry(ValuesRegistry valuesRegistry) {
        this.valuesRegistry = valuesRegistry;
        return this;
    }

    public SettingsManager create() {
        Objects.requireNonNull(configurationData, "configurationData");
        Objects.requireNonNull(resource, "resource");
        Objects.requireNonNull(valuesRegistry, "valuesRegistry");
        return new SettingsManagerImpl(resource, configurationData, migrationService, valuesRegistry);
    }

}
