package com.github.authme.configme.samples;

import com.github.authme.configme.migration.MigrationService;
import com.github.authme.configme.properties.Property;
import com.github.authme.configme.propertymap.PropertyMap;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Provides {@link MigrationService} implementations for testing.
 */
public final class TestSettingsMigrationServices {

    private TestSettingsMigrationServices() {
    }

    /**
     * Returns a settings migration service which always answers that all data is up-to-date.
     *
     * @return test settings migration service
     */
    public static MigrationService alwaysFulfilled() {
        return new MigrationService() {
            @Override
            public boolean checkAndMigrate(FileConfiguration configuration, PropertyMap propertyMap) {
                return false;
            }
        };
    }

    /**
     * Returns a simple settings migration service which is fulfilled if all properties are present.
     *
     * @return test settings migration service
     */
    public static MigrationService checkAllPropertiesPresent() {
        return new MigrationService() {
            @Override
            public boolean checkAndMigrate(FileConfiguration configuration, PropertyMap propertyMap) {
                for (Property<?> property : propertyMap.keySet()) {
                    if (!property.isPresent(configuration)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

}
