package ch.jalu.configme.beanmapper.leafvaluehandler;

import ch.jalu.configme.samples.TestEnum;
import ch.jalu.configme.utils.TypeInformation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

/**
 * Test for {@link BigNumberLeafValueHandler}.
 */
class BigNumberLeafValueHandlerTest {

    private LeafValueHandler bigNumberHandler = new BigNumberLeafValueHandler();

    // For reference, max values for each type:
    // * short 32,767
    // * int 2,147,483,647
    // * long 9,223,372,036,854,775,807
    // * float 3.40282347E+38
    // * double 1.797693134...E+308

    @Test
    void shouldTransformNumbersToBigInteger() {
        // given
        TypeInformation bigIntegerType = new TypeInformation(BigInteger.class);
        long longImpreciseAsDouble = 4611686018427387903L;
        assertThat(longImpreciseAsDouble, not(equalTo(Double.valueOf(longImpreciseAsDouble).longValue())));

        // when / then
        assertThat(bigNumberHandler.convert(bigIntegerType, 3), equalTo(BigInteger.valueOf(3)));
        assertThat(bigNumberHandler.convert(bigIntegerType, 27.88), equalTo(BigInteger.valueOf(27)));
        assertThat(bigNumberHandler.convert(bigIntegerType, longImpreciseAsDouble), equalTo(newBigInteger("4611686018427387903")));
        assertThat(bigNumberHandler.convert(bigIntegerType, -1976453120), equalTo(newBigInteger("-1976453120")));
        assertThat(bigNumberHandler.convert(bigIntegerType, 2e50), equalTo(newBigInteger("2E+50")));
        assertThat(bigNumberHandler.convert(bigIntegerType, 1e20d), equalTo(newBigInteger("1E+20")));
        assertThat(bigNumberHandler.convert(bigIntegerType, 1e20f), equalTo(newBigInteger("100000002004087730000")));
        assertThat(bigNumberHandler.convert(bigIntegerType, (byte) -120), equalTo(BigInteger.valueOf(-120)));
        assertThat(bigNumberHandler.convert(bigIntegerType, (short) 32504), equalTo(BigInteger.valueOf(32504)));

        BigInteger bigInteger = newBigInteger("3.141592E+500");
        assertThat(bigNumberHandler.convert(bigIntegerType, bigInteger), equalTo(bigInteger));
    }

    @Test
    void shouldTransformNumbersToBigDecimal() {
        // given
        TypeInformation bigDecimalType = new TypeInformation(BigDecimal.class);
        long longImpreciseAsDouble = 5076541234567890123L;
        assertThat(longImpreciseAsDouble, not(equalTo(Double.valueOf(longImpreciseAsDouble).longValue())));

        // when / then
        assertThat(bigNumberHandler.convert(bigDecimalType, 6), equalTo(new BigDecimal("6")));
        assertThat(bigNumberHandler.convert(bigDecimalType, -1131.25116), equalTo(new BigDecimal("-1131.25116")));
        assertThat(bigNumberHandler.convert(bigDecimalType, longImpreciseAsDouble), equalTo(new BigDecimal("5076541234567890123")));
        assertThat(bigNumberHandler.convert(bigDecimalType, 2e50), equalTo(new BigDecimal("2E+50")));
        assertThat(bigNumberHandler.convert(bigDecimalType, -1e18f), equalTo(new BigDecimal("-9.9999998430674944E+17")));
        assertThat(bigNumberHandler.convert(bigDecimalType, (byte) 101), equalTo(new BigDecimal("101")));
        assertThat(bigNumberHandler.convert(bigDecimalType, (short) -32724), equalTo(new BigDecimal("-32724")));

        BigDecimal bigDecimal = new BigDecimal("3.0000283746E+422");
        assertThat(bigNumberHandler.convert(bigDecimalType, bigDecimal), equalTo(bigDecimal));
    }

    @Test
    void shouldTransformStringsToBigDecimalAndBigInteger() {
        // given
        TypeInformation bigIntegerType = new TypeInformation(BigInteger.class);
        TypeInformation bigDecimalType = new TypeInformation(BigDecimal.class);

        // when / then
        assertThat(bigNumberHandler.convert(bigIntegerType, "141414"), equalTo(BigInteger.valueOf(141414L)));
        assertThat(bigNumberHandler.convert(bigIntegerType, "88223372036854775807"), equalTo(new BigInteger("88223372036854775807")));
        assertThat(bigNumberHandler.convert(bigIntegerType, "invalid"), nullValue());
        assertThat(bigNumberHandler.convert(bigIntegerType, "7.5"), nullValue());

        assertThat(bigNumberHandler.convert(bigDecimalType, "1234567"), equalTo(new BigDecimal("1234567")));
        assertThat(bigNumberHandler.convert(bigDecimalType, "88223372036854775807.999"), equalTo(new BigDecimal("88223372036854775807.999")));
        assertThat(bigNumberHandler.convert(bigDecimalType, "1.4237E+725"), equalTo(new BigDecimal("1.4237E+725")));
        assertThat(bigNumberHandler.convert(bigDecimalType, "invalid"), nullValue());
        assertThat(bigNumberHandler.convert(bigDecimalType, "7E+34E"), nullValue());
    }

