package ch.jalu.configme.neo.configurationdata;

import ch.jalu.configme.neo.properties.Property;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Contains information about the available properties and their associated comments.
 */
// TODO: Do we really need an interface for this? This class is probably fine on its own and would keep old code working.
public class ConfigurationDataImpl implements ConfigurationData {

    private final List<Property<?>> properties;
    private final Map<String, List<String>> sectionComments;

    public ConfigurationDataImpl(List<? extends Property<?>> allProperties) {
        this(allProperties, Collections.emptyMap());
    }

    public ConfigurationDataImpl(List<? extends Property<?>> allProperties, Map<String, List<String>> sectionComments) {
        this.properties = Collections.unmodifiableList(allProperties);
        this.sectionComments = Collections.unmodifiableMap(sectionComments);
    }

    @Override
    public List<Property<?>> getProperties() {
        return properties;
    }

    @Override
    public List<String> getCommentsForSection(String path) {
        return sectionComments.getOrDefault(path, Collections.emptyList());
    }
}
