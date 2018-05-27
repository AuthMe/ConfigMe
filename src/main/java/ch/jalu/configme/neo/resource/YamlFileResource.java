package ch.jalu.configme.neo.resource;

import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.neo.configurationdata.ConfigurationData;
import ch.jalu.configme.neo.properties.Property;
import ch.jalu.configme.neo.resource.PropertyPathTraverser.PathElement;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;

public class YamlFileResource implements PropertyResource {

    private static final String INDENTATION = "    ";

    protected final File file;
    private Yaml simpleYaml;
    private Yaml singleQuoteYaml;

    public YamlFileResource(File file) {
        this.file = file;
    }

    @Override
    public PropertyReader createReader() {
        return new YamlFileReader(file);
    }

    @Override
    public void exportProperties(ConfigurationData configurationData) {
        try (Writer writer = new FileWriter(file)) {
            PropertyPathTraverser pathTraverser = new PropertyPathTraverser(configurationData);
            for (Property<?> property : configurationData.getProperties()) {
                final Object exportValue = getExportValue(property, configurationData);
                if (exportValue == null) {
                    continue;
                }

                List<PathElement> pathElements = pathTraverser.getPathElements(property);
                for (PathElement pathElement : pathElements) {
                    writeComments(writer, pathElement.indentationLevel, pathElement.comments);
                    writer.append("\n")
                        .append(indent(pathElement.indentationLevel))
                        .append(pathElement.name)
                        .append(":");
                }

                writer.append(" ")
                    .append(toYaml(property, exportValue, pathElements.get(pathElements.size() - 1).indentationLevel));
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

    private void writeComments(Writer writer, int indentation, List<String> comments) throws IOException {
        if (comments.isEmpty()) {
            return;
        }
        String commentStart = "\n" + indent(indentation) + "# ";
        for (String comment : comments) {
            writer.append(commentStart).append(comment);
        }
    }

    private String toYaml(Property<?> property, @Nullable Object value, int indent) {
        String representation = transformValue(property, value);
        String[] lines = representation.split("\\n");
        return String.join("\n" + indent(indent), lines);
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
    protected String transformValue(Property<?> property, @Nullable Object value) {
        if (value instanceof String) {
            return getSingleQuoteYaml().dump(value);
        } else if (value instanceof Collection<?>) {
            return ((Collection<?>) value).isEmpty() ? "[]" : "\n" + getSimpleYaml().dump(value);
        }
        return getSimpleYaml().dump(value);
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

    private <T> Object getExportValue(Property<T> property, ConfigurationData configurationData) {
        return property.toExportRepresentation(configurationData.getValue(property));
    }

    protected Yaml newYaml(boolean useSingleQuotes) {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setAllowUnicode(true);
        if (useSingleQuotes) {
            options.setDefaultScalarStyle(DumperOptions.ScalarStyle.SINGLE_QUOTED);
        }
        return new Yaml(options);
    }
}
