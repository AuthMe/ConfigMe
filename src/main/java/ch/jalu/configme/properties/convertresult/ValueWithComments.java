package ch.jalu.configme.properties.convertresult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Wraps a value and allows to associate it with comments. Can be used as return type from
 * {@link ch.jalu.configme.properties.Property#toExportValue}.
 * <p>
 * Prefer defining comments in {@link ch.jalu.configme.SettingsHolder} classes whenever your comments can be statically
 * defined.
 */
public class ValueWithComments {

    private final Object value;
    private final List<String> comments;
    private final UUID uniqueCommentId;

    /**
     * Constructor.
     * <p>
     * The UUID is associated with the comments and should be not null if the comments should only appear once.
     * Use a random UUID if the comments might appear multiple times, e.g. because they were defined on a type that
     * a property uses in a collection. If repetition is desired, or if you're creating an object in simple cases where
     * the comment cannot be repeated, you can use the constructor {@link #ValueWithComments(Object, List)}.
     *
     * @param value the value to wrap
     * @param comments the comments associated with the value
     * @param uniqueCommentId UUID to identify the comment by, if it should not be repeated (null otherwise)
     */
    public ValueWithComments(@NotNull Object value, @NotNull List<String> comments, @Nullable UUID uniqueCommentId) {
        this.value = value;
        this.comments = comments;
        this.uniqueCommentId = uniqueCommentId;
    }

    /**
     * Constructor.
     *
     * @param value the value to wrap
     * @param comments the comments associated with the value
     */
    public ValueWithComments(@NotNull Object value, @NotNull List<String> comments) {
        this(value, comments, null);
    }

    /**
     * @return the value wrapped by this instance
     */
    public @NotNull Object getValue() {
        return value;
    }

    /**
     * @return the comments associated with the value
     */
    public @NotNull List<String> getComments() {
        return comments;
    }

    /**
     * @return UUID to identify the comments, if the comments should only be included the first time they're encountered
     */
    public @Nullable UUID getUniqueCommentId() {
        return uniqueCommentId;
    }

    /**
     * Unwraps the value and returns the actual value contained in this wrapper if the object is an instance of
     * this class. Otherwise, the same value as the parameter is returned.
     *
     * @param object the object to potentially unwrap
     * @return the value of {@link ValueWithComments} if the object is of this type, otherwise the object itself
     */
    public static @NotNull Object unwrapValue(@NotNull Object object) {
        if (object instanceof ValueWithComments) {
            return ((ValueWithComments) object).getValue();
        }
        return object;
    }

    /**
     * Returns a stream with the comments on the given object, if it is a {@link ValueWithComments} and its comments
     * aren't specified to be unique, or if the comments are encountered for the first time. An empty stream is returned
     * if the object is not an instance of this class, or if the comments are defined to be unique and were already
     * encountered (as determined by the set of used comment IDs).
     *
     * @param object the object to get comments from, if applicable
     * @param usedCommentIds UUIDs of comments which should not be repeated that have already been included
     * @return stream with the comments (never null)
     */
    public static @NotNull Stream<String> streamThroughCommentsIfApplicable(@Nullable Object object,
                                                                            @Nullable Set<UUID> usedCommentIds) {
        if (object instanceof ValueWithComments) {
            ValueWithComments valueWithComments = (ValueWithComments) object;
            if (valueWithComments.getUniqueCommentId() == null
                    || usedCommentIds == null
                    || usedCommentIds.add(valueWithComments.getUniqueCommentId())) {
                return valueWithComments.getComments().stream();
            }
        }
        return Stream.empty();
    }
}
