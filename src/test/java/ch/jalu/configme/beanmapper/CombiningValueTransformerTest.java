package ch.jalu.configme.beanmapper;

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link CombiningValueTransformer}. See also {@link StandardTransformersTest}.
 */
public class CombiningValueTransformerTest {

    @Test
    public void shouldRespectOrderOfTransformers() {
        // given
        ValueTransformer t1 = new TestValueTransformerImpl("1");
        ValueTransformer t2 = new TestValueTransformerImpl("2");

        // when
        CombiningValueTransformer result1 = new CombiningValueTransformer(t1, t2);
        CombiningValueTransformer result2 = new CombiningValueTransformer(Arrays.asList(t1, t2));

        // then
        assertThat(result1.value(null, null), equalTo("value_1"));
        assertThat(result1.toExportValue(null), equalTo("export_1"));
        assertThat(result2.value(null, null), equalTo("value_1"));
        assertThat(result2.toExportValue(null), equalTo("export_1"));
    }

    @Test
    public void shouldGetFirstNonNullResult() {
        // given
        ValueTransformer t1 = new TestValueTransformerImpl(null);
        ValueTransformer t2 = new TestValueTransformerImpl("2");
        ValueTransformer t3 = new TestValueTransformerImpl("3");

        // when
        CombiningValueTransformer result = new CombiningValueTransformer(t1, t2, t3);

        // then
        assertThat(result.value(null, null), equalTo("value_2"));
        assertThat(result.toExportValue(null), equalTo("export_2"));
    }

    private static final class TestValueTransformerImpl implements ValueTransformer {

        private final String suffix;

        private TestValueTransformerImpl(String suffix) {
            this.suffix = suffix;
        }

        @Override
        public Object value(Class<?> clazz, Object value) {
            return suffix == null ? null : "value_" + suffix;
        }

        @Override
        public Object toExportValue(Object value) {
            return suffix == null ? null : "export_" + suffix;
        }
    }
}
