package ch.jalu.configme.configurationdata;

import ch.jalu.configme.SettingsHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Allows to register comments (intended via {@link SettingsHolder#registerComments}).
 */
public class CommentsConfiguration {

    private final Map<String, List<String>> comments;

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
    public CommentsConfiguration(Map<String, List<String>> comments) {
        this.comments = comments;
    }

    /**
     * Sets the given lines for the provided path, overriding any previously existing comments for the path.
     *
     * @param path the path to register the comment lines for
     * @param commentLines the comment lines to set for the path
     */
    public void setComment(String path, String... commentLines) {
        comments.put(path, Collections.unmodifiableList(Arrays.asList(commentLines)));
    }

    /**
     * Returns a read-only view of the map with all comments.
     *
     * @return map with all comments
     */
    public Map<String, List<String>> getAllComments() {
        return Collections.unmodifiableMap(comments);
    }
}
