package ch.jalu.configme.beanmapper.leafvaluehandler;

import ch.jalu.configme.utils.TypeInformation;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link CombiningLeafValueHandler}. See also {@link StandardLeafValueHandlersTest}.
 */
public class CombiningLeafValueHandlerTest {

    @Test
    public void shouldRespectOrderOfTransformers() {
        // given
        LeafValueHandler t1 = new TestValueTransformerImpl("1");
        LeafValueHandler t2 = new TestValueTransformerImpl("2");

        // when
        CombiningLeafValueHandler result1 = new CombiningLeafValueHandler(t1, t2);
        CombiningLeafValueHandler result2 = new CombiningLeafValueHandler(Arrays.asList(t1, t2));

        // then
        assertThat(result1.convert(null, null), equalTo("value_1"));
        assertThat(result1.toExportValue(null), equalTo("export_1"));
        assertThat(result2.convert(null, null), equalTo("value_1"));
        assertThat(result2.toExportValue(null), equalTo("export_1"));
    }

    @Test
    public void shouldGetFirstNonNullResult() {
        // given
        LeafValueHandler t1 = new TestValueTransformerImpl(null);
        LeafValueHandler t2 = new TestValueTransformerImpl("2");
        LeafValueHandler t3 = new TestValueTransformerImpl("3");

        // when
        CombiningLeafValueHandler result = new CombiningLeafValueHandler(t1, t2, t3);

        // then
        assertThat(result.convert(null, null), equalTo("value_2"));
        assertThat(result.toExportValue(null), equalTo("export_2"));
    }

    private static final class TestValueTransformerImpl implements LeafValueHandler {

        private final String suffix;

        private TestValueTransformerImpl(String suffix) {
            this.suffix = suffix;
        }

        @Override
        public Object convert(TypeInformation typeInformation, Object value) {
            return suffix == null ? null : "value_" + suffix;
        }

        @Override
        public Object toExportValue(Object value) {
            return suffix == null ? null : "export_" + suffix;
        }
    }
}
