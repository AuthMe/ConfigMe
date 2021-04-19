package ch.jalu.configme.beanmapper.leafvaluehandler;

import ch.jalu.configme.beanmapper.command.CommandConfig;
import ch.jalu.configme.samples.TestEnum;
import ch.jalu.configme.utils.TypeInformation;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static ch.jalu.configme.TestUtils.transform;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

/**
 * Test for {@link StandardLeafValueHandlers}.
 */
class StandardLeafValueHandlersTest {

    @Test
    void shouldReturnDefaultTransformerAsSingleton() {
        // given
        LeafValueHandler defaultTransformer = StandardLeafValueHandlers.getDefaultLeafValueHandler();

        // when
        LeafValueHandler result = StandardLeafValueHandlers.getDefaultLeafValueHandler();

        // then
        assertThat(result, sameInstance(defaultTransformer));
    }

    @Test
    void shouldUseAllContainedClassInDefaultTransformer() {
        // given
        Class<?>[] innerClasses = StandardLeafValueHandlers.class.getDeclaredClasses();

        // when
        LeafValueHandler defaultValueTransformer = StandardLeafValueHandlers.getDefaultLeafValueHandler();

        // then
        List<Class<?>> transformerTypesUsed = transform(
            ((CombiningLeafValueHandler) defaultValueTransformer).getHandlers(), Object::getClass);
        assertThat(transformerTypesUsed, containsInAnyOrder(innerClasses));
    }

    @Test
    void shouldMapToEnum() {
        // given
        String input1 = TestEnum.SECOND.name();
        String input2 = TestEnum.SECOND.name() + "bogus";
        String input3 = null;
        LeafValueHandler transformer = StandardLeafValueHandlers.getDefaultLeafValueHandler();

        // when / then
        assertThat(transformer.convert(of(TestEnum.class), input1), equalTo(TestEnum.SECOND));
        assertThat(transformer.convert(of(TestEnum.class), input2), nullValue());
        assertThat(transformer.convert(of(TestEnum.class), input3), nullValue());
    }

    @Test
    void shouldMapToString() {
        // given
        Object input1 = "str";
        Object input2 = Collections.emptyMap();
        Object input3 = null;
        LeafValueHandler transformer = StandardLeafValueHandlers.getDefaultLeafValueHandler();

        // when / then
        assertThat(transformer.convert(of(String.class), input1), equalTo(input1));
        assertThat(transformer.convert(of(String.class), input2), nullValue());
        assertThat(transformer.convert(of(String.class), input3), nullValue());
    }

    @Test
    void shouldMapToBoolean() {
        // given
        Object input1 = true;
        Object input2 = Collections.emptyMap();
        Object input3 = null;
        LeafValueHandler transformer = StandardLeafValueHandlers.getDefaultLeafValueHandler();

        // when / then
        assertThat(transformer.convert(of(Boolean.class), input1), equalTo(true));
        assertThat(transformer.convert(of(Boolean.class), input2), nullValue());
        assertThat(transformer.convert(of(Boolean.class), input3), nullValue());

        assertThat(transformer.convert(of(boolean.class), input1), equalTo(true));
        assertThat(transformer.convert(of(boolean.class), input2), nullValue());
        assertThat(transformer.convert(of(boolean.class), input3), nullValue());
    }

    @Test
    void shouldMapToObject() {
        // given
        Object input1 = "str";
        Object input2 = Collections.emptyMap();
        Object input3 = null;
        LeafValueHandler transformer = StandardLeafValueHandlers.getDefaultLeafValueHandler();

        // when / then
        assertThat(transformer.convert(of(Object.class), input1), equalTo(input1));
        assertThat(transformer.convert(of(Object.class), input2), equalTo(input2));
        assertThat(transformer.convert(of(Object.class), input3), nullValue());
    }

