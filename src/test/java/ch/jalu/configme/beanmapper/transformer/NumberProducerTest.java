package ch.jalu.configme.beanmapper.transformer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link Transformers.NumberProducer}.
 */
@RunWith(Parameterized.class)
public class NumberProducerTest {

    private final Object value;
    private final Class<?> clazz;

    public NumberProducerTest(Object value, Class<?> clazz) {
        this.value = value;
        this.clazz = clazz;
    }

    @Test
    public void shouldReturnTypedNumber() {
        // given
        Transformers.NumberProducer numberProducer = new Transformers.NumberProducer();

        // when
        Number result = numberProducer.transform(clazz, null, value);

        // then
        Number expectedResult = expectedValue(value, clazz);
        if (expectedResult == null) {
            assertThat(result, nullValue());
        } else {
            assertThat(result, instanceOf(clazz));
            assertThat(result, equalTo(expectedResult));
        }
    }

    @Parameterized.Parameters(name = "{0} to {1}")
    public static List<Object[]> getParameters() {
        byte b = 8;
        short s = 32767;
        int i = -157;
        long l = 23857L;
        float f = -24456.248f;
        double d = 42_000_000.32;
        BigDecimal bd = new BigDecimal("14");
        Number[] numbers = {b, s, i, l, f, d, bd};

        Class<?>[] classes = {byte.class, short.class, int.class, long.class, float.class, double.class,
                              Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class,
                              BigDecimal.class};
        List<Object[]> params = new ArrayList<>();
        for (Class<?> clazz : classes) {
            for (Number number : numbers) {
                params.add(new Object[]{number, clazz});
            }
        }
        params.add(new Object[]{"test", Short.class});
        params.add(new Object[]{14, String.class});
        params.add(new Object[]{null, int.class});
        return params;
    }

    private static Number expectedValue(Object value, Class<?> clazz) {
        if (!(value instanceof Number)) {
            return null;
        }

        Number n = (Number) value;
        if (clazz == byte.class || clazz == Byte.class) {
            return n.byteValue();
        } else if (clazz == short.class || clazz == Short.class) {
            return n.shortValue();
        } else if (clazz == int.class || clazz == Integer.class) {
            return n.intValue();
        } else if (clazz == long.class || clazz == Long.class) {
            return n.longValue();
        } else if (clazz == float.class || clazz == Float.class) {
            return n.floatValue();
        } else if (clazz == double.class || clazz == Double.class) {
            return n.doubleValue();
        } else if (clazz == BigDecimal.class) {
            return n instanceof BigDecimal ? n : null;
        } else if (clazz == String.class) {
            return null;
        }
        throw new IllegalArgumentException("For class '" + clazz + "'");
    }
}
