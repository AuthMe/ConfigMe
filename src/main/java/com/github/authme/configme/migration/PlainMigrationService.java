package com.github.authme.configme.migration;

import com.github.authme.configme.knownproperties.PropertyEntry;
import com.github.authme.configme.resource.PropertyResource;

import java.util.List;

/**
 * Simple migration service that can be extended.
 */
public class PlainMigrationService implements MigrationService {

    @Override
    public boolean checkAndMigrate(PropertyResource resource, List<PropertyEntry> knownProperties) {
        return performMigrations(resource, knownProperties) || !containsAllSettings(resource, knownProperties);
    }

    /**
     * Override this method for custom migrations. This method is executed before checking
     * if all settings are present. For instance, you could implement deleting obsolete properties
     * and renamed properties in this method.
     * <p>
     * Note that you do <i>not</i> have to save the resource. The settings manager automatically
     * does this if the migration service returns an according result.
     *
     * @param resource the property resource to check
     * @param knownProperties list of known properties
     * @return true if a migration was performed and the config should be saved,
     * false if no migration was performed
     */
    protected boolean performMigrations(PropertyResource resource, List<PropertyEntry> knownProperties) {
        return false;
    }

    private static boolean containsAllSettings(PropertyResource resource, List<PropertyEntry> knownProperties) {
        for (PropertyEntry entry : knownProperties) {
            if (!entry.getProperty().isPresent(resource)) {
                return false;
            }
        }
        return true;
    }

}