    @Test
    void shouldHandleUnsupportedTypesWhenTransformingToBigIntegerOrBigDecimal() {
        // given
        TypeInformation bigIntegerType = new TypeInformation(BigInteger.class);
        TypeInformation bigDecimalType = new TypeInformation(BigDecimal.class);

        // when / then
        Stream.of(TimeUnit.SECONDS, true, null, new Object()).forEach(invalidParam -> {
            assertThat(bigNumberHandler.convert(bigIntegerType, invalidParam), nullValue());
            assertThat(bigNumberHandler.convert(bigDecimalType, invalidParam), nullValue());
        });
    }

    @Test
    void shouldReturnNullForNonBigNumberType() {
        // given / when / then
        assertThat(bigNumberHandler.convert(new TypeInformation(String.class), "34"), nullValue());
        assertThat(bigNumberHandler.convert(new TypeInformation(Integer.class), "34"), nullValue());
        assertThat(bigNumberHandler.convert(new TypeInformation(Double.class), 34), nullValue());
        assertThat(bigNumberHandler.convert(new TypeInformation(Boolean.class), null), nullValue());
    }

    @Test
    void shouldExportBigIntegerValuesCorrectly() {
        // given /  when / then
        assertThat(bigNumberHandler.toExportValue(new BigInteger("0")), equalTo("0"));
        assertThat(bigNumberHandler.toExportValue(new BigInteger("123987")), equalTo("123987"));
        assertThat(bigNumberHandler.toExportValue(new BigInteger("-16541234560123456789")), equalTo("-16541234560123456789"));
    }

    @Test
    void shouldExportBigDecimalValuesCorrectly() {
        // given / when / then
        assertThat(bigNumberHandler.toExportValue(new BigDecimal("0")), equalTo("0"));
        assertThat(bigNumberHandler.toExportValue(new BigDecimal("-123987.440")), equalTo("-123987.440"));
        assertThat(bigNumberHandler.toExportValue(new BigDecimal("5.2348997563E+300")), equalTo("5.2348997563E+300"));
        assertThat(bigNumberHandler.toExportValue(new BigDecimal("9123456789.43214321")), equalTo("9123456789.43214321"));
        assertThat(bigNumberHandler.toExportValue(new BigDecimal("-9999999999.999999")), equalTo("-9999999999.999999"));
        assertThat(bigNumberHandler.toExportValue(new BigDecimal("-2E3")), equalTo("-2000"));
        assertThat(bigNumberHandler.toExportValue(new BigDecimal("7.4718329E40")), equalTo("74718329000000000000000000000000000000000"));
        assertThat(bigNumberHandler.toExportValue(new BigDecimal("-2.5E+221")), equalTo("-2.5E+221"));

        // BigDecimal toString can have odd, unexpected behavior and depends on the way it was created (as seen above).
        // For completeness, we check a few export outputs from BigDecimals constructed via other methods.
        assertThat(bigNumberHandler.toExportValue(BigDecimal.valueOf(29384723984.9234)), equalTo("29384723984.9234"));
        assertThat(bigNumberHandler.toExportValue(BigDecimal.valueOf(8523327856898475L)), equalTo("8523327856898475"));
        assertThat(bigNumberHandler.toExportValue(BigDecimal.valueOf(-456, -30)), equalTo("-456000000000000000000000000000000"));
        assertThat(bigNumberHandler.toExportValue(BigDecimal.valueOf(-456, -101)), equalTo("-4.56E+103"));
    }

    @Test
    void shouldNotExportUnsupportedTypes() {
        // given / when / then
        assertThat(bigNumberHandler.toExportValue(34), nullValue());
        assertThat(bigNumberHandler.toExportValue(null), nullValue());
        assertThat(bigNumberHandler.toExportValue("453"), nullValue());
        assertThat(bigNumberHandler.toExportValue(TestEnum.SECOND), nullValue());
    }

    @Test
    void shouldNotHandleExtensionsOfBigNumberClasses() {
        // given
        BigDecimal bigDecimalExt = new TestBigDecimalDummyExt("1.41421");
        BigInteger bigIntegerExt = new TestBigIntegerDummyExt("5432");

        // when / then
        assertThat(bigNumberHandler.toExportValue(bigIntegerExt), nullValue());
        assertThat(bigNumberHandler.toExportValue(bigDecimalExt), nullValue());
        assertThat(bigNumberHandler.convert(new TypeInformation(TestBigDecimalDummyExt.class), "2"), nullValue());
        assertThat(bigNumberHandler.convert(new TypeInformation(TestBigIntegerDummyExt.class), "7"), nullValue());
    }

    private static BigInteger newBigInteger(String value) {
        return new BigDecimal(value).toBigIntegerExact();
    }

    private static final class TestBigDecimalDummyExt extends BigDecimal {

        public TestBigDecimalDummyExt(String val) {
            super(val);
        }
    }

    private static final class TestBigIntegerDummyExt extends BigInteger {

        public TestBigIntegerDummyExt(String val) {
            super(val);
        }
    }
}
