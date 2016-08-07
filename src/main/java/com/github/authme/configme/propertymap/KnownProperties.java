package com.github.authme.configme.propertymap;

import java.util.List;

/**
 * Provides all known properties for a given resource.
 */
public interface KnownProperties {

    /**
     * Returns all existing properties for the property resource.
     *
     * @return the property descriptions
     */
    List<PropertyEntry> getEntries();

}
