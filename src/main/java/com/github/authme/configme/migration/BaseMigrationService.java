package com.github.authme.configme.migration;

import com.github.authme.configme.properties.Property;
import com.github.authme.configme.propertymap.PropertyMap;
import com.github.authme.configme.resource.PropertyResource;

/**
 * Base implementation for a custom migration service.
 */
public abstract class BaseMigrationService implements MigrationService {

    @Override
    public boolean checkAndMigrate(PropertyResource resource, PropertyMap propertyMap) {
        return performMigrations(resource, propertyMap) || !containsAllSettings(resource, propertyMap);
    }

    protected abstract boolean performMigrations(PropertyResource resource, PropertyMap propertyMap);

    private static boolean containsAllSettings(PropertyResource resource, PropertyMap propertyMap) {
        for (Property<?> property : propertyMap.keySet()) {
            if (!property.isPresent(resource)) {
                return false;
            }
        }
        return true;
    }

}
