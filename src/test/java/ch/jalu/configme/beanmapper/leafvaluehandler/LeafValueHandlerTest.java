package ch.jalu.configme.beanmapper.leafvaluehandler;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

/**
 * Test for {@link LeafValueHandler}.
 */
class LeafValueHandlerTest {

    @Test
    void shouldUnwrapReturnNull() {
        // given / when / then
        assertThat(LeafValueHandler.unwrapReturnNull(null), nullValue());
        assertThat(LeafValueHandler.unwrapReturnNull(LeafValueHandler.RETURN_NULL), nullValue());

        assertThat(LeafValueHandler.unwrapReturnNull("null"), equalTo("null"));
        assertThat(LeafValueHandler.unwrapReturnNull(TimeUnit.SECONDS), equalTo(TimeUnit.SECONDS));
        assertThat(LeafValueHandler.unwrapReturnNull(12), equalTo(12));
    }
}
