package ch.jalu.configme.utils;

import ch.jalu.configme.exception.ConfigMeException;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static ch.jalu.configme.TestUtils.verifyException;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link TypeInformation}.
 */
@Deprecated // has been moved
public class TypeInformationTest {

    private List<String> stringList;
    private List<?> wildcardList;
    private String stringField = "";
    private Map<String, Integer> stringIntegerMap;
    private Map<Double, ?> wildcardMap;
    private Map<?, Character> wildcardMap2;
    private Map uncheckedMap;

    private Collection<Iterable<Comparable<Optional<Class<Supplier<String>>>>>> nestedType;

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

    @Test
    public void shouldHaveEqualsImplementation() {
        // given
        TypeInformation<Iterable> iterableType = TypeInformation.of(Iterable.class);
        TypeInformation<Iterable> stringIterableType = TypeInformation.of(Iterable.class, String.class);
        TypeInformation<String> stringType = TypeInformation.of(String.class);

        // when / then
        assertThat(iterableType.equals(new Object()), equalTo(false));
        assertThat(iterableType.equals(TypeInformation.of(Iterable.class)), equalTo(true));
        assertThat(iterableType.equals(stringIterableType), equalTo(false));
        assertThat(stringIterableType.equals(iterableType), equalTo(false));
        assertThat(stringIterableType.equals(TypeInformation.of(Iterable.class, String.class)), equalTo(true));
        assertThat(stringType.equals(TypeInformation.of(String.class)), equalTo(true));
    }

    @Test
    public void shouldHaveTypeInfoInToString() {
        // given
        TypeInformation<String> type = TypeInformation.of(String.class);

        // when
        String string = type.toString();

        // then
        assertThat(string, containsString("clazz=class java.lang.String"));
    }

    @Test
    public void shouldBuildGenericType() {
        // given
        TypeInformation<?> type = fromField("nestedType");
        List<Class<?>> expectedTypes = Arrays.asList(Collection.class, Iterable.class, Comparable.class,
            Optional.class, Class.class, Supplier.class, String.class);

        // when / then
        for (int i = 0; i < expectedTypes.size(); ++i) {
            assertThat(type.getClazz(), equalTo(expectedTypes.get(i)));
            if (i < expectedTypes.size() - 1) {
                type = type.buildGenericType(0);
            }
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
