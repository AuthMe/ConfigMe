package ch.jalu.configme.properties.types;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.typeresolver.TypeInfo;
import ch.jalu.typeresolver.numbers.StandardNumberType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ch.jalu.typeresolver.TypeInfo.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Test for {@link NumberType}.
 */
class NumberTypeTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("argsForNumberTypeClasses")
    void shouldDefineWhichClassesAreSupported(NumberType<?> numberType,
                                              Class<?> referenceType,
                                              Class<?> primitiveType) {
        // given / when / then
        assertTrue(numberType.canConvertToType(of(referenceType)));
        if (primitiveType != null) {
            assertTrue(numberType.canConvertToType(of(primitiveType)));
        }
        assertTrue(numberType.canConvertToType(of(Number.class)));
        assertTrue(numberType.canConvertToType(of(Serializable.class)));
        assertTrue(numberType.canConvertToType(of(Object.class)));

        assertFalse(numberType.canConvertToType(of(int[].class)));
        assertFalse(numberType.canConvertToType(of(String.class)));
        assertFalse(numberType.canConvertToType(of(List.class)));
        if (referenceType != BigDecimal.class) {
            assertFalse(numberType.canConvertToType(of(BigDecimal.class)));
        }
        if (primitiveType != int.class) {
            assertFalse(numberType.canConvertToType(of(int.class)));
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("argsForNumberTypeClasses")
    void shouldReturnNumberClassAndResolverNumberType(NumberType<?> numberType, Class<?> referenceType) {
        // given / when
        Class<?> numberClass = numberType.getType();
        ch.jalu.typeresolver.numbers.NumberType<?> internalNumberType = numberType.getTypeResolverNumberType();

        // then
        assertThat(numberClass, equalTo(referenceType));
        assertThat(internalNumberType, notNullValue());
        assertThat(internalNumberType, equalTo(StandardNumberType.fromClass(numberClass)));
    }

    static List<Arguments> argsForNumberTypeClasses() {
        return Arrays.asList(
            Arguments.of(NumberType.BYTE, Byte.class, byte.class),
            Arguments.of(NumberType.SHORT, Short.class, short.class),
            Arguments.of(NumberType.INTEGER, Integer.class, int.class),
            Arguments.of(NumberType.LONG, Long.class, long.class),
            Arguments.of(NumberType.FLOAT, Float.class, float.class),
            Arguments.of(NumberType.DOUBLE, Double.class, double.class),
            Arguments.of(NumberType.BIG_INTEGER, BigInteger.class, null),
            Arguments.of(NumberType.BIG_DECIMAL, BigDecimal.class, null));
    }

    @ParameterizedTest
    @MethodSource("argsForNumberConversions")
    void shouldConvertFromNumber(NumberType<?> numberType,
                                 Number input, Number expectedResult,
                                 boolean isFullyValid) {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when
        Number result = numberType.convert(input, errorRecorder);

        // then
        assertThat(result, equalTo(expectedResult));
        assertThat(errorRecorder.isFullyValid(), equalTo(isFullyValid));
    }

    static List<Arguments> argsForNumberConversions() {
        return Arrays.asList(
            Arguments.of(NumberType.BYTE, 30L, (byte) 30, true),
            Arguments.of(NumberType.BYTE, -6663.2, Byte.MIN_VALUE, false),
            Arguments.of(NumberType.SHORT, -45, (short) -45, true),
            Arguments.of(NumberType.SHORT, -872368723L, Short.MIN_VALUE, false),
            Arguments.of(NumberType.INTEGER, 3.5f, 3, true),
            Arguments.of(NumberType.INTEGER, Long.MAX_VALUE, Integer.MAX_VALUE, false),
            Arguments.of(NumberType.LONG, 42, 42L, true),
            Arguments.of(NumberType.LONG, -Float.MAX_VALUE, Long.MIN_VALUE, false),
            Arguments.of(NumberType.FLOAT, 3, 3f, true),
            Arguments.of(NumberType.FLOAT, Double.MAX_VALUE, Float.MAX_VALUE, false),
            Arguments.of(NumberType.DOUBLE, (byte) 42, 42d, true),
            Arguments.of(NumberType.DOUBLE, new BigDecimal("3E500"), Double.MAX_VALUE, false),
            Arguments.of(NumberType.BIG_INTEGER, 1.1415, BigInteger.ONE, true),
            Arguments.of(NumberType.BIG_INTEGER, -9898762420L, new BigInteger("-9898762420"), true),
            Arguments.of(NumberType.BIG_DECIMAL, 0.69, new BigDecimal("0.69"), true),
            Arguments.of(NumberType.BIG_DECIMAL, BigInteger.valueOf(123456789), new BigDecimal("123456789"), true));
    }

    @ParameterizedTest
    @MethodSource("argsForStringConversions")
    void shouldConvertFromString(NumberType<?> numberType,
                                 String input, Number expectedResult,
                                 boolean isFullyValid) {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when
        Number result = numberType.convert(input, errorRecorder);

        // then
        assertThat(result, equalTo(expectedResult));
        assertThat(errorRecorder.isFullyValid(), equalTo(isFullyValid));
    }

    static List<Arguments> argsForStringConversions() {
        return Arrays.asList(
            Arguments.of(NumberType.BYTE, "30.8", (byte) 30, true),
            Arguments.of(NumberType.BYTE, "-200", Byte.MIN_VALUE, false),
            Arguments.of(NumberType.SHORT, "-45", (short) -45, true),
            Arguments.of(NumberType.SHORT, "872368723", Short.MAX_VALUE, false),
            Arguments.of(NumberType.INTEGER, "3.5", 3, true),
            Arguments.of(NumberType.LONG, "50", 50L, true),
            Arguments.of(NumberType.LONG, "-12345.67", -12345L, true),
            Arguments.of(NumberType.FLOAT, "3", 3f, true),
            Arguments.of(NumberType.FLOAT, "-503.24", -503.24f, true),
            Arguments.of(NumberType.DOUBLE, "0.75", 0.75d, true),
            Arguments.of(NumberType.DOUBLE, "4", 4.0, true),
            Arguments.of(NumberType.BIG_INTEGER, "1.1415", BigInteger.ONE, true),
            Arguments.of(NumberType.BIG_INTEGER, "-9898762420", new BigInteger("-9898762420"), true),
            Arguments.of(NumberType.BIG_DECIMAL, "0.69", new BigDecimal("0.69"), true));
    }

    @ParameterizedTest
    @MethodSource("argsForUnsupportedConversions")
    void shouldReturnNullForUnsupportedValues(NumberType<?> numberType,
                                              Object input) {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when / then
        assertThat(numberType.convert(input, errorRecorder), nullValue());
    }

    static List<Arguments> argsForUnsupportedConversions() {
        return Arrays.asList(
            Arguments.of(NumberType.BYTE, ""),
            Arguments.of(NumberType.SHORT, Collections.singletonList("test")),
            Arguments.of(NumberType.INTEGER, Boolean.FALSE),
            Arguments.of(NumberType.LONG, "35d"),
            Arguments.of(NumberType.FLOAT, new ConvertErrorRecorder()),
            Arguments.of(NumberType.DOUBLE, null),
            Arguments.of(NumberType.BIG_INTEGER, 'a'),
            Arguments.of(NumberType.BIG_DECIMAL, "invalid"),
            Arguments.of(NumberType.BIG_DECIMAL, "7E+34E"));
    }

    @ParameterizedTest
    @MethodSource("argsForExportValues")
    void shouldReturnSameNumberForExportOrStringForBigNumberTypes(NumberType<Number> numberType,
                                                                  Number input, Object expectedExportValue) {
        // given / when
        Object result = numberType.toExportValue(input);

        // then
        assertThat(result, equalTo(expectedExportValue));
    }

    static List<Arguments> argsForExportValues() {
        return Arrays.asList(
            Arguments.of(NumberType.BYTE, (byte) 20, (byte) 20),
            Arguments.of(NumberType.SHORT, (short) -350, (short) -350),
            Arguments.of(NumberType.INTEGER, 65535, 65535),
            Arguments.of(NumberType.LONG, -80_000L, -80_000L),
            Arguments.of(NumberType.FLOAT, 4.35f, 4.35f),
            Arguments.of(NumberType.DOUBLE, 270e3, 270e3),
            Arguments.of(NumberType.BIG_INTEGER, new BigInteger("123456789123456789"), "123456789123456789"),
            Arguments.of(NumberType.BIG_DECIMAL, new BigDecimal("123456789.123456789"), "123456789.123456789"));
    }

    @Test
    void shouldConvertToBigIntegersWithoutLossOfPrecision() {
        // given
        long longImpreciseAsDouble = 4611686018427387903L;
        assertThat(longImpreciseAsDouble, not(equalTo(Double.valueOf(longImpreciseAsDouble).longValue())));
        BigInteger largeBigInteger = newBigInteger("3.141592E+500");

        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);
        TypeInfo bigIntegerType = of(BigInteger.class);

        // when / then
        assertThat(NumberType.BIG_INTEGER.convert(longImpreciseAsDouble, bigIntegerType, errorRecorder), equalTo(new BigInteger("4611686018427387903")));
        assertThat(NumberType.BIG_INTEGER.convert(-1976453120, bigIntegerType, errorRecorder), equalTo(new BigInteger("-1976453120")));
        assertThat(NumberType.BIG_INTEGER.convert(2e50, bigIntegerType, errorRecorder), equalTo(newBigInteger("2E+50")));
        assertThat(NumberType.BIG_INTEGER.convert(1e20d, bigIntegerType, errorRecorder), equalTo(newBigInteger("1E+20")));
        assertThat(NumberType.BIG_INTEGER.convert(1e20f, bigIntegerType, errorRecorder), equalTo(new BigInteger("100000002004087730000")));
        assertThat(NumberType.BIG_INTEGER.convert("88223372036854775807", bigIntegerType, errorRecorder), equalTo(new BigInteger("88223372036854775807")));
        assertThat(NumberType.BIG_INTEGER.convert(largeBigInteger, bigIntegerType, errorRecorder), equalTo(largeBigInteger));
        verifyNoInteractions(errorRecorder);
    }

    @Test
    void shouldConvertToBigDecimalsWithoutLossOfPrecision() {
        // given
        long longImpreciseAsDouble = 5076541234567890123L;
        assertThat(longImpreciseAsDouble, not(equalTo(Double.valueOf(longImpreciseAsDouble).longValue())));
        BigDecimal largeBigDecimal = new BigDecimal("3.0000283746E+422");

        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);
        TypeInfo bigDecimalType = of(BigDecimal.class);

        // when / then
        assertThat(NumberType.BIG_DECIMAL.convert(longImpreciseAsDouble, bigDecimalType, errorRecorder), equalTo(new BigDecimal("5076541234567890123")));
        assertThat(NumberType.BIG_DECIMAL.convert(2e50, bigDecimalType, errorRecorder), equalTo(new BigDecimal("2.0E+50")));
        assertThat(NumberType.BIG_DECIMAL.convert(-1e18f, bigDecimalType, errorRecorder), equalTo(new BigDecimal("-9.9999998430674944E+17")));
        assertThat(NumberType.BIG_DECIMAL.convert("88223372036854775807.999", bigDecimalType, errorRecorder), equalTo(new BigDecimal("88223372036854775807.999")));
        assertThat(NumberType.BIG_DECIMAL.convert(largeBigDecimal, bigDecimalType, errorRecorder), equalTo(largeBigDecimal));
        verifyNoInteractions(errorRecorder);
    }

    @Test
    void shouldExportBigDecimalValuesCorrectly() {
        // given / when / then
        assertThat(NumberType.BIG_DECIMAL.toExportValue(new BigDecimal("0")), equalTo("0"));
        assertThat(NumberType.BIG_DECIMAL.toExportValue(new BigDecimal("-123987.440")), equalTo("-123987.440"));
        assertThat(NumberType.BIG_DECIMAL.toExportValue(new BigDecimal("5.2348997563E+300")), equalTo("5.2348997563E+300"));
        assertThat(NumberType.BIG_DECIMAL.toExportValue(new BigDecimal("9123456789.43214321")), equalTo("9123456789.43214321"));
        assertThat(NumberType.BIG_DECIMAL.toExportValue(new BigDecimal("-9999999999.999999")), equalTo("-9999999999.999999"));
        assertThat(NumberType.BIG_DECIMAL.toExportValue(new BigDecimal("-2E3")), equalTo("-2000"));
        assertThat(NumberType.BIG_DECIMAL.toExportValue(new BigDecimal("7.4718329E40")), equalTo("74718329000000000000000000000000000000000"));
        assertThat(NumberType.BIG_DECIMAL.toExportValue(new BigDecimal("-2.5E+221")), equalTo("-2.5E+221"));

        // BigDecimal toString can have odd, unexpected behavior and depends on the way it was created (as seen above).
        // For completeness, we check a few export outputs from BigDecimals constructed via other methods.
        assertThat(NumberType.BIG_DECIMAL.toExportValue(BigDecimal.valueOf(29384723984.9234)), equalTo("29384723984.9234"));
        assertThat(NumberType.BIG_DECIMAL.toExportValue(BigDecimal.valueOf(8523327856898475L)), equalTo("8523327856898475"));
        assertThat(NumberType.BIG_DECIMAL.toExportValue(BigDecimal.valueOf(-456, -30)), equalTo("-456000000000000000000000000000000"));
        assertThat(NumberType.BIG_DECIMAL.toExportValue(BigDecimal.valueOf(-456, -101)), equalTo("-4.56E+103"));
    }

    @Test
    void shouldNotProduceExportValueForUnhandledTypes() {
        // given / when / then
        assertThat(NumberType.INTEGER.toExportValueIfApplicable(3), equalTo(3));
        assertThat(NumberType.INTEGER.toExportValueIfApplicable(3L), nullValue());
        assertThat(NumberType.INTEGER.toExportValueIfApplicable(3.0), nullValue());
        assertThat(NumberType.INTEGER.toExportValueIfApplicable(new BigDecimal("3")), nullValue());

        assertThat(NumberType.LONG.toExportValueIfApplicable(3), nullValue());
        assertThat(NumberType.LONG.toExportValueIfApplicable(3L), equalTo(3L));
        assertThat(NumberType.LONG.toExportValueIfApplicable(3.0), nullValue());
        assertThat(NumberType.LONG.toExportValueIfApplicable(new BigDecimal("3")), nullValue());

        assertThat(NumberType.DOUBLE.toExportValueIfApplicable(3), nullValue());
        assertThat(NumberType.DOUBLE.toExportValueIfApplicable(3L), nullValue());
        assertThat(NumberType.DOUBLE.toExportValueIfApplicable(3.0), equalTo(3.0));
        assertThat(NumberType.DOUBLE.toExportValueIfApplicable(new BigDecimal("3")), nullValue());

        assertThat(NumberType.BIG_DECIMAL.toExportValueIfApplicable(3), nullValue());
        assertThat(NumberType.BIG_DECIMAL.toExportValueIfApplicable(3L), nullValue());
        assertThat(NumberType.BIG_DECIMAL.toExportValueIfApplicable(3.0), nullValue());
        assertThat(NumberType.BIG_DECIMAL.toExportValueIfApplicable(new BigDecimal("3")), equalTo("3"));
    }

    /**
     * Creates a BigInteger via BigDecimal constructor so that a BigInteger can be created via scientific notation
     * (e.g. 3E5), which the BigInteger constructor does not support.
     *
     * @param value the value as string
     * @return BigInteger containing the given value
     */
    private static BigInteger newBigInteger(String value) {
        return new BigDecimal(value).toBigIntegerExact();
    }
}
