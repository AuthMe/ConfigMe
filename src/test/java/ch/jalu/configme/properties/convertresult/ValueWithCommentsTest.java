package ch.jalu.configme.properties.convertresult;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;

/**
 * Test for {@link ValueWithComments}.
 */
class ValueWithCommentsTest {

    @Test
    void shouldUnwrapValue() {
        // given
        Object object1 = new ValueWithComments("test", Arrays.asList("Explanatory", "comments"), null);
        Object object2 = TimeUnit.SECONDS;

        // when / then
        assertThat(ValueWithComments.unwrapValue(object1), equalTo("test"));
        assertThat(ValueWithComments.unwrapValue(object2), equalTo(TimeUnit.SECONDS));
    }

    @Test
    void shouldStreamThroughCommentsWithoutUuidSet() {
        // given
        Object object1 = new ValueWithComments("test", Arrays.asList("Explanatory", "comments"), null);
        Object object2 = new ValueWithComments(144, Arrays.asList("144", "coms"), UUID.randomUUID());
        Object object3 = TimeUnit.SECONDS;

        // when / then
        assertThat(ValueWithComments.streamThroughCommentsIfApplicable(object1, null).collect(toList()), contains("Explanatory", "comments"));
        assertThat(ValueWithComments.streamThroughCommentsIfApplicable(object2, null).collect(toList()), contains("144", "coms"));
        assertThat(ValueWithComments.streamThroughCommentsIfApplicable(object3, null).collect(toList()), empty());
        assertThat(ValueWithComments.streamThroughCommentsIfApplicable(null, null).collect(toList()), empty());
    }

    @Test
    void shouldStreamThroughCommentsIfUnique() {
        // given
        UUID uuid1 = UUID.fromString("0000-00-00-00-001");
        UUID uuid2 = UUID.fromString("0000-00-00-00-002");
        UUID uuid3 = UUID.fromString("0000-00-00-00-003");
        Set<UUID> usedCommentIds = new HashSet<>();
        usedCommentIds.add(uuid3);

        Object object1 = new ValueWithComments(1.0, Arrays.asList("coms", "1"), uuid1);
        Object object2 = new ValueWithComments("2", Arrays.asList("coms", "2"), uuid2);
        Object object3 = new ValueWithComments('3', Arrays.asList("coms", "3"), null);

        // when / then
        assertThat(ValueWithComments.streamThroughCommentsIfApplicable(object1, usedCommentIds).collect(toList()), contains("coms", "1"));
        assertThat(ValueWithComments.streamThroughCommentsIfApplicable(object1, usedCommentIds).collect(toList()), empty());
        assertThat(ValueWithComments.streamThroughCommentsIfApplicable(object2, usedCommentIds).collect(toList()), contains("coms", "2"));
        assertThat(ValueWithComments.streamThroughCommentsIfApplicable(object2, usedCommentIds).collect(toList()), empty());
        assertThat(ValueWithComments.streamThroughCommentsIfApplicable(object3, usedCommentIds).collect(toList()), contains("coms", "3"));
        assertThat(ValueWithComments.streamThroughCommentsIfApplicable(object3, usedCommentIds).collect(toList()), contains("coms", "3"));
    }
}
