package ch.jalu.configme.beanmapper.leafvaluehandler;

import ch.jalu.configme.utils.TypeInformation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

/**
 * Test for {@link NumberLeafValueHandler}.
 */
class NumberLeafValueHandlerTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("getNumberTypeArguments")
    void shouldMapToNumbers(Class<?> referenceType, Class<?> primitiveType,
                            Object expectedResultForInput1, Object expectedResultForInput4) {
        // given
        Object input1 = 3;
        Object input2 = Collections.emptyMap();
        Object input3 = null;
        Object input4 = -5.9;
        LeafValueHandler transformer = new NumberLeafValueHandler();

        // when / then
        Stream.of(referenceType, primitiveType).forEach(clz -> {
            TypeInformation type = new TypeInformation(clz);
            assertThat(transformer.convert(type, input1), equalTo(expectedResultForInput1));
            assertThat(transformer.convert(type, input2), nullValue());
            assertThat(transformer.convert(type, input3), nullValue());
            assertThat(transformer.convert(type, input4), equalTo(expectedResultForInput4));
        });
    }

    /**
     * Returns a list of arguments to test with: reference class, equivalent primitive class,
     * expected value for input1, expected value for input4.
     */
    private static List<Arguments> getNumberTypeArguments() {
        return Arrays.asList(
            Arguments.of(Byte.class, byte.class, (byte) 3, (byte) -5),
            Arguments.of(Short.class, short.class, (short) 3, (short) -5),
            Arguments.of(Integer.class, int.class, 3, -5),
            Arguments.of(Long.class, long.class, 3L, -5L),
            Arguments.of(Float.class, float.class, 3f, -5.9f),
            Arguments.of(Double.class, double.class, 3d, -5.9d));
    }
}
