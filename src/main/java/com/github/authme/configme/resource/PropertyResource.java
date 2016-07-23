package com.github.authme.configme.resource;

import com.github.authme.configme.propertymap.PropertyMap;

import java.util.List;

/**
 * Property resources; provides and exports properties.
 */
public interface PropertyResource {

    // Export
    void exportProperties(PropertyMap propertyMap);


    // Reload / Modification
    void reload();

    void setValue(String path, Object value);


    // Retrieval

    boolean contains(String path);

    Object getObject(String path);

    String getString(String path);

    Integer getInt(String path);

    Double getDouble(String path);

    Boolean getBoolean(String path);

    List<?> getList(String path);

}
