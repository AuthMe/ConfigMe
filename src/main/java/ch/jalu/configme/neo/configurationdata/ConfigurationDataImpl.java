package ch.jalu.configme.neo.configurationdata;

import ch.jalu.configme.neo.properties.Property;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Contains information about the available properties and their associated comments.
 */
public class ConfigurationDataImpl implements ConfigurationData {

    private final List<Property<?>> allProperties;
    private final Map<String, String[]> sectionComments;

    public ConfigurationDataImpl(List<? extends Property<?>> allProperties) {
        this(allProperties, Collections.emptyMap());
    }

    public ConfigurationDataImpl(List<? extends Property<?>> allProperties, Map<String, String[]> sectionComments) {
        this.allProperties = Collections.unmodifiableList(allProperties);
        this.sectionComments = Collections.unmodifiableMap(sectionComments);
    }

    @Override
    public List<Property<?>> getAllProperties() {
        return allProperties;
    }

    @Override
    public String[] getCommentsForSection(String path) {
        String[] comments = sectionComments.get(path);
        return (comments == null) ? new String[0] : comments;
    }
}
