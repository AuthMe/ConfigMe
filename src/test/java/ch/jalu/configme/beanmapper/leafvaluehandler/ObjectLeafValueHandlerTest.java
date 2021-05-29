package ch.jalu.configme.beanmapper.leafvaluehandler;

import ch.jalu.configme.utils.TypeInformation;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

/**
 * Test for {@link ObjectLeafValueHandler}.
 */
class ObjectLeafValueHandlerTest {

    private ObjectLeafValueHandler objectHandler = new ObjectLeafValueHandler();

    @Test
    void shouldMapToObject() {
        // given
        Object input1 = "str";
        Object input2 = Collections.emptyMap();
        Object input3 = null;

        // when / then
        assertThat(objectHandler.convert(new TypeInformation(Object.class), input1), equalTo(input1));
        assertThat(objectHandler.convert(new TypeInformation(Object.class), input2), equalTo(input2));
        assertThat(objectHandler.convert(new TypeInformation(Object.class), input3), nullValue());
    }

    @Test
    void shouldNotExport() {
        // given / when / then
        assertThat(objectHandler.toExportValue(new Object()), nullValue());
        assertThat(objectHandler.toExportValue("abc"), nullValue());
        assertThat(objectHandler.toExportValue(null), nullValue());
    }
}
