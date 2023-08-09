package ch.jalu.configme.resource;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.resource.PropertyPathTraverser.PathElement;
import ch.jalu.configme.resource.yaml.SnakeYamlNodeBuilder;
import ch.jalu.configme.resource.yaml.SnakeYamlNodeBuilderImpl;
import ch.jalu.configme.resource.yaml.SnakeYamlNodeContainer;
import ch.jalu.configme.resource.yaml.SnakeYamlNodeContainerImpl;
import ch.jalu.configme.utils.StreamUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Node;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class YamlFileResource implements PropertyResource {

    private final Path path;
    private final @NotNull YamlFileResourceOptions options;
    private @Nullable Yaml yamlObject;

    public YamlFileResource(@NotNull Path path) {
        this(path, YamlFileResourceOptions.builder().build());
    }

    public YamlFileResource(@NotNull Path path, @NotNull YamlFileResourceOptions options) {
        this.path = path;
        this.options = options;
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
        SnakeYamlNodeContainer root = createNodeContainerForRoot(configurationData.getCommentsForSection(""));
        PropertyPathTraverser pathTraverser = new PropertyPathTraverser();
        SnakeYamlNodeBuilder nodeBuilder = createNodeBuilder();

        List<Property<?>> properties = configurationData.getProperties();
        for (Property<?> property : properties) {
            Object exportValue = getExportValue(property, configurationData);
            if (exportValue != null) {
                String path = property.getPath();
                List<PathElement> pathElements = pathTraverser.getPathElements(path);
                createAndAddYamlNode(exportValue, path, pathElements, root, configurationData, nodeBuilder);
            }
        }

        Node rootNode;
        if (properties.size() == 1 && "".equals(properties.get(0).getPath())) {
            rootNode = root.getRootValueNode();
        } else {
            rootNode = root.convertToNode(nodeBuilder);
        }

        try (OutputStream os = Files.newOutputStream(path);
             OutputStreamWriter writer = new OutputStreamWriter(os, options.getCharset())) {
            getYamlObject().serialize(rootNode, writer);
        } catch (IOException e) {
            throw new ConfigMeException("Could not save config to '" + path + "'", e);
        } finally {
            onWriteComplete();
        }
    }

    /**
     * Creates a YAML node for the export value and stores it, along with any comments for intermediate paths that
     * have not been visited yet.
     *
     * @param exportValue the export value to store
     * @param path the path the export value is for
     * @param pathElements the path elements of this property's path
     * @param rootContainer the root YAML node container for storing the export value
     * @param configurationData the configuration data (for the retrieval of comments)
     * @param nodeBuilder YAML node builder
     */
    protected void createAndAddYamlNode(@NotNull Object exportValue, @NotNull String path,
                                        @NotNull List<PathElement> pathElements,
                                        @NotNull SnakeYamlNodeContainer rootContainer,
                                        @NotNull ConfigurationData configurationData,
                                        @NotNull SnakeYamlNodeBuilder nodeBuilder) {
        SnakeYamlNodeContainer container = rootContainer;
        for (PathElement pathElement : pathElements) {
            if (pathElement.isEndOfPath()) {
                int emptyLines = options.getNumberOfEmptyLinesBefore(pathElement);
                container.putNode(pathElement.getName(),
                    nodeBuilder.createYamlNode(exportValue, path, configurationData, emptyLines));
            } else {
                container = container.getOrCreateChildContainer(pathElement.getName(),
                    () -> getCommentsForPathElement(configurationData, pathElement));
            }
        }
    }

    @NotNull
    protected List<String> getCommentsForPathElement(@NotNull ConfigurationData configurationData,
                                                     @NotNull PathElement pathElement) {
        return Stream.concat(
                    StreamUtils.repeat("\n", options.getNumberOfEmptyLinesBefore(pathElement)),
                    configurationData.getCommentsForSection(pathElement.getFullPath()).stream())
            .collect(Collectors.toList());
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
        options.setProcessComments(true);
        options.setIndent(this.options.getIndentationSize());
        return new Yaml(options);
    }

    protected final @NotNull YamlFileResourceOptions getOptions() {
        return options;
    }

    protected @NotNull SnakeYamlNodeBuilder createNodeBuilder() {
        return new SnakeYamlNodeBuilderImpl();
    }

    protected @NotNull SnakeYamlNodeContainer createNodeContainerForRoot(@NotNull List<String> rootComments) {
        return new SnakeYamlNodeContainerImpl(rootComments);
    }

    private <T> @Nullable Object getExportValue(@NotNull Property<T> property,
                                                @NotNull ConfigurationData configurationData) {
        return property.toExportValue(configurationData.getValue(property));
    }
}
