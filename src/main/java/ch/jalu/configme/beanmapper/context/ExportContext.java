package ch.jalu.configme.beanmapper.context;

import ch.jalu.configme.beanmapper.propertydescription.BeanPropertyComments;
import org.jetbrains.annotations.NotNull;

/**
 * Context used by the bean mapper when a value is exported.
 */
public interface ExportContext {

    /**
     * @return path relative to the bean root that is currently being processed
     */
    @NotNull String getBeanPath();

    /**
     * Creates a child context with the given path as addition to this context's path.
     *
     * @param path the path to add
     * @return child export context
     */
    @NotNull ExportContext createChildContext(@NotNull String path);

    /**
     * Specifies whether the given comments instance should be included in the export in this context. Comments
     * should not be included if they're specified to appear only once and they've already been incorporated.
     *
     * @param comments the comments instance to process
     * @return true if the comments should be included, false if they should be skipped
     */
    boolean shouldInclude(@NotNull BeanPropertyComments comments);

    /**
     * Registers the given comments. Used to keep track of all unique comment's UUIDs that have been processed.
     *
     * @param comments the comments instance to process
     */
    void registerComment(@NotNull BeanPropertyComments comments);

}
