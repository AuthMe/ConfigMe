package ch.jalu.configme.neo.resource;

import ch.jalu.configme.neo.configurationdata.ConfigurationData;

// TODO: Naming to be revised (better name for PropertyResource?)
public interface PropertyResource {

    PropertyReader createReader();

    void exportProperties(ConfigurationData configurationData);

}
