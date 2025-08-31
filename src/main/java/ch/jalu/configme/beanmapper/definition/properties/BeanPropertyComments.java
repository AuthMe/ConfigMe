package ch.jalu.configme.beanmapper.definition.properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Contains the comments for a bean property, with a UUID if the comment should only be included once.
 */
public class BeanPropertyComments {

    /** Instance which can be used if there are no comments to add. */
    public static final BeanPropertyComments EMPTY = new BeanPropertyComments(Collections.emptyList(), null);

    private final List<String> comments;
    private final UUID uuid;

    /**
     * Constructor.
     *
     * @param comments the comments
     * @param uuid UUID to identify the comment with, null if the comment should be repeated
     */
    public BeanPropertyComments(@NotNull List<String> comments, @Nullable UUID uuid) {
        this.comments = comments;
        this.uuid = uuid;
    }

    public @NotNull List<String> getComments() {
        return comments;
    }

    /**
     * UUID to identify this comment. Not-null when the comment should not be repeated if it could be part of the
     * export multiple times.
     *
     * @return UUID if the comment should be unique, null otherwise
     */
    public @Nullable UUID getUuid() {
        return uuid;
    }
}
