package ch.jalu.configme.migration.version;

import ch.jalu.configme.migration.Migration;

/**
 * @author gamerover98
 */
public interface VersionMigration extends Migration {

    /**
     * @return The current version value (such as 1).
     */
    int fromVersion();

    /**
     * @return The next version value (such as 2).
     */
    int toVersion();
}
