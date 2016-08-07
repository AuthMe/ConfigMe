package com.github.authme.configme.samples;

import com.github.authme.configme.migration.MigrationService;
import com.github.authme.configme.propertymap.KnownProperties;
import com.github.authme.configme.propertymap.PropertyEntry;
import com.github.authme.configme.resource.PropertyResource;

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
            public boolean checkAndMigrate(PropertyResource resource, KnownProperties knownProperties) {
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
            public boolean checkAndMigrate(PropertyResource resource, KnownProperties knownProperties) {
                for (PropertyEntry entry : knownProperties.getEntries()) {
                    if (!entry.getProperty().isPresent(resource)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

}
