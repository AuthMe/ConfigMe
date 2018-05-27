package ch.jalu.configme.neo.configurationdata;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsConfiguration {

    private Map<String, List<String>> comments = new HashMap<>();

    public void setComment(String path, String... commentLines) {
        comments.put(path, Arrays.asList(commentLines));
    }

    public Map<String, List<String>> getAllComments() {
        return comments;
    }

}
