package com.github.authme.configme.beanmapper;

import com.github.authme.configme.TestUtils;
import org.junit.Test;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.authme.configme.TestUtils.verifyException;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.hasSize;
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
    public void shouldFindWritableProperties() {
        // given
        Class<?> clazz = SampleBean.class;

        // when
        List<PropertyDescriptor> properties = MapperUtils.getWritableProperties(clazz);

        // then
        assertThat(properties, hasSize(2));
    }

    @Test(expected = ConfigMeMapperException.class)
    public void shouldHandlePropertyGetError() {
        // given
        PropertyDescriptor sizeProperty = getDescriptor("size", MapperUtils.getWritableProperties(SampleBean.class));
        SampleBean bean = SampleBean.createThrowingBean();

        // when
        MapperUtils.getBeanProperty(sizeProperty, bean);
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

    private static PropertyDescriptor getDescriptor(String name, List<PropertyDescriptor> properties) {
        for (PropertyDescriptor property : properties) {
            if (property.getName().equals(name)) {
                return property;
            }
        }
        throw new IllegalArgumentException("No property with name '" + name + "'");
    }

    private static final class SampleBean {
        private String name;
        private int size;
        private UUID uuid = UUID.randomUUID();
        private boolean throwExceptions = false;

        SampleBean() {
        }

        static SampleBean createThrowingBean() {
            SampleBean bean = new SampleBean();
            bean.throwExceptions = true;
            return bean;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getSize() {
            if (throwExceptions) {
                throw new IllegalStateException();
            }
            return size;
        }

        public void setSize(int size) {
            if (throwExceptions) {
                throw new IllegalStateException();
            }
            this.size = size;
        }

        public UUID getUuid() {
            return uuid;
        }
    }

}
