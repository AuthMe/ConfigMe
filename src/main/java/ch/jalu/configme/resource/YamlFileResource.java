package ch.jalu.configme.resource;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.resource.PropertyPathTraverser.PathElement;
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
    private Yaml yamlObject;

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
            onWriteComplete();
        }
    }

    protected void exportValue(Writer writer, PropertyPathTraverser pathTraverser,
                               String path, Object value) throws IOException {
        if (value == null) {
            return;
        }

        if (value instanceof Map<?, ?> && !((Map) value).isEmpty()) {
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
            return getYamlObject().dump(value);
        } else if (value instanceof Collection<?>) {
            return ((Collection<?>) value).isEmpty() ? "[]" : "\n" + getYamlObject().dump(value);
        }
        return getYamlObject().dump(value);
    }

    protected String indent(int level) {
        String result = "";
        for (int i = 0; i < level; i++) {
            result += INDENTATION;
        }
        return result;
    }

    /**
     * Called at the end of {@link #exportProperties}, regardless whteher the execution was successful or not.
     */
    protected void onWriteComplete() {
        yamlObject = null;
    }

    /**
     * Returns the YAML instance with which values are converted to YAML.
     *
     * @return the YAML instance to use
     */
    protected Yaml getYamlObject() {
        if (yamlObject == null) {
            yamlObject = createNewYaml();
        }
        return yamlObject;
    }

    protected Yaml createNewYaml() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setAllowUnicode(true);
        return new Yaml(options);
    }

    private <T> Object getExportValue(Property<T> property, ConfigurationData configurationData) {
        return property.toExportValue(configurationData.getValue(property));
    }
}
