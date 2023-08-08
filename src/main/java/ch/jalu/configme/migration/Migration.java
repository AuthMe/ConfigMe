package ch.jalu.configme.migration;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.resource.PropertyReader;
import org.jetbrains.annotations.NotNull;

/**
 * @author gamerover98
 */
public interface Migration {

    /**
     * Migrate the configuration.
     *
     * @param reader the property reader to read the configuration file from
     * @param configurationData configuration data to update a property's value
     */
    void migrate(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData);
}
