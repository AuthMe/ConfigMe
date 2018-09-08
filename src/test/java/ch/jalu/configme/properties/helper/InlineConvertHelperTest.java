package ch.jalu.configme.properties.helper;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

// Pointless tests, because we know, that return all methods in InlineConvertHelper class.
// But we want to up coverage percent c:
public class InlineConvertHelperTest {

    @Test
    public void shouldReturnStringType() {
        assertThat(
            InlineConvertHelper.stringHelper(),
            equalTo(PrimitiveConvertHelper.DEFAULT_STRING)
        );
    }

    @Test
    public void shouldReturnLongType() {
        assertThat(
            InlineConvertHelper.longHelper(),
            equalTo(PrimitiveConvertHelper.DEFAULT_LONG)
        );
    }

    @Test
    public void shouldReturnIntegerType() {
        assertThat(
            InlineConvertHelper.integerHelper(),
            equalTo(PrimitiveConvertHelper.DEFAULT_INTEGER)
        );
    }

    @Test
    public void shouldReturnDoubleType() {
        assertThat(
            InlineConvertHelper.doubleHelper(),
            equalTo(PrimitiveConvertHelper.DEFAULT_DOUBLE)
        );
    }

    @Test
    public void shouldReturnFloatType() {
        assertThat(
            InlineConvertHelper.floatHelper(),
            equalTo(PrimitiveConvertHelper.DEFAULT_FLOAT)
        );
    }

    @Test
    public void shouldReturnShortType() {
        assertThat(
            InlineConvertHelper.shortHelper(),
            equalTo(PrimitiveConvertHelper.DEFAULT_SHORT)
        );
    }

    @Test
    public void shouldReturnByteType() {
        assertThat(
            InlineConvertHelper.byteHelper(),
            equalTo(PrimitiveConvertHelper.DEFAULT_BYTE)
        );
    }

    @Test
    public void shouldReturnBooleanType() {
        assertThat(
            InlineConvertHelper.booleanHelper(),
            equalTo(PrimitiveConvertHelper.DEFAULT_BOOLEAN)
        );
    }

}
