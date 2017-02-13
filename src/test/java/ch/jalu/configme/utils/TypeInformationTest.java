package ch.jalu.configme.utils;

import ch.jalu.configme.exception.ConfigMeException;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static ch.jalu.configme.TestUtils.verifyException;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link TypeInformation}.
 */
public class TypeInformationTest {

    private List<String> stringList;
    private List<?> wildcardList;
    private String stringField = "";
    private Map<String, Integer> stringIntegerMap;
    private Map<Double, ?> wildcardMap;
    private Map<?, Character> wildcardMap2;
    private Map uncheckedMap;

    @Test
    public void shouldFindGenericType() {
        // given
        TypeInformation typeInformation = fromField("stringList");

        // when / then
        assertThat(typeInformation.getGenericClass(0), equalTo(String.class));
    }

    @Test
    public void shouldThrowExceptionForWrongType() {
        // given
        String[] fieldNames = {"wildcardList", "stringField", "uncheckedMap"};

        // when / then
        for (String field : fieldNames) {
            TypeInformation typeInformation = fromField(field);
            verifyException(() -> typeInformation.getGenericClass(0), ConfigMeException.class);
        }
    }

    @Test
    public void shouldFindGenericTypes() {
        // given
        TypeInformation mapType = fromField("stringIntegerMap");

        // when
        Class<?> keyType = mapType.getGenericClass(0);
        Class<?> valueType = mapType.getGenericClass(1);

        // then
        assertTrue(String.class == keyType);
        assertTrue(Integer.class == valueType);
    }

    @Test
    public void shouldThrowExceptionForInvalidTypes() {
        // given
        String[] fieldNames = {"stringList", "stringField", "wildcardMap", "wildcardMap2", "uncheckedMap"};

        // when / then
        for (String field : fieldNames) {
            TypeInformation type = fromField(field);
            verifyException(() -> {
                type.getGenericClass(0);
                type.getGenericClass(1);
            }, ConfigMeException.class);
        }
    }

    private static TypeInformation fromField(String name) {
        try {
            Field field = TypeInformationTest.class.getDeclaredField(name);
            return TypeInformation.of(field.getType(), field.getGenericType());
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static final class SampleBean {
        SampleBean() {
        }
    }

}
