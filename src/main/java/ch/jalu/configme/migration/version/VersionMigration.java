package ch.jalu.configme.migration.version;

import ch.jalu.configme.migration.Migration;

/**
 * @author gamerover98
 */
public interface VersionMigration extends Migration {

    /**
     * @return the configuration version this migration converts to (e.g. 2)
     */
    int targetVersion();
}
