package ch.jalu.configme.properties.inlinearray;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link StandardInlineArrayConverters}.
 */
@RunWith(Parameterized.class)
public class StandardInlineArrayConvertersTest {

    @Parameterized.Parameter(value = 0)
    public String name;
    @Parameterized.Parameter(value = 1)
    public InlineArrayConverter converter;
    @Parameterized.Parameter(value = 2)
    public TestData testData;

    @Test
    public void shouldConvertValueFromString() {
        // given
        String input = testData.inputValue;

        // when
        Object[] result = converter.fromString(input);

        // then
        assertThat(result, equalTo(testData.expectedValue));
    }

    @Test
    public void shouldExportValue() {
        // given
        Object[] values = testData.expectedValue;

        // when
        String result = converter.toExportValue(values);

        // then
        assertThat(result, equalTo(testData.expectedExport));
    }

    @Test
    public void shouldNotThrowErrorForInvalidValues() {
        // given
        String input = testData.inputWithErrors;

        // when
        Object[] result = converter.fromString(input);

        // then
        assertThat(result, equalTo(testData.expectedValueWithErrors));
    }

    @Test
    public void shouldConvertFromEmptyString() {
        // given / when
        Object[] result = converter.fromString("");

        // then
        if (converter == StandardInlineArrayConverters.STRING) {
            assertThat(result, equalTo(new String[]{""}));
        } else {
            assertThat(result, emptyArray());
        }
    }

    @Test
    public void shouldExportEmptyArray() {
        // given
        Object[] input = converter == StandardInlineArrayConverters.STRING ? new String[0] : new Object[0];

        // when
        String result = converter.toExportValue(input);

        // then
        assertThat(result, equalTo(""));
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> collectStandardInlineConverters() throws IllegalAccessException {
        List<Object[]> converters = new ArrayList<>();
        for (Field field : StandardInlineArrayConverters.class.getDeclaredFields()) {
            if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                StandardInlineArrayConverters converter = (StandardInlineArrayConverters) field.get(null);
                converters.add(new Object[]{field.getName(), converter, getTestData(converter)});
            }
        }
        return converters;
    }

    private static TestData getTestData(StandardInlineArrayConverters converter) {
        TestData testData = new TestData();
        if (converter == StandardInlineArrayConverters.LONG) {
            testData.setInputAndExpected("3, 4,  -44,", "3, 4, -44", 3L, 4L, -44L);
            testData.setInputWithErrors("3, a, 4.5, 2, -b", 3L, 2L);
        } else if (converter == StandardInlineArrayConverters.INTEGER) {
            testData.setInputAndExpected("3, 4,  -44,", "3, 4, -44", 3, 4, -44);
            testData.setInputWithErrors("3, a, 4.5, 2, -b", 3, 2);
        } else if (converter == StandardInlineArrayConverters.FLOAT) {
            testData.setInputAndExpected("3, 4.5,  -445.68233,", "3.0, 4.5, -445.68234", 3f, 4.5f, -445.68234f);
            testData.setInputWithErrors("3, a, 4.5, -2, -b", 3f, 4.5f, -2f);
        } else if (converter == StandardInlineArrayConverters.DOUBLE) {
            testData.setInputAndExpected("3, 4.5,  -445.68234,", "3.0, 4.5, -445.68234", 3.0, 4.5, -445.68234);
            testData.setInputWithErrors("3, a, 4.5, -2, -b", 3.0, 4.5, -2.0);
        } else if (converter == StandardInlineArrayConverters.SHORT) {
            testData.setInputAndExpected("3, 4,  -44,", "3, 4, -44", (short) 3, (short) 4, (short) -44);
            testData.setInputWithErrors("3, a, 4.5, 2, -b", (short) 3, (short) 2);
        } else if (converter == StandardInlineArrayConverters.BYTE) {
            testData.setInputAndExpected("3, 9999, 4,  -44,", "3, 4, -44", (byte) 3, (byte) 4, (byte) -44);
            testData.setInputWithErrors("3, a, 4.5, 2, -b", (byte) 3, (byte) 2);
        } else if (converter == StandardInlineArrayConverters.BOOLEAN) {
            testData.setInputAndExpected("true, false, ,, true", "true, false, true", true, false, true);
            testData.setInputWithErrors("TRUE, something, else, 43, true, -1", true, false, false, false, true, false);
        } else if (converter == StandardInlineArrayConverters.STRING) {
            testData.setInputAndExpected("a\nb\nLong test string\nd", "a\nb\nLong test string\nd",
                "a", "b", "Long test string", "d");
            String someString = "An even longer String\twith a tab";
            // Note this peculiarity: String#split does not include any last elements which are empty
            testData.setInputWithErrors(someString + "\n\n", someString);
        } else {
            throw new IllegalStateException("Unhandled converter '" + converter + "'");
        }
        return testData;
    }

    private static final class TestData {
        private String inputValue;
        private Object[] expectedValue;
        private String expectedExport;

        private String inputWithErrors;
        private Object[] expectedValueWithErrors;

        void setInputAndExpected(String input, String expectedExport, Object... expectedValues) {
            this.inputValue = input;
            this.expectedExport = expectedExport;
            this.expectedValue = createArray(expectedValues);
        }

        void setInputWithErrors(String inputWithErrors, Object... expectedValues) {
            this.inputWithErrors = inputWithErrors;
            this.expectedValueWithErrors = createArray(expectedValues);
        }

        private static Object[] createArray(Object... values) {
            Class<?> clazz = values[0].getClass();
            Object[] arr = (Object[]) Array.newInstance(clazz, values.length);
            System.arraycopy(values, 0, arr, 0, values.length);
            return arr;
        }
    }
}
