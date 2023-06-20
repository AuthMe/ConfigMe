package ch.jalu.configme.properties.type;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.properties.types.PrimitivePropertyType;
import ch.jalu.configme.properties.types.PropertyType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

/**
 * Test for {@link PrimitivePropertyType}.
 */
class PrimitivePropertyTypeTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("data")
    void shouldConvertValue1(String name, PropertyType<?> propertyType, TestData testData) {
        // given
        Object object = testData.object1;

        // when
        Object result = propertyType.convert(object, new ConvertErrorRecorder());

        // then
        assertThat(result, equalTo(testData.expected1));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("data")
    void shouldConvertValue2(String name, PropertyType<?> propertyType, TestData testData) {
        // given
        Object object = testData.object2;

        // when
        Object result = propertyType.convert(object, new ConvertErrorRecorder());

        // then
        assertThat(result, equalTo(testData.expected2));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("data")
    void shouldReturnNullForInvalidValue(String name, PropertyType<?> propertyType, TestData testData) {
        // given
        Object object = testData.invalid;

        // when
        Object result = propertyType.convert(object, new ConvertErrorRecorder());

        // then
        assertThat(result, nullValue());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("data")
    void shouldHandleNull(String name, PropertyType<?> propertyType, TestData testData) {
        // given / when
        Object result = propertyType.convert(null, new ConvertErrorRecorder());

        // then
        assertThat(result, nullValue());
    }

    private static List<Object[]> data() throws IllegalAccessException {
        List<Object[]> converters = new ArrayList<>();
        for (Field field : PrimitivePropertyType.class.getDeclaredFields()) {
            if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                PrimitivePropertyType<?> propertyType = (PrimitivePropertyType<?>) field.get(null);
                converters.add(new Object[]{field.getName(), propertyType, getTestData(propertyType)});
            }
        }
        return converters;
    }

    private static TestData getTestData(PrimitivePropertyType<?> propertyType) {
        if (propertyType == PrimitivePropertyType.BOOLEAN) {
            return new TestData(true, true, false, false, 3);
        } else if (propertyType == PrimitivePropertyType.BYTE) {
            return new TestData(3, (byte) 3, -120L, (byte) -120, "test");
        } else if (propertyType == PrimitivePropertyType.DOUBLE) {
            return new TestData(3, 3.0, -45.0f, -45.0, new Object());
        } else if (propertyType == PrimitivePropertyType.FLOAT) {
            return new TestData(3, 3.0f, -45.0, -45.0f, 'c');
        } else if (propertyType == PrimitivePropertyType.INTEGER) {
            return new TestData(3, 3, -45.0, -45, true);
        } else if (propertyType == PrimitivePropertyType.LONG) {
            return new TestData(3, 3L, -45L, -45L, "");
        } else if (propertyType == PrimitivePropertyType.LOWERCASE_STRING) {
            return new TestData(3, "3", "Test", "test", null);
        } else if (propertyType == PrimitivePropertyType.SHORT) {
            return new TestData(3, (short) 3, -45.0f, (short) -45, "a");
        } else if (propertyType == PrimitivePropertyType.STRING) {
            return new TestData("Test", "Test", -45.0f, "-45.0", null);
        } else {
            throw new IllegalStateException("Unhandled property type '" + propertyType + "'");
        }
    }

    private static final class TestData {

        private final Object object1;
        private final Object expected1;
        private final Object object2;
        private final Object expected2;
        private final Object invalid;

        private TestData(Object object1, Object expected1, Object object2, Object expected2, Object invalid) {
            this.object1 = object1;
            this.expected1 = expected1;
            this.object2 = object2;
            this.expected2 = expected2;
            this.invalid = invalid;
        }
    }
}
