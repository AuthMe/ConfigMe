package ch.jalu.configme.beanmapper.leafvaluehandler;

import ch.jalu.configme.samples.TestEnum;
import ch.jalu.configme.utils.TypeInformation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

/**
 * Test for {@link StringLeafValueHandler}.
 */
class StringLeafValueHandlerTest {

    private StringLeafValueHandler stringHandler = new StringLeafValueHandler();

    @Test
    void shouldMapToString() {
        // given
        TypeInformation stringType = new TypeInformation(String.class);

        // when / then
        assertThat(stringHandler.convert(stringType, "a text"), equalTo("a text"));
        assertThat(stringHandler.convert(stringType, Boolean.TRUE), equalTo("true"));
        assertThat(stringHandler.convert(stringType, 554), equalTo("554"));
        assertThat(stringHandler.convert(stringType, -277.54), equalTo("-277.54"));

        assertThat(stringHandler.convert(stringType, null), nullValue());
        assertThat(stringHandler.convert(stringType, TestEnum.SECOND), nullValue());
        assertThat(stringHandler.convert(stringType, new Object()), nullValue());
        assertThat(stringHandler.convert(stringType, Collections.emptyMap()), nullValue());
    }

    @Test
    void shouldNotConvertUnsupportedTypes() {
        // given / when / then
        assertThat(stringHandler.convert(new TypeInformation(BigDecimal.class), 34), nullValue());
        assertThat(stringHandler.convert(new TypeInformation(BigInteger.class), "87654"), nullValue());
        assertThat(stringHandler.convert(new TypeInformation(TestEnum.class), null), nullValue());
        assertThat(stringHandler.convert(new TypeInformation(Boolean.class), false), nullValue());
    }

    @Test
    void shouldExportStrings() {
        // given / when / then
        assertThat(stringHandler.toExportValue("str"), equalTo("str"));
        assertThat(stringHandler.toExportValue(null), nullValue());
        assertThat(stringHandler.toExportValue(7), nullValue());
        assertThat(stringHandler.toExportValue(TestEnum.THIRD), nullValue());
    }
}
