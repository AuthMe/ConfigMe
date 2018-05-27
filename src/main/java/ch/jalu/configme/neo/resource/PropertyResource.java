package ch.jalu.configme.neo.resource;

import ch.jalu.configme.neo.configurationdata.ConfigurationData;

// TODO: Naming to be revised (better names for PropertyResource / PropertyReader)
public interface PropertyResource {

    PropertyReader createReader();

    // TODO: Or separate interface?
    void exportProperties(ConfigurationData configurationData);

}
