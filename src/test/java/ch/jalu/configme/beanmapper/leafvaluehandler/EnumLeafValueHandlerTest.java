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

    @Test
    void shouldMapToEnum() {
        // given
        TypeInformation testEnumType = new TypeInformation(TestEnum.class);
        String input1 = TestEnum.SECOND.name();
        String input2 = TestEnum.SECOND.name() + "bogus";
        String input3 = null;
        LeafValueHandler transformer = new EnumLeafValueHandler();

        // when / then
        assertThat(transformer.convert(testEnumType, input1), equalTo(TestEnum.SECOND));
        assertThat(transformer.convert(testEnumType, input2), nullValue());
        assertThat(transformer.convert(testEnumType, input3), nullValue());
    }
}
