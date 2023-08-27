package ch.jalu.configme.beanmapper.context;

import ch.jalu.configme.beanmapper.propertydescription.BeanPropertyComments;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

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
     * Returns the mutable set of unique comment UUIDs that have already been included in the export. Used to
     * ensure that comments which should only appear once are not present in the export multiple times.
     * <p>
     * Implementations of this context typically just need to provide a mutable HashSet. This set is handled by
     * the mapper itself. In other words, if you're implementing a custom leaf value handler, you typically do not
     * have to worry about comments.
     *
     * @return set of unique comment UUIDs that have already been used
     */
    @NotNull Set<UUID> getUsedUniqueCommentIds();

    /**
     * Specifies whether the given comments instance should be included in the export in this context. Comments
     * should not be included if they're specified to appear only once and they've already been incorporated.
     *
     * @param comments the comments instance to process
     * @return true if the comments should be included, false if they should be skipped
     */
    default boolean shouldInclude(@NotNull BeanPropertyComments comments) {
        return !comments.getComments().isEmpty()
            && (comments.getUuid() == null || !getUsedUniqueCommentIds().contains(comments.getUuid()));
    }

}
