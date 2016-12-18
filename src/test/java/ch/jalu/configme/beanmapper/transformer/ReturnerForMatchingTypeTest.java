package ch.jalu.configme.beanmapper.transformer;

import ch.jalu.configme.beanmapper.transformer.Transformers.ReturnerForMatchingType;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link Transformers.ReturnerForMatchingType}.
 */
public class ReturnerForMatchingTypeTest {

    @Test
    public void shouldReturnValueOfSameType() {
        // given
        Class<?> clazz = String.class;
        Object value = "test";
        ReturnerForMatchingType transformer = new ReturnerForMatchingType();

        // when
        Object result = transformer.transform(clazz, null, value);

        // then
        assertThat(result, equalTo(value));
    }

    @Test
    public void shouldReturnForParentType() {
        // given
        Class<?> clazz = Exception.class;
        Object value = new IllegalStateException();
        ReturnerForMatchingType transformer = new ReturnerForMatchingType();

        // when
        Object result = transformer.transform(clazz, null, value);

        // then
        assertThat(result, equalTo(value));
    }

    @Test
    public void shouldReturnForBooleanPrimitive() {
        // given
        Class<?> clazz = boolean.class;
        Object value = Boolean.TRUE;
        ReturnerForMatchingType transformer = new ReturnerForMatchingType();

        // when
        Object result = transformer.transform(clazz, null, value);

        // then
        assertThat(result, equalTo(value));
    }

    @Test
    public void shouldReturnNullForOtherType() {
        // given
        Class<?> clazz = boolean.class;
        Object value = "A string";
        ReturnerForMatchingType transformer = new ReturnerForMatchingType();

        // when
        Object result = transformer.transform(clazz, null, value);

        // then
        assertThat(result, nullValue());
    }

    @Test
    public void shouldReturnNullForNullValue() {
        // given
        Class<?> clazz = String.class;
        Object value = null;
        ReturnerForMatchingType transformer = new ReturnerForMatchingType();

        // when
        Object result = transformer.transform(clazz, null, value);

        // then
        assertThat(result, nullValue());
    }
}
