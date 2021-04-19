package ch.jalu.configme.beanmapper.leafvaluehandler;

import ch.jalu.configme.samples.TestEnum;
import ch.jalu.configme.utils.TypeInformation;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

/**
 * Test for {@link EnumLeafValueHandler}.
 */
class EnumLeafValueHandlerTest {

    private final EnumLeafValueHandler enumHandler = new EnumLeafValueHandler();

    @Test
    void shouldMapToEnum() {
        // given
        TypeInformation testEnumType = new TypeInformation(TestEnum.class);
        String input1 = TestEnum.SECOND.name();
        String input2 = TestEnum.SECOND.name() + "bogus";
        String input3 = null;

        // when / then
        assertThat(enumHandler.convert(testEnumType, input1), equalTo(TestEnum.SECOND));
        assertThat(enumHandler.convert(testEnumType, input2), nullValue());
        assertThat(enumHandler.convert(testEnumType, input3), nullValue());
    }

    @Test
    void shouldNotConvertToUnsupportedTypes() {
        // given / when / then
        assertThat(enumHandler.convert(new TypeInformation(String.class), "THIRD"), nullValue());
        assertThat(enumHandler.convert(new TypeInformation(Integer.class), null), nullValue());
        assertThat(enumHandler.convert(new TypeInformation(Double.class), true), nullValue());
        assertThat(enumHandler.convert(new TypeInformation(TestEnum[].class), "false"), nullValue());
    }

    @Test
    void shouldExportEnumValue() {
        // given / when / then
        assertThat(enumHandler.toExportValue(TestEnum.THIRD), equalTo("THIRD"));
        assertThat(enumHandler.toExportValue(null), nullValue());
        assertThat(enumHandler.toExportValue("bogus"), nullValue());
        assertThat(enumHandler.toExportValue(27.5), nullValue());
    }
}
