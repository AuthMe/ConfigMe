package ch.jalu.configme.resource;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helper class for the export of properties: it keeps track of the previously traversed property
 * and returns which path parts are new and defines the level of indentation.
 * <p>
 * For example if the property for path {@code config.datasource.mysql.type} was exported and we now
 * encounter the property for path {@code config.datasource.driver.version}, the newly encountered
 * sections are {@code driver} and {@code version}.
 */
public class PropertyPathTraverser {

    private final ConfigurationData configurationData;
    /** Contains all path elements besides the last, e.g. {datasource, mysql} for "datasource.mysql.table". */
    private List<String> parentPathElements = new ArrayList<>(0);
    private boolean isFirstProperty = true;

    public PropertyPathTraverser(ConfigurationData configurationData) {
        this.configurationData = configurationData;
    }

    /**
     * Returns all path elements for the given property that have not been traversed yet.
     *
     * @param path the property path
     * @return the new path elements
     */
    public List<PathElement> getPathElements(String path) {
        List<String> propertyPath = Arrays.asList(path.split("\\."));
        List<String> commonPathParts = CollectionUtils.filterCommonStart(
            parentPathElements, propertyPath.subList(0, propertyPath.size() - 1));
        List<String> newPathParts = CollectionUtils.getRange(propertyPath, commonPathParts.size());

        parentPathElements = propertyPath.subList(0, propertyPath.size() - 1);

        int indentationLevel = commonPathParts.size();
        String prefix = commonPathParts.isEmpty() ? "" : String.join(".", commonPathParts) + ".";
        return convertToPathElements(indentationLevel, prefix, newPathParts);
    }

    private List<PathElement> convertToPathElements(int indentation, String prefix, List<String> elements) {
        List<PathElement> pathElements = new ArrayList<>(elements.size());
        for (String element : elements) {
            List<String> comments = isFirstProperty
                ? getCommentsIncludingRoot(prefix + element)
                : configurationData.getCommentsForSection(prefix + element);
            pathElements.add(new PathElement(indentation, element, comments));
            prefix += element + ".";
            ++indentation;
        }
        return pathElements;
    }

    private List<String> getCommentsIncludingRoot(String path) {
        isFirstProperty = false;

        List<String> rootComments = configurationData.getCommentsForSection("");
        if ("".equals(path)) {
            return rootComments;
        }
        List<String> sectionComments = configurationData.getCommentsForSection(path);
        // One or the other list might be empty, but we only do this once so we can ignore performance considerations
        if (sectionComments.isEmpty()) {
            return rootComments;
        }
        List<String> allComments = new ArrayList<>(rootComments);
        allComments.addAll(sectionComments);
        return allComments;
    }

    public static final class PathElement {
        public final int indentationLevel;
        public final String name;
        public final List<String> comments;

        public PathElement(int indentationLevel, String name, List<String> comments) {
            this.indentationLevel = indentationLevel;
            this.name = name;
            this.comments = comments;
        }
    }

}
