package ch.jalu.configme.configurationdata;

import ch.jalu.configme.properties.Property;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Contains information about the available properties and their associated comments.
 *
 * @see ConfigurationDataBuilder
 */
public class ConfigurationData {

    private final List<Property<?>> properties;
    private final Map<String, String[]> sectionComments;

    public ConfigurationData(List<? extends Property<?>> properties) {
        this(properties, Collections.emptyMap());
    }

    public ConfigurationData(List<? extends Property<?>> properties, Map<String, String[]> sectionComments) {
        this.properties = Collections.unmodifiableList(properties);
        this.sectionComments = Collections.unmodifiableMap(sectionComments);
    }

    public List<Property<?>> getProperties() {
        return properties;
    }

    public String[] getCommentsForSection(String path) {
        String[] comments = sectionComments.get(path);
        return (comments == null) ? new String[0] : comments;
    }
}
