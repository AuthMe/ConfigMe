package ch.jalu.configme.beanmapper.leafvaluehandler;

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

    // For reference, max values for each type:
    // * short 32,767
    // * int 2,147,483,647
    // * long 9,223,372,036,854,775,807
    // * float 3.40282347E+38
    // * double 1.797693134...E+308

    @Test
    void shouldTransformNumbersToBigInteger() {
        // given
        LeafValueHandler handler = new BigNumberLeafValueHandler();
        TypeInformation typeInformation = new TypeInformation(BigInteger.class);

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
        LeafValueHandler handler = new BigNumberLeafValueHandler();
        TypeInformation typeInformation = new TypeInformation(BigDecimal.class);

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
        LeafValueHandler handler = new BigNumberLeafValueHandler();
        TypeInformation bigIntegerType = new TypeInformation(BigInteger.class);
        TypeInformation bigDecimalType = new TypeInformation(BigDecimal.class);

        // when / then
        assertThat(handler.convert(bigIntegerType, "141414"), equalTo(BigInteger.valueOf(141414L)));
        assertThat(handler.convert(bigIntegerType, "88223372036854775807"), equalTo(new BigInteger("88223372036854775807")));
        assertThat(handler.convert(bigIntegerType, "invalid"), nullValue());
        assertThat(handler.convert(bigIntegerType, "7.5"), nullValue());

        assertThat(handler.convert(bigDecimalType, "1234567"), equalTo(new BigDecimal("1234567")));
        assertThat(handler.convert(bigDecimalType, "88223372036854775807.999"), equalTo(new BigDecimal("88223372036854775807.999")));
        assertThat(handler.convert(bigDecimalType, "1.4237E+725"), equalTo(new BigDecimal("1.4237E+725")));
        assertThat(handler.convert(bigDecimalType, "invalid"), nullValue());
        assertThat(handler.convert(bigDecimalType, "7E+34E"), nullValue());
    }

    @Test
    void shouldHandleUnsupportedTypesWhenTransformingToBigIntegerOrBigDecimal() {
        // given
        LeafValueHandler handler = new BigNumberLeafValueHandler();
        TypeInformation bigIntegerType = new TypeInformation(BigInteger.class);
        TypeInformation bigDecimalType = new TypeInformation(BigDecimal.class);

        // when / then
        Stream.of(TimeUnit.SECONDS, true, null, new Object()).forEach(invalidParam -> {
            assertThat(handler.convert(bigIntegerType, null), nullValue());
            assertThat(handler.convert(bigDecimalType, null), nullValue());
        });
    }

    @Test
    void shouldExportBigIntegerValuesCorrectly() {
        // given
        LeafValueHandler handler = new BigNumberLeafValueHandler();

        // when / then
        assertThat(handler.toExportValue(new BigInteger("0")), equalTo("0"));
        assertThat(handler.toExportValue(new BigInteger("123987")), equalTo("123987"));
        assertThat(handler.toExportValue(new BigInteger("-16541234560123456789")), equalTo("-16541234560123456789"));
    }

    @Test
    void shouldExportBigDecimalValuesCorrectly() {
        // given
        LeafValueHandler handler = new BigNumberLeafValueHandler();

        // when / then
        assertThat(handler.toExportValue(new BigDecimal("0")), equalTo("0"));
        assertThat(handler.toExportValue(new BigDecimal("-123987.440")), equalTo("-123987.440"));
        assertThat(handler.toExportValue(new BigDecimal("5.2348997563E+300")), equalTo("5.2348997563E+300"));
        assertThat(handler.toExportValue(new BigDecimal("9123456789.43214321")), equalTo("9123456789.43214321"));
        assertThat(handler.toExportValue(new BigDecimal("-9999999999.999999")), equalTo("-9999999999.999999"));
        assertThat(handler.toExportValue(new BigDecimal("-2E3")), equalTo("-2000"));
        assertThat(handler.toExportValue(new BigDecimal("-2.5E+10")), equalTo("-2.5E+10"));
    }

    private static BigInteger newBigInteger(String value) {
        return new BigDecimal(value).toBigIntegerExact();
    }
}
