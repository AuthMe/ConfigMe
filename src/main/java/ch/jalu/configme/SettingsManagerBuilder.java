package ch.jalu.configme;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.migration.MigrationService;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.resource.PropertyResource;
import ch.jalu.configme.resource.YamlFileResource;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import ch.jalu.configme.utils.Utils;

import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Creates {@link SettingsManager} instances.
 */
public final class SettingsManagerBuilder {

    private final PropertyResource resource;
    private ConfigurationData configurationData;
    private MigrationService migrationService;

    private SettingsManagerBuilder(PropertyResource resource) {
        this.resource = resource;
    }

    /**
     * Creates a builder, using the given YAML file to use as property resource.
     *
     * @param file the yaml file to use
     * @return settings manager builder
     */
    public static SettingsManagerBuilder withYamlFile(Path file) {
        return withYamlFile(file, YamlFileResourceOptions.builder().build());
    }

    /**
     * Creates a builder, using the given YAML file to use as property resource.
     *
     * @param file the yaml file to use
     * @return settings manager builder
     */
    public static SettingsManagerBuilder withYamlFile(File file) {
        return withYamlFile(file.toPath(), YamlFileResourceOptions.builder().build());
    }

    /**
     * Creates a builder, using the given YAML file to use as property resource with the given options.
     *
     * @param path the yaml file to use
     * @param resourceOptions the resource options
     * @return settings manager builder
     */
    public static SettingsManagerBuilder withYamlFile(Path path, YamlFileResourceOptions resourceOptions) {
        Utils.createFileIfNotExists(path);
        return new SettingsManagerBuilder(new YamlFileResource(path, resourceOptions));
    }

    /**
     * Creates a new builder with the given property resource.
     *
     * @param resource the resource to use
     * @return settings manager builder
     */
    public static SettingsManagerBuilder withResource(PropertyResource resource) {
        return new SettingsManagerBuilder(resource);
    }

    /**
     * Sets up configuration data with the input of the given settings holder classes.
     *
     * @param classes the settings holder classes
     * @return this builder
     */
    @SafeVarargs
    public final SettingsManagerBuilder configurationData(Class<? extends SettingsHolder>... classes) {
        this.configurationData = ConfigurationDataBuilder.createConfiguration(classes);
        return this;
    }

    /**
     * Sets the provided configuration data to the builder.
     *
     * @param configurationData the configuration data
     * @return this builder
     */
    public SettingsManagerBuilder configurationData(ConfigurationData configurationData) {
        this.configurationData = configurationData;
        return this;
    }

    /**
     * Sets the given migration service to the builder.
     *
     * @param migrationService the migration service to use (or null)
     * @return this builder
     */
    public SettingsManagerBuilder migrationService(@Nullable MigrationService migrationService) {
        this.migrationService = migrationService;
        return this;
    }

    /**
     * Registers the default migration service to the builder, which triggers a rewrite of the
     * configuration file if a property is missing from it.
     *
     * @return this builder
     */
    public SettingsManagerBuilder useDefaultMigrationService() {
        this.migrationService = new PlainMigrationService();
        return this;
    }

    /**
     * Creates a settings manager instance. It is mandatory that resource and configuration data have been
     * configured beforehand.
     *
     * @return the settings manager
     */
    public SettingsManager create() {
        Objects.requireNonNull(resource, "resource");
        Objects.requireNonNull(configurationData, "configurationData");
        return new SettingsManagerImpl(resource, configurationData, migrationService);
    }
}
