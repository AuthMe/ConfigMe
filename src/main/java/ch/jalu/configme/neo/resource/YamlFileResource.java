package ch.jalu.configme.neo.resource;

import ch.jalu.configme.neo.configurationdata.ConfigurationData;
import ch.jalu.configme.neo.exception.ConfigMeException;
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
import java.util.Map;

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
                exportValue(writer, pathTraverser, property.getPath(), exportValue);
            }
            writer.flush();
        } catch (IOException e) {
            throw new ConfigMeException("Could not save config to '" + file.getPath() + "'", e);
        } finally {
            simpleYaml = null;
            singleQuoteYaml = null;
        }
    }

    protected void exportValue(Writer writer, PropertyPathTraverser pathTraverser,
                               String path, Object value) throws IOException {
        if (value == null) {
            return;
        }

        if (value instanceof Map<?, ?>) {
            final String pathPrefix = path.isEmpty() ? "" : path + ".";
            for (Map.Entry<String, ?> entry : ((Map<String, ?>) value).entrySet()) {
                exportValue(writer, pathTraverser, pathPrefix + entry.getKey(), entry.getValue());
            }
        } else {
            List<PathElement> pathElements = pathTraverser.getPathElements(path);
            for (PathElement pathElement : pathElements) {
                writeComments(writer, pathElement.indentationLevel, pathElement.comments);
                writer.append("\n")
                    .append(indent(pathElement.indentationLevel))
                    .append(pathElement.name)
                    .append(":");
            }

            writer.append(" ")
                .append(toYaml(value, pathElements.get(pathElements.size() - 1).indentationLevel));
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

    private String toYaml(@Nullable Object value, int indent) {
        String representation = transformValue(value);
        String[] lines = representation.split("\\n");
        return String.join("\n" + indent(indent), lines);
    }

    /**
     * Returns the YAML representation for the given value (belonging to the given value).
     * This method returns the YAML representation of the value only (does not include the key)
     * with no indentation (will be applied afterwards with the appropriate level).
     *
     * @param value the value to transform as YAML
     * @return the YAML representation of the value
     */
    protected String transformValue(@Nullable Object value) { // TODO: find better name?
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
        return property.toExportValue(configurationData.getValue(property));
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
