package ch.jalu.configme.properties.types;

import ch.jalu.configme.internal.ConversionUtils;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Test for {@link InlineArrayPropertyType}.
 */
@ExtendWith(MockitoExtension.class)
class InlineArrayPropertyTypeTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("data")
    void shouldConvertValueFromString(String name, InlineArrayPropertyType<?> converter, TestData testData) {
        // given
        String input = testData.inputValue;
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when
        Object[] result = converter.convert(input, errorRecorder);

        // then
        assertThat(result, equalTo(testData.expectedValue));
        assertThat(errorRecorder.isFullyValid(), equalTo(true));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("data")
    void shouldExportValue(String name, InlineArrayPropertyType converter, TestData testData) {
        // given
        Object[] values = testData.expectedValue;

        // when
        String result = converter.toExportValue(values);

        // then
        assertThat(result, equalTo(testData.expectedExport));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("data")
    void shouldNotThrowErrorForInvalidValues(String name, InlineArrayPropertyType<?> converter, TestData testData) {
        // given
        String input = testData.inputWithErrors;
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when
        Object[] result = converter.convert(input, errorRecorder);

        // then
        assertThat(result, equalTo(testData.expectedValueWithErrors));
        if (converter != InlineArrayPropertyType.STRING) {
            assertThat(errorRecorder.isFullyValid(), equalTo(false));
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("data")
    void shouldConvertFromEmptyString(String name, InlineArrayPropertyType<?> converter) {
        // given / when
        Object[] result = converter.convert("", new ConvertErrorRecorder());

        // then
        if (converter == InlineArrayPropertyType.STRING) {
            assertThat(result, equalTo(new String[]{""}));
        } else {
            assertThat(result, emptyArray());
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("data")
    void shouldExportEmptyArray(String name, InlineArrayPropertyType converter) {
        // given
        Object[] input = new Object[0];

        // when
        String result = converter.toExportValue(input);

        // then
        assertThat(result, equalTo(""));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("data")
    void shouldNotClaimFullyValidIfHasSeparatorAtEnd(String name, InlineArrayPropertyType<?> converter) {
        // given
        assumeTrue(converter != InlineArrayPropertyType.STRING && converter != InlineArrayPropertyType.BOOLEAN);

        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();
        String value = "2, 3,";

        // when
        Object[] result = converter.convert(value, errorRecorder);

        // then
        assertThat(result, arrayWithSize(2)); // 2, 3
        assertThat(errorRecorder.isFullyValid(), equalTo(false));
    }

    @Test
    void shouldNotConvertIfValueIsNotString() {
        // given
        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);

        // when
        Integer[] result = InlineArrayPropertyType.INTEGER.convert(3, errorRecorder);

        // then
        assertThat(result, nullValue());
        verifyNoInteractions(errorRecorder);
    }

    private static List<Object[]> data() throws IllegalAccessException {
        List<Object[]> converters = new ArrayList<>();
        for (Field field : InlineArrayPropertyType.class.getDeclaredFields()) {
            if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                InlineArrayPropertyType<?> converter = (InlineArrayPropertyType<?>) field.get(null);
                converters.add(new Object[]{field.getName(), converter, getTestData(converter)});
            }
        }
        return converters;
    }

    private static TestData getTestData(InlineArrayPropertyType<?> converter) {
        TestData testData = new TestData();
        if (converter == InlineArrayPropertyType.LONG) {
            testData.setInputAndExpected("3, 4,  -44", "3, 4, -44", 3L, 4L, -44L);
            testData.setInputWithErrors("3, a, 4.5, 2, -b,", 3L, 4L, 2L);
        } else if (converter == InlineArrayPropertyType.INTEGER) {
            testData.setInputAndExpected("3, 4,  -44", "3, 4, -44", 3, 4, -44);
            testData.setInputWithErrors("3, a, 4.5, 2, -b", 3, 4, 2);
        } else if (converter == InlineArrayPropertyType.FLOAT) {
            testData.setInputAndExpected("3, 4.5,  -445.68233", "3.0, 4.5, -445.68234", 3f, 4.5f, -445.68234f);
            testData.setInputWithErrors("3, a, 4.5, -2, -b", 3f, 4.5f, -2f);
        } else if (converter == InlineArrayPropertyType.DOUBLE) {
            testData.setInputAndExpected("3, 4.5,  -445.68234", "3.0, 4.5, -445.68234", 3.0, 4.5, -445.68234);
            testData.setInputWithErrors("3, a, 4.5, -2, -b", 3.0, 4.5, -2.0);
        } else if (converter == InlineArrayPropertyType.SHORT) {
            testData.setInputAndExpected("3, 4,  -44", "3, 4, -44", (short) 3, (short) 4, (short) -44);
            testData.setInputWithErrors("3, a, 4.5, 2, -b,", (short) 3, (short) 4, (short) 2);
        } else if (converter == InlineArrayPropertyType.BYTE) {
            testData.setInputAndExpected("3, 4,  -44", "3, 4, -44", (byte) 3, (byte) 4, (byte) -44);
            testData.setInputWithErrors("3, a, 4.5, -b, 9999", (byte) 3, (byte) 4, (byte) 127);
        } else if (converter == InlineArrayPropertyType.BOOLEAN) {
            testData.setInputAndExpected("true, false, true", "true, false, true", true, false, true);
            testData.setInputWithErrors("TRUE, something, else, 43, true,, , False", true, true, false);
        } else if (converter == InlineArrayPropertyType.STRING) {
            testData.setInputAndExpected("a\nb\nLong test string\nd", "a\nb\nLong test string\nd",
                "a", "b", "Long test string", "d");
            String someString = "An even longer String\twith a tab";
            testData.setInputWithErrors(someString + "\n\n",
                someString, "", "");
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
            Object[] arr = ConversionUtils.createArrayForReferenceType(clazz, values.length);
            System.arraycopy(values, 0, arr, 0, values.length);
            return arr;
        }
    }
}
