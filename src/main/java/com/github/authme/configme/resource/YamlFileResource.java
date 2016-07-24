package com.github.authme.configme.resource;

import com.github.authme.configme.exception.ConfigMeException;
import com.github.authme.configme.properties.Property;
import com.github.authme.configme.properties.StringListProperty;
import com.github.authme.configme.propertymap.PropertyMap;
import com.github.authme.configme.utils.CollectionUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Property resource based on a YAML file.
 */
public class YamlFileResource implements PropertyResource {

    private static final String INDENTATION = "    ";
    private static Yaml simpleYaml;
    private static Yaml singleQuoteYaml;

    private final File file;
    private YamlFileReader reader;

    public YamlFileResource(File file) {
        this.file = file;
        this.reader = new YamlFileReader(file);
    }

    @Override
    public boolean contains(String path) {
        return reader.contains(path);
    }

    @Override
    public Object getObject(String path) {
        return reader.getObject(path);
    }

    @Override
    public String getString(String path) {
        return reader.getTypedObject(path, String.class);
    }

    @Override
    public Integer getInt(String path) {
        Number n = reader.getTypedObject(path, Number.class);
        return (n == null)
            ? null
            : n.intValue();
    }

    @Override
    public Double getDouble(String path) {
        Number n = reader.getTypedObject(path, Number.class);
        return (n == null)
            ? null
            : n.doubleValue();
    }

    @Override
    public Boolean getBoolean(String path) {
        return reader.getTypedObject(path, Boolean.class);
    }

    @Override
    public List<?> getList(String path) {
        return reader.getTypedObject(path, List.class);
    }

    @Override
    public void setValue(String path, Object value) {
        reader.set(path, value);
    }

    @Override
    public void reload() {
        reader = new YamlFileReader(file);
    }

    @Override
    public void exportProperties(PropertyMap propertyMap) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("");

            // Contains all but the last node of the setting, e.g. [DataSource, mysql] for "DataSource.mysql.username"
            List<String> currentPath = new ArrayList<>();
            for (Map.Entry<Property<?>, String[]> entry : propertyMap.entrySet()) {
                Property<?> property = entry.getKey();

                // Handle properties
                List<String> propertyPath = Arrays.asList(property.getPath().split("\\."));
                List<String> commonPathParts = CollectionUtils.filterCommonStart(
                    currentPath, propertyPath.subList(0, propertyPath.size() - 1));
                List<String> newPathParts = CollectionUtils.getRange(propertyPath, commonPathParts.size());

                if (commonPathParts.isEmpty()) {
                    writer.append("\n");
                }

                int indentationLevel = commonPathParts.size();
                if (newPathParts.size() > 1) {
                    for (String path : newPathParts.subList(0, newPathParts.size() - 1)) {
                        writer.append("\n")
                            .append(indent(indentationLevel))
                            .append(path)
                            .append(": ");
                        ++indentationLevel;
                    }
                }
                for (String comment : entry.getValue()) {
                    writer.append("\n")
                        .append(indent(indentationLevel))
                        .append("# ")
                        .append(comment);
                }
                writer.append("\n")
                    .append(indent(indentationLevel))
                    .append(CollectionUtils.getRange(newPathParts, newPathParts.size() - 1).get(0))
                    .append(": ")
                    .append(toYaml(property, indentationLevel));

                currentPath = propertyPath.subList(0, propertyPath.size() - 1);
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new ConfigMeException("Could not save config to '" + file.getPath() + "'", e);
        }
    }

    protected String transformValue(Property<?> property, Object value) {
        if (property instanceof StringListProperty) {
            // If the property is a non-empty list we need to append a new line because it will be
            // something like the following, which requires a new line:
            // - 'item 1'
            // - 'second item in list'
            String representation = getSingleQuoteYaml().dump(value);
            return (((Collection<?>) value).isEmpty())
                ? representation
                : "\n" + representation;
        }
        if (value instanceof Enum) {
            return getSingleQuoteYaml().dump(((Enum) value).name());
        }
        if (value instanceof String) {
            return getSingleQuoteYaml().dump(value);
        }
        return getSimpleYaml().dump(value);
    }

    private <T> String toYaml(Property<T> property, int indent) {
        Object value = property.getValue(this);
        String representation = transformValue(property, value);
        String result = "";
        String[] lines = representation.split("\\n");
        for (int i = 0; i < lines.length; ++i) {
            if (i == 0) {
                result = lines[0];
            } else {
                result += "\n" + indent(indent) + lines[i];
            }
        }
        return result;
    }

    private static String indent(int level) {
        String result = "";
        for (int i = 0; i < level; i++) {
            result += INDENTATION;
        }
        return result;
    }

    /**
     * Returns a YAML instance set to export values with the default style.
     *
     * @return YAML instance
     */
    protected Yaml getSimpleYaml() {
        if (simpleYaml == null) {
            simpleYaml = newYaml(false);
        }
        return simpleYaml;
    }

    /**
     * Returns a YAML instance set to export values with single quotes.
     *
     * @return YAML instance
     */
    protected Yaml getSingleQuoteYaml() {
        if (singleQuoteYaml == null) {
            singleQuoteYaml = newYaml(true);
        }
        return singleQuoteYaml;
    }

    private static Yaml newYaml(boolean useSingleQuotes) {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setAllowUnicode(true);
        if (useSingleQuotes) {
            options.setDefaultScalarStyle(DumperOptions.ScalarStyle.SINGLE_QUOTED);
        }
        return new Yaml(options);
    }

}