    @Test
    void shouldMapToNumbers() {
        // given
        Object input1 = 3;
        Object input2 = Collections.emptyMap();
        Object input3 = null;
        Object input4 = -5.9;
        LeafValueHandler transformer = StandardLeafValueHandlers.getDefaultLeafValueHandler();

        // when / then
        Stream.of(Integer.class, int.class).forEach(clz -> {
            TypeInformation type = new TypeInformation(clz);
            assertThat(transformer.convert(type, input1), equalTo(3));
            assertThat(transformer.convert(type, input2), nullValue());
            assertThat(transformer.convert(type, input3), nullValue());
            assertThat(transformer.convert(type, input4), equalTo(-5));
        });

        Stream.of(Double.class, double.class).forEach(clz -> {
            TypeInformation type = new TypeInformation(clz);
            assertThat(transformer.convert(type, input1), equalTo(3.0));
            assertThat(transformer.convert(type, input2), nullValue());
            assertThat(transformer.convert(type, input3), nullValue());
            assertThat(transformer.convert(type, input4), equalTo(-5.9));
        });

        Stream.of(Float.class, float.class).forEach(clz -> {
            TypeInformation type = new TypeInformation(clz);
            assertThat(transformer.convert(type, input1), equalTo(3.0f));
            assertThat(transformer.convert(type, input2), nullValue());
            assertThat(transformer.convert(type, input3), nullValue());
            assertThat(transformer.convert(type, input4), equalTo(-5.9f));
        });

        Stream.of(Byte.class, byte.class).forEach(clz -> {
            TypeInformation type = new TypeInformation(clz);
            assertThat(transformer.convert(type, input1), equalTo((byte) 3));
            assertThat(transformer.convert(type, input2), nullValue());
            assertThat(transformer.convert(type, input3), nullValue());
            assertThat(transformer.convert(type, input4), equalTo((byte) -5));
        });

        Stream.of(Short.class, short.class).forEach(clz -> {
            TypeInformation type = new TypeInformation(clz);
            assertThat(transformer.convert(type, input1), equalTo((short) 3));
            assertThat(transformer.convert(type, input2), nullValue());
            assertThat(transformer.convert(type, input3), nullValue());
            assertThat(transformer.convert(type, input4), equalTo((short) -5));
        });

        Stream.of(Long.class, long.class).forEach(clz -> {
            TypeInformation type = new TypeInformation(clz);
            assertThat(transformer.convert(type, input1), equalTo(3L));
            assertThat(transformer.convert(type, input2), nullValue());
            assertThat(transformer.convert(type, input3), nullValue());
            assertThat(transformer.convert(type, input4), equalTo(-5L));
        });
    }

    @Test
    void shouldNotMapUnknownValue() {
        // given
        LeafValueHandler transformer = StandardLeafValueHandlers.getDefaultLeafValueHandler();

        // when / then
        assertThat(transformer.convert(of(TestEnum.class), 45), nullValue());
        assertThat(transformer.convert(of(Map.class), Collections.emptyMap()), nullValue());
        assertThat(transformer.convert(of(Integer[].class), 3.8), nullValue());
        assertThat(transformer.convert(of(String.class), Optional.of("3")), nullValue());
    }

    @Test
    void shouldExportSimpleValues() {
        // given
        LeafValueHandler transformer = StandardLeafValueHandlers.getDefaultLeafValueHandler();

        // when / then
        assertExportValueSameAsInput(transformer, 45);
        assertExportValueSameAsInput(transformer, Boolean.FALSE);
        assertExportValueSameAsInput(transformer, -8.45);
        assertExportValueSameAsInput(transformer, (short) -2);
        assertExportValueSameAsInput(transformer, "test string");
        assertThat(transformer.toExportValue(TestEnum.THIRD), equalTo(TestEnum.THIRD.name()));
    }

    @Test
    void shouldNotExportOtherValues() {
        // given
        LeafValueHandler transformer = StandardLeafValueHandlers.getDefaultLeafValueHandler();

        // when / then
        assertThat(transformer.toExportValue(Optional.of(3253)), nullValue());
        assertThat(transformer.toExportValue(new CommandConfig()), nullValue());
        assertThat(transformer.toExportValue(String.class), nullValue());
        assertThat(transformer.toExportValue(Arrays.asList("", 5)), nullValue());
    }

    // for reference, max values for each type:
    // * short 32,767
    // * int 2,147,483,647
    // * long 9,223,372,036,854,775,807
    // * float 3.40282347E+38
    // * double 1.797693134...E+308

    @Test
    void shouldTransformNumbersToBigInteger() {
        // given
        LeafValueHandler handler = StandardLeafValueHandlers.getDefaultLeafValueHandler();
        TypeInformation typeInformation = of(BigInteger.class);

        long longImpreciseAsDouble = 4611686018427387903L;
        assertThat(longImpreciseAsDouble, not(equalTo(Double.valueOf(longImpreciseAsDouble).longValue())));

        // when / then
        assertThat(handler.convert(typeInformation, 3), equalTo(BigInteger.valueOf(3)));
        assertThat(handler.convert(typeInformation, 27.88), equalTo(BigInteger.valueOf(27)));
        assertThat(handler.convert(typeInformation, longImpreciseAsDouble), equalTo(newBigInteger("4611686018427387903")));
        assertThat(handler.convert(typeInformation, -1976453120), equalTo(newBigInteger("-1976453120")));
        assertThat(handler.convert(typeInformation, 2e50), equalTo(newBigInteger("2E+50")));
        assertThat(handler.convert(typeInformation, 1e20d), equalTo(newBigInteger("1E+20")));
        assertThat(handler.convert(typeInformation, 1e20f), equalTo(newBigInteger("100000002004087730000")));
        assertThat(handler.convert(typeInformation, (byte) -120), equalTo(BigInteger.valueOf(-120)));
        assertThat(handler.convert(typeInformation, (short) 32504), equalTo(BigInteger.valueOf(32504)));

        BigInteger bigInteger = newBigInteger("3.141592E+500");
        assertThat(handler.convert(typeInformation, bigInteger), equalTo(bigInteger));
    }

