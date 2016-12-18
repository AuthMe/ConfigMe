package ch.jalu.configme.resource;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.properties.Property;
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
     * @param property the property
     * @return the new path elements
     */
    public List<PathElement> getPathElements(Property<?> property) {
        List<String> propertyPath = Arrays.asList(property.getPath().split("\\."));
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
            String[] comments = isFirstProperty ? getCommentsIncludingRoot(prefix + element)
                : configurationData.getCommentsForSection(prefix + element);
            pathElements.add(new PathElement(indentation, element, comments));
            prefix += element + ".";
            ++indentation;
        }
        return pathElements;
    }

    private String[] getCommentsIncludingRoot(String path) {
        isFirstProperty = false;

        String[] rootComments = configurationData.getCommentsForSection("");
        String[] sectionComments = configurationData.getCommentsForSection(path);
        // One or the other array might be empty, but we only do this once so we can ignore performance considerations
        return mergeArrays(rootComments, sectionComments);
    }

    // http://stackoverflow.com/questions/80476/how-can-i-concatenate-two-arrays-in-java
    private static String[] mergeArrays(String[] a, String[] b) {
        final int aLen = a.length;
        final int bLen = b.length;

        String[] c = new String[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    public static final class PathElement {
        public final int indentationLevel;
        public final String name;
        public final String[] comments;

        public PathElement(int indentationLevel, String name, String[] comments) {
            this.indentationLevel = indentationLevel;
            this.name = name;
            this.comments = comments;
        }
    }

}
