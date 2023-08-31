package ch.jalu.configme.beanmapper.context;

import ch.jalu.configme.beanmapper.propertydescription.BeanPropertyComments;
import ch.jalu.configme.utils.PathUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Standard implementation of {@link ExportContext}.
 */
public class ExportContextImpl implements ExportContext {

    private final String beanPath;
    private final Set<UUID> usedUniqueCommentIds;

    /**
     * Constructor.
     *
     * @param beanPath path relative to the bean root
     * @param usedUniqueCommentIds set of unique comment UUIDs that have already been used
     */
    protected ExportContextImpl(@NotNull String beanPath, @NotNull Set<UUID> usedUniqueCommentIds) {
        this.beanPath = beanPath;
        this.usedUniqueCommentIds = usedUniqueCommentIds;
    }

    /**
     * Creates an initial context for the export of a bean value.
     *
     * @return root export context
     */
    public static @NotNull ExportContextImpl createRoot() {
        return new ExportContextImpl("", new HashSet<>());
    }

    @Override
    public @NotNull ExportContext createChildContext(@NotNull String path) {
        String childPath = PathUtils.concatSpecifierAware(beanPath, path);
        return new ExportContextImpl(childPath, usedUniqueCommentIds);
    }

    @Override
    public @NotNull String getBeanPath() {
        return beanPath;
    }

    @Override
    public boolean shouldInclude(@NotNull BeanPropertyComments comments) {
        return !comments.getComments().isEmpty()
            && (comments.getUuid() == null || !usedUniqueCommentIds.contains(comments.getUuid()));
    }

    @Override
    public void registerComment(@NotNull BeanPropertyComments comments) {
        if (comments.getUuid() != null) {
            usedUniqueCommentIds.add(comments.getUuid());
        }
    }
}