    @Test
    void shouldTransformNumbersToBigDecimal() {
        // given
        LeafValueHandler handler = StandardLeafValueHandlers.getDefaultLeafValueHandler();
        TypeInformation typeInformation = of(BigDecimal.class);

        long longImpreciseAsDouble = 5076541234567890123L;
        assertThat(longImpreciseAsDouble, not(equalTo(Double.valueOf(longImpreciseAsDouble).longValue())));

        // when / then
        assertThat(handler.convert(typeInformation, 6), equalTo(new BigDecimal("6")));
        assertThat(handler.convert(typeInformation, -1131.25116), equalTo(new BigDecimal("-1131.25116")));
        assertThat(handler.convert(typeInformation, longImpreciseAsDouble), equalTo(new BigDecimal("5076541234567890123")));
        assertThat(handler.convert(typeInformation, 2e50), equalTo(new BigDecimal("2E+50")));
        assertThat(handler.convert(typeInformation, -1e18f), equalTo(new BigDecimal("-9.9999998430674944E+17")));
        assertThat(handler.convert(typeInformation, (byte) 101), equalTo(new BigDecimal("101")));
        assertThat(handler.convert(typeInformation, (short) -32724), equalTo(new BigDecimal("-32724")));

        BigDecimal bigDecimal = new BigDecimal("3.0000283746E+422");
        assertThat(handler.convert(typeInformation, bigDecimal), equalTo(bigDecimal));
    }

    @Test
    void shouldTransformStringsToBigDecimalAndBigInteger() {
        // given
        LeafValueHandler handler = StandardLeafValueHandlers.getDefaultLeafValueHandler();

        // when / then
        assertThat(handler.convert(of(BigInteger.class), "141414"), equalTo(BigInteger.valueOf(141414L)));
        assertThat(handler.convert(of(BigInteger.class), "88223372036854775807"), equalTo(new BigInteger("88223372036854775807")));
        assertThat(handler.convert(of(BigInteger.class), "invalid"), nullValue());
        assertThat(handler.convert(of(BigInteger.class), "7.5"), nullValue());

        assertThat(handler.convert(of(BigDecimal.class), "1234567"), equalTo(new BigDecimal("1234567")));
        assertThat(handler.convert(of(BigDecimal.class), "88223372036854775807.999"), equalTo(new BigDecimal("88223372036854775807.999")));
        assertThat(handler.convert(of(BigDecimal.class), "1.4237E+725"), equalTo(new BigDecimal("1.4237E+725")));
        assertThat(handler.convert(of(BigDecimal.class), "invalid"), nullValue());
        assertThat(handler.convert(of(BigDecimal.class), "7E+34E"), nullValue());
    }

    @Test
    void shouldHandleUnsupportedTypesWhenTransformingToBigIntegerOrBigDecimal() {
        // given
        LeafValueHandler handler = StandardLeafValueHandlers.getDefaultLeafValueHandler();

        // when / then
        Stream.of(TimeUnit.SECONDS, true, null, new Object()).forEach(invalidParam -> {
            assertThat(handler.convert(of(BigInteger.class), null), nullValue());
            assertThat(handler.convert(of(BigDecimal.class), null), nullValue());
        });
    }

    @Test
    void shouldExportBigIntegerValuesCorrectly() {
        // given
        LeafValueHandler handler = StandardLeafValueHandlers.getDefaultLeafValueHandler();

        // when / then
        assertThat(handler.toExportValue(new BigInteger("0")), equalTo("0"));
        assertThat(handler.toExportValue(new BigInteger("123987")), equalTo("123987"));
        assertThat(handler.toExportValue(new BigInteger("-16541234560123456789")), equalTo("-16541234560123456789"));
    }

    @Test
    void shouldExportBigDecimalValuesCorrectly() {
        // given
        LeafValueHandler handler = StandardLeafValueHandlers.getDefaultLeafValueHandler();

        // when / then
        assertThat(handler.toExportValue(new BigDecimal("0")), equalTo("0"));
        assertThat(handler.toExportValue(new BigDecimal("-123987.440")), equalTo("-123987.440"));
        assertThat(handler.toExportValue(new BigDecimal("5.2348997563E+300")), equalTo("5.2348997563E+300"));
        assertThat(handler.toExportValue(new BigDecimal("9123456789.43214321")), equalTo("9123456789.43214321"));
        assertThat(handler.toExportValue(new BigDecimal("-9999999999.999999")), equalTo("-9999999999.999999"));
        assertThat(handler.toExportValue(new BigDecimal("-2E3")), equalTo("-2000"));
        assertThat(handler.toExportValue(new BigDecimal("-2.5E+10")), equalTo("-2.5E+10"));
    }

    private void assertExportValueSameAsInput(LeafValueHandler transformer, Object input) {
        assertThat(transformer.toExportValue(input), sameInstance(input));
    }

    private static TypeInformation of(Type type) {
        return new TypeInformation(type);
    }

    private static BigInteger newBigInteger(String value) {
        return new BigDecimal(value).toBigIntegerExact();
    }
}
