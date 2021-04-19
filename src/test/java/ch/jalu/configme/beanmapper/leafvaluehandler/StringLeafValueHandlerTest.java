package ch.jalu.configme.beanmapper.leafvaluehandler;

import ch.jalu.configme.utils.TypeInformation;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

/**
 * Test for {@link StringLeafValueHandler}.
 */
class StringLeafValueHandlerTest {

    @Test
    void shouldMapToString() {
        // given
        Object input1 = "str";
        Object input2 = Collections.emptyMap();
        Object input3 = null;
        LeafValueHandler transformer = new StringLeafValueHandler();

        // when / then
        assertThat(transformer.convert(new TypeInformation(String.class), input1), equalTo(input1));
        assertThat(transformer.convert(new TypeInformation(String.class), input2), nullValue());
        assertThat(transformer.convert(new TypeInformation(String.class), input3), nullValue());
    }
}
