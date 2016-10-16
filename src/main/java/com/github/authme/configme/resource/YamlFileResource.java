package com.github.authme.configme.resource;

import com.github.authme.configme.beanmapper.BeanProperty;
import com.github.authme.configme.beanmapper.ConstantCollectionProperty;
import com.github.authme.configme.beanmapper.PropertyEntryGenerator;
import com.github.authme.configme.exception.ConfigMeException;
import com.github.authme.configme.knownproperties.ConfigurationData;
import com.github.authme.configme.properties.Property;
import com.github.authme.configme.properties.StringListProperty;
import com.github.authme.configme.resource.PropertyPathTraverser.PathElement;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Property resource based on a YAML file.
 */
public class YamlFileResource implements PropertyResource {

    private static final String INDENTATION = "    ";

    private final File file;
    private final PropertyReader reader;
    private final PropertyEntryGenerator propertyEntryGenerator;
    private Yaml simpleYaml;
    private Yaml singleQuoteYaml;

    /**
     * Constructor.
     *
     * @param file the config file
     */
    public YamlFileResource(File file) {
        this(file, new YamlFileReader(file), new PropertyEntryGenerator());
    }

    /**
     * Constructor.
     *
     * @param file the config file (the YAML file properties get exported to)
     * @param reader the reader from which the properties' values are read
     * @param propertyEntryGenerator generate of property entries to export bean properties. Can be null
     *                               if you do not use bean properties.
     */
    public YamlFileResource(File file, PropertyReader reader, PropertyEntryGenerator propertyEntryGenerator) {
        this.file = file;
        this.reader = reader;
        this.propertyEntryGenerator = propertyEntryGenerator;
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
    public boolean contains(String path) {
        return reader.getObject(path) != null;
    }

    @Override
    public void setValue(String path, Object value) {
        reader.set(path, value);
    }

    @Override
    public void reload() {
        reader.reload();
    }

    @Override
    public void exportProperties(ConfigurationData configurationData) {
        try (Writer writer = new FileWriter(file)) {
            PropertyPathTraverser pathTraverser = new PropertyPathTraverser(configurationData);
            for (Property<?> property : replaceBeanPropertiesToLeafValues(configurationData.getProperties())) {

                List<PathElement> pathElements = pathTraverser.getPathElements(property);
                for (PathElement pathElement : pathElements) {
                    writeComments(writer, pathElement.indentationLevel, pathElement.comments);
                    writer.append("\n")
                        .append(indent(pathElement.indentationLevel))
                        .append(pathElement.name)
                        .append(":");
                }

                writer.append(" ")
                    .append(toYaml(property, pathElements.get(pathElements.size() - 1).indentationLevel));
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new ConfigMeException("Could not save config to '" + file.getPath() + "'", e);
        } finally {
            simpleYaml = null;
            singleQuoteYaml = null;
        }
    }

    private void writeComments(Writer writer, int indentation, String[] comments) throws IOException {
        if (comments.length == 0) {
            return;
        }
        String commentStart = "\n" + indent(indentation) + "# ";
        for (String comment : comments) {
            writer.append(commentStart).append(comment);
        }
    }

    /**
     * Converts property entries of type {@link BeanProperty} to multiple {@link Property} objects
     * that reflect all concrete values that need to be stored to properly, losslessly export the bean.
     * The property entries are essentially the "leaf nodes" of the bean if viewed as a tree.
     *
     * @param originalList the list of property entries to convert
     * @return list of properties with converted property entries
     */
    @SuppressWarnings("unchecked")
    private List<Property<?>> replaceBeanPropertiesToLeafValues(List<Property<?>> originalList) {
        List<Property<?>> result = new LinkedList<>();
        for (Property<?> entry : originalList) {
            if (entry instanceof BeanProperty<?>) {
                BeanProperty beanProperty = (BeanProperty<?>) entry;
                result.addAll(propertyEntryGenerator.generate(
                    beanProperty, beanProperty.getValue(this)));
            } else {
                result.add(entry);
            }
        }
        return result;
    }

    /**
     * Returns the YAML representation for the given value (belonging to the given value).
     * This method returns the YAML representation of the value only (does not include the key)
     * with no indentation (will be applied afterwards with the appropriate level).
     *
     * @param property the associated property
     * @param value the value to transform as YAML
     * @return the YAML representation of the value
     */
    // For more custom types, you can override this method and implement your custom behavior
    // and call super.transformValue() at the end to handle all types already handled here
    protected String transformValue(Property<?> property, Object value) {
        if (property instanceof StringListProperty) {
            // If the property is a non-empty list we need to append a new line because it will be
            // something like the following, which requires a new line:
            // - 'item 1'
            // - 'second item in list'
            String representation = getSingleQuoteYaml().dump(value);
            return ((Collection<?>) value).isEmpty()
                ? representation
                : "\n" + representation;
        }
        if (property instanceof ConstantCollectionProperty) {
            Property<?>[] properties = (Property<?>[]) value;
            if (properties.length == 0) {
                return "[]";
            }
            String result = "\n";
            for (Property<?> entry : (Property<?>[]) value) {
                result += "\n- " + toYaml(entry, 0);
            }
            return result;
        }
        if (value instanceof Enum<?>) {
            return getSingleQuoteYaml().dump(((Enum<?>) value).name());
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
