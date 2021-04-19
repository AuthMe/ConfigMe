package ch.jalu.configme.beanmapper.leafvaluehandler;

import ch.jalu.configme.utils.TypeInformation;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

/**
 * Test for {@link BooleanLeafValueHandler}.
 */
class BooleanLeafValueHandlerTest {

    @Test
    void shouldMapToBoolean() {
        // given
        Object input1 = true;
        Object input2 = Collections.emptyMap();
        Object input3 = null;
        LeafValueHandler transformer = new BooleanLeafValueHandler();

        // when / then
        assertThat(transformer.convert(new TypeInformation(Boolean.class), input1), equalTo(true));
        assertThat(transformer.convert(new TypeInformation(Boolean.class), input2), nullValue());
        assertThat(transformer.convert(new TypeInformation(Boolean.class), input3), nullValue());

        assertThat(transformer.convert(new TypeInformation(boolean.class), input1), equalTo(true));
        assertThat(transformer.convert(new TypeInformation(boolean.class), input2), nullValue());
        assertThat(transformer.convert(new TypeInformation(boolean.class), input3), nullValue());
    }
}
