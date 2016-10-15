package com.github.authme.configme.knownproperties;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Contains information about the available properties and their associated comments.
 *
 * @see ConfigurationDataBuilder
 */
public class ConfigurationData {

    private final List<PropertyEntry> propertyEntries;
    private final Map<String, String[]> sectionComments;

    public ConfigurationData(List<PropertyEntry> propertyEntries) {
        this(propertyEntries, Collections.emptyMap());
    }

    public ConfigurationData(List<PropertyEntry> propertyEntries, Map<String, String[]> sectionComments) {
        this.propertyEntries = Collections.unmodifiableList(propertyEntries);
        this.sectionComments = Collections.unmodifiableMap(sectionComments);
    }

    public List<PropertyEntry> getPropertyEntries() {
        return propertyEntries;
    }

    public String[] getCommentsForSection(String path) {
        String[] comments = sectionComments.get(path);
        return (comments == null) ? new String[0] : comments;
    }
}
