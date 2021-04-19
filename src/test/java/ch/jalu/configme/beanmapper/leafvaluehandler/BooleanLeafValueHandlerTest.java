package ch.jalu.configme.beanmapper.leafvaluehandler;

import ch.jalu.configme.samples.TestEnum;
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

    private final BooleanLeafValueHandler booleanHandler = new BooleanLeafValueHandler();

    @Test
    void shouldMapToBoolean() {
        // given
        Object input1 = true;
        Object input2 = Collections.emptyMap();
        Object input3 = null;

        // when / then
        assertThat(booleanHandler.convert(new TypeInformation(Boolean.class), input1), equalTo(true));
        assertThat(booleanHandler.convert(new TypeInformation(Boolean.class), input2), nullValue());
        assertThat(booleanHandler.convert(new TypeInformation(Boolean.class), input3), nullValue());

        assertThat(booleanHandler.convert(new TypeInformation(boolean.class), input1), equalTo(true));
        assertThat(booleanHandler.convert(new TypeInformation(boolean.class), input2), nullValue());
        assertThat(booleanHandler.convert(new TypeInformation(boolean.class), input3), nullValue());
    }

    @Test
    void shouldNotConvertToUnsupportedTypes() {
        // given / when / then
        assertThat(booleanHandler.convert(new TypeInformation(String.class), Boolean.FALSE), nullValue());
        assertThat(booleanHandler.convert(new TypeInformation(Integer.class), "false"), nullValue());
        assertThat(booleanHandler.convert(new TypeInformation(Double.class), true), nullValue());
        assertThat(booleanHandler.convert(new TypeInformation(TestEnum.class), null), nullValue());
    }

    @Test
    void shouldExportBooleans() {
        // given / when / then
        assertThat(booleanHandler.toExportValue(Boolean.FALSE), equalTo(false));
        assertThat(booleanHandler.toExportValue(true), equalTo(true));

        assertThat(booleanHandler.toExportValue(null), nullValue());
        assertThat(booleanHandler.toExportValue(1), nullValue());
        assertThat(booleanHandler.toExportValue("test"), nullValue());
    }
}
