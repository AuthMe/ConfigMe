package ch.jalu.configme.resource;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for the export of properties: it keeps track of the previously traversed property
 * and returns which path parts are new.
 * <p>
 * For example, if the property for path {@code config.datasource.mysql.type} was exported and we now
 * encounter the property for path {@code config.datasource.driver.version}, the newly encountered
 * sections are {@code driver} and {@code version}.
 */
public class PropertyPathTraverser {

    /** The last path that was processed. */
    private String lastPath;
    private boolean isFirstProperty = true;

    /**
     * Returns all path elements of the given path.
     *
     * @param path the path to inspect
     * @return path elements (with useful information)
     */
    public @NotNull List<PathElement> getPathElements(@NotNull String path) {
        String[] pathParts = path.split("\\.");
        int totalParts = pathParts.length;
        int levelOfFirstNewPart = returnLevelOfFirstNewPathElement(path);

        StringBuilder fullPathBuilder = new StringBuilder();
        List<PathElement> pathElements = new ArrayList<>(totalParts);
        int level = 0;
        for (int i = 0; i < totalParts; ++i) {
            fullPathBuilder.append(pathParts[i]);
            PathElement element = new PathElement(level, pathParts[i], fullPathBuilder.toString(), isFirstProperty);
            element.setEndOfPath(i == totalParts - 1);
            element.setFirstOfGroup(levelOfFirstNewPart == level);
            pathElements.add(element);

            ++level;
            fullPathBuilder.append(".");
            isFirstProperty = false;
        }
        lastPath = path;
        return pathElements;
    }

    /**
     * Returns the hierarchy level of the highest path element that is being visited for the first time. For example,
     * if we previously processed {@code config.datasource.mysql.type} and the given path is
     * {@code config.datasource.driver.version}, then the level for the path element "driver" is returned (i.e. 2).
     *
     * @param path the new path
     * @return the level of the first new path element
     */
    private int returnLevelOfFirstNewPathElement(@NotNull String path) {
        if (lastPath == null) {
            return 0;
        }

        int minLength = Math.min(lastPath.length(), path.length());
        int i = 0;
        int level = 0;
        while (i < minLength && path.charAt(i) == lastPath.charAt(i)) {
            if (path.charAt(i) == '.') {
                ++level;
            }
            ++i;
        }
        return level;
    }

    /**
     * Represents the current element of a path which is currently being handled. This consists of a part of a
     * property's path or may be a property's full path.
     */
    public static class PathElement {

        private final int indentationLevel;
        private final String name;
        private final String fullPath;
        private final boolean isFirstElement;
        private boolean isFirstOfGroup;
        private boolean isEndOfPath;

        public PathElement(int indentationLevel, @NotNull String name, @NotNull String fullPath,
                           boolean isFirstElement) {
            this.indentationLevel = indentationLevel;
            this.name = name;
            this.fullPath = fullPath;
            this.isFirstElement = isFirstElement;
        }

        /**
         * @return the hierarchy level of this path element
         */
        public int getIndentationLevel() {
            return indentationLevel;
        }

        /**
         * @return the name of this path element (e.g. "driver")
         */
        public @NotNull String getName() {
            return name;
        }

        /**
         * @return the full path of this element (e.g. "config.datasource.driver")
         */
        public @NotNull String getFullPath() {
            return fullPath;
        }

        /**
         * @return true if this path element is the <b>very first</b> element returned by the traverser; false otherwise
         */
        public boolean isFirstElement() {
            return isFirstElement;
        }

        /**
         * Returns if this path element is the first new path element of a property. For example, if a property
         * {@code config.datasource.mysql.type} was previously processed and we're now processing the path
         * {@code config.datasource.driver.version}, then the path element representing {@code driver} is considered
         * to be the first of the group.
         *
         * @return true if this path element is the first new element of the path, false otherwise
         */
        public boolean isFirstOfGroup() {
            return isFirstOfGroup;
        }

        void setFirstOfGroup(boolean firstOfGroup) {
            isFirstOfGroup = firstOfGroup;
        }

        /**
         * Returns if this path element is at the end, i.e. whether it represents a leaf path that is associated to
         * a property. For example, given a property {@code config.datasource.driver.version}, the path element for
         * {@code version} returns true for this method.
         *
         * @return true if this element is the final part of the given path
         */
        public boolean isEndOfPath() {
            return isEndOfPath;
        }

        void setEndOfPath(boolean isEndOfPath) {
            this.isEndOfPath = isEndOfPath;
        }
    }
}
