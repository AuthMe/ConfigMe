package ch.jalu.configme.properties.convertresult;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

/**
 * Test for {@link ValueWithComments}.
 */
class ValueWithCommentsTest {

    @Test
    void shouldUnwrapValue() {
        // given
        Object object1 = new ValueWithComments("test", Arrays.asList("Explanatory", "comments"));
        Object object2 = TimeUnit.SECONDS;

        // when / then
        assertThat(ValueWithComments.unwrapValue(object1), equalTo("test"));
        assertThat(ValueWithComments.unwrapValue(object2), equalTo(TimeUnit.SECONDS));
        assertThat(ValueWithComments.unwrapValue(null), nullValue());
    }

    @Test
    void shouldStreamThroughComments() {
        // given
        Object object1 = new ValueWithComments("test", Arrays.asList("Explanatory", "comments"));
        Object object2 = TimeUnit.SECONDS;

        // when / then
        assertThat(ValueWithComments.streamThroughCommentsIfApplicable(object1).collect(toList()), contains("Explanatory", "comments"));
        assertThat(ValueWithComments.streamThroughCommentsIfApplicable(object2).collect(toList()), empty());
        assertThat(ValueWithComments.streamThroughCommentsIfApplicable(null).collect(toList()), empty());
    }
}
