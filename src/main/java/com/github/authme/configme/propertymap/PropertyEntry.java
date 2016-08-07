package com.github.authme.configme.propertymap;

import com.github.authme.configme.properties.Property;

/**
 * A property and the comments associated with it.
 */
public class PropertyEntry {

    private final Property<?> property;
    private final String[] comments;

    /**
     * Constructor.
     *
     * @param property the property
     * @param comments the comments
     */
    public PropertyEntry(Property<?> property, String... comments) {
        this.property = property;
        this.comments = comments;
    }

    /**
     * @return the property
     */
    public Property<?> getProperty() {
        return property;
    }

    /**
     * @return the comments
     */
    public String[] getComments() {
        return comments;
    }
}
