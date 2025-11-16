package ch.jalu.configme.configurationdata;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Allows to register comments (intended via {@link SettingsHolder#registerComments}).
 */
public class CommentsConfiguration {

    private final @NotNull Map<String, List<String>> comments;

    public static final String FOOTER_KEY = "..FOOTER";

    /**
     * Constructor.
     */
    public CommentsConfiguration() {
        this.comments = new HashMap<>();
    }

    /**
     * Constructor.
     *
     * @param comments map to store comments in
     */
    public CommentsConfiguration(@NotNull Map<String, List<String>> comments) {
        this.comments = comments;
    }

    /**
     * Sets the given lines for the provided path, overriding any previously existing comments for the path.
     * An entry that is a sole new-line (i.e. "\n") will result in an empty line without any comment marker.
     *
     * @param path the path to register the comment lines for
     * @param commentLines the comment lines to set for the path
     */
    public void setComment(@NotNull String path, @NotNull String @NotNull ... commentLines) {
        List<String> replaced = comments.put(path, Collections.unmodifiableList(Arrays.asList(commentLines)));
        
        if (replaced != null) {
            String commentAnnotation = "@" + Comment.class.getSimpleName();
            throw new IllegalStateException("Comments for path '" + path + "' have already been registered. Use "
                + commentAnnotation + " on a property field, or one call to CommentsConfiguration#setComment per path");
        }
    }

    /**
     * Returns a read-only view of the map with all comments.
     *
     * @return map with all comments
     */
    public @NotNull @UnmodifiableView Map<String, @UnmodifiableView List<String>> getAllComments() {
        return Collections.unmodifiableMap(comments);
    }

    /**
     * Adds the given lines as footer comments. They will be written at the end of the configuration file.
     *
     * @param commentLines the comment lines to add as footer comments
     */
    public void setFooterComments(@NotNull String... commentLines) {
        setComment(FOOTER_KEY, commentLines);
    }

    /**
     * Adds the given lines as header comments. They will be written at the start of the configuration file.
     *
     * @param commentLines the comment lines to add as header comments
     */
    public void setHeaderComments(@NotNull String... commentLines) {
        setComment("", commentLines);
    }

}
