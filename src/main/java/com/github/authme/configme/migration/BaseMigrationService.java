package com.github.authme.configme.migration;

import com.github.authme.configme.properties.Property;
import com.github.authme.configme.propertymap.PropertyMap;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Base implementation for a custom migration service.
 */
public abstract class BaseMigrationService implements MigrationService {

    @Override
    public boolean checkAndMigrate(FileConfiguration configuration, PropertyMap propertyMap) {
        return performMigrations(configuration, propertyMap) || !containsAllSettings(configuration, propertyMap);
    }

    protected abstract boolean performMigrations(FileConfiguration configuration, PropertyMap propertyMap);

    private static boolean containsAllSettings(FileConfiguration configuration, PropertyMap propertyMap) {
        for (Property<?> property : propertyMap.keySet()) {
            if (!property.isPresent(configuration)) {
                return false;
            }
        }
        return true;
    }

}
