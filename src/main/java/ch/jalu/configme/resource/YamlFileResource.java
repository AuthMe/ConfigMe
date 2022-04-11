package ch.jalu.configme.resource;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.resource.PropertyPathTraverser.PathElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class YamlFileResource implements PropertyResource {

    private final Path path;
    private final @NotNull YamlFileResourceOptions options;
    private final String indentationSpace;
    private @Nullable Yaml yamlObject;

    public YamlFileResource(@NotNull Path path) {
        this(path, YamlFileResourceOptions.builder().build());
    }

    public YamlFileResource(@NotNull Path path, @NotNull YamlFileResourceOptions options) {
        this.path = path;
        this.options = options;
        this.indentationSpace = options.getIndentation();
    }

    /**
     * Constructor (legacy). Prefer {@link #YamlFileResource(Path)}.
     *
     * @param file the file
     * @deprecated scheduled for removal
     */
    @Deprecated
    public YamlFileResource(@NotNull File file) {
        this(file.toPath());
    }

    @Override
    public @NotNull PropertyReader createReader() {
        return new YamlFileReader(path, options.getCharset(), options.splitDotPaths());
    }

    @Override
    public void exportProperties(@NotNull ConfigurationData configurationData) {
        try (OutputStream os = Files.newOutputStream(path);
             OutputStreamWriter writer = new OutputStreamWriter(os, options.getCharset())) {
            PropertyPathTraverser pathTraverser = new PropertyPathTraverser(configurationData);
            for (Property<?> property : configurationData.getProperties()) {
                final Object exportValue = getExportValue(property, configurationData);
                exportValue(writer, pathTraverser, Arrays.asList(property.getPath().split("\\.")), exportValue);
            }
            writer.append("\n");
            writer.flush();
        } catch (IOException e) {
            throw new ConfigMeException("Could not save config to '" + path + "'", e);
        } finally {
            onWriteComplete();
        }
    }

    protected final @NotNull Path getPath() {
        return path;
    }

    // Scheduled for removal in favor of #getPath
    @Deprecated
    protected final @NotNull File getFile() {
        return path.toFile();
    }

    /**
     * Exports the given value at the provided path.
     *
     * @param writer the file writer to write with
     * @param pathTraverser the path traverser (e.g. keeps track of which path elements are new)
     * @param pathElements all elements that make up the path to the value
     * @param value the value to export
     * @throws IOException .
     */
    protected void exportValue(@NotNull Writer writer, @NotNull PropertyPathTraverser pathTraverser,
                               @NotNull List<String> pathElements, @Nullable Object value) throws IOException {
        if (value == null) {
            return;
        }

        if (value instanceof Map<?, ?> && !((Map<?, ?>) value).isEmpty()) {
            final Map<String, ?> mapValue = (Map<String, ?>) value;

            for (Map.Entry<String, ?> entry : mapValue.entrySet()) {
                List<String> pathElementsForEntry = combinePathElementsAndMapEntryKey(pathElements, entry.getKey());
                exportValue(writer, pathTraverser, pathElementsForEntry, entry.getValue());
            }
        } else {
            List<PathElement> newPathElements = pathTraverser.getPathElements(pathElements);
            final boolean isRootProperty = newPathElements.size() == 1 && "".equals(newPathElements.get(0).getName());

            for (PathElement pathElement : newPathElements) {
                writeIndentingBetweenLines(writer, pathElement);
                writeComments(writer, pathElement.getIndentationLevel(), pathElement);
                writer.append(getNewLineIfNotFirstElement(pathElement));
                if (!isRootProperty) {
                    writer.append(indent(pathElement.getIndentationLevel()))
                          .append(escapePathElementIfNeeded(pathElement.getName()))
                          .append(":");
                }
            }
            if (!isRootProperty) {
                writer.append(" ");
            }

            writer.append(toYamlIndented(value, newPathElements.get(newPathElements.size() - 1).getIndentationLevel()));
        }
    }

    /**
     * Writes the given comment lines as YAML comments at the given indentation level.
     *
     * @param writer the writer to write with
     * @param indentation the level at which the comment lines should be indented
     * @param pathElement the path element for which the comments are being generated
     * @throws IOException .
     */
    protected void writeComments(@NotNull Writer writer, int indentation, @NotNull PathElement pathElement) throws IOException {
        if (pathElement.getComments().isEmpty()) {
            return;
        }

        String lineStart = pathElement.isFirstElement() ? "" : "\n";
        String commentStart = indent(indentation) + "# ";
        for (String comment : pathElement.getComments()) {
            writer.append(lineStart);
            lineStart = "\n";

            if (!"\n".equals(comment)) {
                writer.append(commentStart)
                      .append(comment);
            }
        }
    }

    /**
     * Combines two path element sources to a new list of path elements: the list of path elements that were given
     * from the parent context and the map entry key from which one or more path elements should be derived.
     *
     * @param parentPathElements the path elements that were previously given
     * @param mapEntryKey the key of a map entry which is added to the path
     * @return path of the map entry based on previous elements and its key
     */
    protected List<String> combinePathElementsAndMapEntryKey(List<String> parentPathElements,
                                                             String mapEntryKey) {
        // If we were at the root just before, parent path elements is an empty string, which needs to be skipped
        Stream<String> parentPathElems = parentPathElements.size() == 1 && "".equals(parentPathElements.get(0))
            ? Stream.empty()
            : parentPathElements.stream();
        // Split map by '.' if so configured, otherwise retain entire key as one additional path element
        Stream<String> pathElemsFromEntryKey = options.splitDotPaths()
            ? Arrays.stream(mapEntryKey.split("\\."))
            : Stream.of(mapEntryKey);
        return Stream.concat(parentPathElems, pathElemsFromEntryKey)
            .collect(Collectors.toList());
    }

    private void writeIndentingBetweenLines(@NotNull Writer writer, @NotNull PathElement pathElement) throws IOException {
        int numberOfEmptyLines = options.getNumberOfEmptyLinesBefore(pathElement);
        for (int i = 0; i < numberOfEmptyLines; ++i) {
            writer.append("\n");
        }
    }

    private @NotNull String getNewLineIfNotFirstElement(@NotNull PathElement pathElement) {
        return pathElement.isFirstElement() && pathElement.getComments().isEmpty() ? "" : "\n";
    }

    /**
     * Returns the value in its YAML representation with an indentation of the given level. Proper indentation
     * should be applied to all lines except for the first one (such that this method's return value can simply
     * be appended to a properly indented property prefix like {@code name:}).
     *
     * @param value the value to convert to YAML
     * @param indent level of indentation to use
     * @return the value as YAML at the given indentation level
     */
    protected @NotNull String toYamlIndented(@Nullable Object value, int indent) {
        String representation = toYaml(value);
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
    protected String toYaml(@Nullable Object value) {
        if (value instanceof String) {
            return getYamlObject().dump(value);
        } else if (value instanceof Collection<?>) {
            List<?> list = collectionToList((Collection<?>) value);
            return list.isEmpty() ? "[]" : "\n" + getYamlObject().dump(list);
        } else if (value instanceof Object[]) {
            Object[] array = (Object[]) value;

            return array.length == 0 ? "[]" : "\n" + getYamlObject().dump(array);
        }
        return getYamlObject().dump(value);
    }

    /**
     * Returns a String of whitespace for indentation in YAML at the given level.
     *
     * @param level the desired level of indentation
     * @return whitespace to prepend to a line for proper indentation
     */
    protected String indent(int level) {
        switch (level) {
            case 0: return "";
            case 1: return indentationSpace;
            case 2: return indentationSpace + indentationSpace;
            case 3: return indentationSpace + indentationSpace + indentationSpace;
            case 4: return indentationSpace + indentationSpace + indentationSpace + indentationSpace;
            case 5: return indentationSpace + indentationSpace + indentationSpace + indentationSpace + indentationSpace;
            default: // proceed
        }

        final StringBuilder result = new StringBuilder(level * indentationSpace.length());
        for (int i = 0; i < level; ++i) {
            result.append(indentationSpace);
        }
        return result.toString();
    }

    protected @NotNull String escapePathElementIfNeeded(@NotNull String path) {
        return getYamlObject().dump(path).trim();
    }

    /**
     * Called at the end of {@link #exportProperties}, regardless whether the execution was successful or not.
     */
    protected void onWriteComplete() {
        yamlObject = null;
    }

    /**
     * Returns the YAML instance with which values are converted to YAML.
     *
     * @return the YAML instance to use
     */
    protected @NotNull Yaml getYamlObject() {
        if (yamlObject == null) {
            yamlObject = createNewYaml();
        }
        return yamlObject;
    }

    protected @NotNull Yaml createNewYaml() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setAllowUnicode(true);
        return new Yaml(options);
    }

    protected final @NotNull YamlFileResourceOptions getOptions() {
        return options;
    }

    private <T> @Nullable Object getExportValue(@NotNull Property<T> property, @NotNull ConfigurationData configurationData) {
        return property.toExportValue(configurationData.getValue(property));
    }

    private static @NotNull List<?> collectionToList(@NotNull Collection<?> collection) {
        return collection instanceof List<?>
            ? (List<?>) collection
            : new ArrayList<>(collection);
    }
}
