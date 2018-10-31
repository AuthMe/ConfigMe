package ch.jalu.configme.resource;

import ch.jalu.configme.configurationdata.ConfigurationData;

/**
 * Represents a medium (typically a file on disk) from which property values should be built and allows to
 * write back to it.
 */
public interface PropertyResource {

    /**
     * Creates a reader to access the values in the medium (typically a file).
     * <p>
     * The reader is discarded after its use and so is not required to refresh itself.
     *
     * @return reader providing values in the medium (e.g. file)
     */
    PropertyReader createReader();

    /**
     * Exports the provided configuration data to the medium (typically a file).
     *
     * @param configurationData the configuration data to export
     */
    void exportProperties(ConfigurationData configurationData);

}
