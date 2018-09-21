package ch.jalu.configme.properties.type;

import ch.jalu.configme.properties.types.PrimitivePropertyType;
import ch.jalu.configme.properties.types.PropertyType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link PrimitivePropertyType}.
 */
@RunWith(Parameterized.class)
public class PrimitivePropertyTypeTest {

    @Parameterized.Parameter(value = 0)
    public String name;
    @Parameterized.Parameter(value = 1)
    public PropertyType propertyType;
    @Parameterized.Parameter(value = 2)
    public TestData testData;

    @Test
    public void shouldConvertValue1() {
        // given
        Object object = testData.object1;

        // when
        Object result = propertyType.convert(object);

        // then
        assertThat(result, equalTo(testData.expected1));
    }

    @Test
    public void shouldConvertValue2() {
        // given
        Object object = testData.object2;

        // when
        Object result = propertyType.convert(object);

        // then
        assertThat(result, equalTo(testData.expected2));
    }

    @Test
    public void shouldReturnNullForInvalidValue() {
        // given
        Object object = testData.invalid;

        // when
        Object result = propertyType.convert(object);

        // then
        assertThat(result, nullValue());
    }

    @Test
    public void shouldHandleNull() {
        // given / when
        Object result = propertyType.convert(null);

        // then
        assertThat(result, nullValue());
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> collectStandardInlineConverters() throws IllegalAccessException {
        List<Object[]> converters = new ArrayList<>();
        for (Field field : PrimitivePropertyType.class.getDeclaredFields()) {
            if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                PrimitivePropertyType propertyType = (PrimitivePropertyType) field.get(null);
                converters.add(new Object[]{field.getName(), propertyType, getTestData(propertyType)});
            }
        }
        return converters;
    }

    private static TestData getTestData(PrimitivePropertyType propertyType) {
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
