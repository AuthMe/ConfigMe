package ch.jalu.configme.beanmapper;

import ch.jalu.configme.TestUtils;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static ch.jalu.configme.TestUtils.verifyException;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link MapperUtils}.
 */
public class MapperUtilsTest {

    private List<String> stringList;
    private List<?> wildcardList;
    private String stringField = "";
    private Map<String, Integer> stringIntegerMap;
    private Map<Double, ?> wildcardMap;
    private Map<?, Character> wildcardMap2;
    private Map uncheckedMap;

    @Test
    public void shouldFindGenericType() {
        // given / when / then
        assertTrue(String.class == MapperUtils.getGenericClassSafely(getFieldType("stringList")));
    }

    @Test
    public void shouldThrowExceptionForWrongType() {
        // given
        String[] fieldNames = {"wildcardList", "stringField", "uncheckedMap"};

        // when / then
        for (String field : fieldNames) {
            Type type = getFieldType(field);
            verifyException(() -> MapperUtils.getGenericClassSafely(type), ConfigMeMapperException.class);
        }
    }

    @Test
    public void shouldFindGenericTypes() {
        // given
        Type mapType = getFieldType("stringIntegerMap");

        // when
        Class<?>[] result = MapperUtils.getGenericClassesSafely(mapType);

        // then
        assertThat(result, arrayWithSize(2));
        assertTrue(String.class == result[0]);
        assertTrue(Integer.class == result[1]);
    }

    @Test
    public void shouldThrowExceptionForInvalidTypes() {
        // given
        String[] fieldNames = {"stringList", "stringField", "wildcardMap", "wildcardMap2", "uncheckedMap"};

        // when / then
        for (String field : fieldNames) {
            Type type = getFieldType(field);
            verifyException(() -> MapperUtils.getGenericClassesSafely(type), ConfigMeMapperException.class);
        }
    }

    @Test
    public void shouldInvokeDefaultConstructor() {
        // given / when
        SampleBean result = MapperUtils.invokeDefaultConstructor(SampleBean.class);

        // then
        assertThat(result, not(nullValue()));
    }

    @Test(expected = ConfigMeMapperException.class)
    public void shouldForwardConstructorError() {
        // given / when / then
        MapperUtils.invokeDefaultConstructor(Iterable.class);
    }

    @Test
    public void shouldHaveHiddenConstructor() {
        TestUtils.validateHasOnlyPrivateEmptyConstructor(MapperUtils.class);
    }

    private static Type getFieldType(String name) {
        try {
            return MapperUtilsTest.class.getDeclaredField(name).getGenericType();
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static final class SampleBean {
        SampleBean() {
        }
    }

}
