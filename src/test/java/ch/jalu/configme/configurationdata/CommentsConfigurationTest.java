package ch.jalu.configme.configurationdata;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for {@link CommentsConfiguration}.
 */
class CommentsConfigurationTest {

    @Test
    void shouldThrowForExistingPath() {
        // given
        final String path = "config.me"; 
        CommentsConfiguration conf = new CommentsConfiguration();
        conf.setComment(path, "Old", "Comments", "1", "2", "3");
        
        // when
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> conf.setComment(path, "New Comment"));

        // then
        assertThat(ex.getMessage(), equalTo("Comment lines already exists for the path 'config.me'"));
        assertThat(conf.getAllComments().keySet(), contains(path));
        assertThat(conf.getAllComments().get(path), contains("New Comment"));
    }

    @Test
    void shouldOverrideExistingComment() {
        // given
        CommentsConfiguration conf = new CommentsConfiguration();
        conf.setComment("com.acme", "Acme comment test");
        conf.setComment("other.path", "Some other", "path I am", "adding");

        // when
        IllegalStateException ex = assertThrows(IllegalStateException.class, ()-> conf.setComment("com.acme", "Acme new comment", "1, 2, 3"));

        // then
        assertThat(ex.getMessage(), equalTo("Comment lines already exists for the path 'com.acme'"));

        Map<String, List<String>> allComments = conf.getAllComments();
        assertThat(allComments.keySet(), containsInAnyOrder("com.acme", "other.path"));
        assertThat(allComments.get("com.acme"), contains("Acme new comment", "1, 2, 3"));
        assertThat(allComments.get("other.path"), contains("Some other", "path I am", "adding")); // other one unchanged
    }

    @Test
    void shouldReturnReadOnlyMap() {
        // given
        CommentsConfiguration conf = new CommentsConfiguration();
        conf.setComment("one", "hello", "moto");
        conf.setComment("two", "hallo", "velo");

        // when
        Map<String, List<String>> allComments = conf.getAllComments();

        // then
        assertThat(allComments.getClass().getName(), equalTo("java.util.Collections$UnmodifiableMap"));
        assertThat(allComments.get("one").getClass().getName(), equalTo("java.util.Collections$UnmodifiableRandomAccessList"));
        assertThat(allComments.get("two").getClass().getName(), equalTo("java.util.Collections$UnmodifiableRandomAccessList"));
    }

    @Test
    void shouldPreserveCommentsFromInputMap() {
        // given
        Map<String, List<String>> originalMap = new HashMap<>();
        originalMap.put("one", Arrays.asList("one", "1"));
        originalMap.put("two", Arrays.asList("two", "2"));
        CommentsConfiguration conf = new CommentsConfiguration(originalMap);

        // when
        conf.setComment("three", "three", "3");

        // then
        Map<String, List<String>> allComments = conf.getAllComments();
        assertThat(allComments.keySet(), containsInAnyOrder("one", "two", "three"));
        assertThat(allComments.get("two"), contains("two", "2"));
    }
}
