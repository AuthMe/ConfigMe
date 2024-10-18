package ch.jalu.configme.beanmapper.propertydescription;

import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.samples.beanannotations.AnnotatedEntry;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for {@link BeanFieldPropertyDescription}.
 */
class BeanFieldPropertyDescriptionTest {

    @Test
    void shouldGetProperties() {
        // given
        BeanFieldPropertyDescription sizeProperty = getDescriptor("size", SampleBean.class);
        SampleBean bean = new SampleBean();
        bean.size = 77;

        // when
        Object result1 = sizeProperty.getValue(bean);
        sizeProperty.setValue(bean, -120);
        Object result2 = sizeProperty.getValue(bean);

        // then
        assertThat(result1, equalTo(77));
        assertThat(result2, equalTo(-120));
    }

    @Test
    void shouldHandlePropertySetError() {
        // given
        BeanFieldPropertyDescription sizeProperty = getDescriptor("size", SampleBean.class);
        String wrongObject = "test";

        // when
        ConfigMeException ex = assertThrows(ConfigMeException.class,
            () -> sizeProperty.setValue(wrongObject, -120));

        // then
        assertThat(ex.getMessage(), equalTo("Failed to set value to field BeanFieldPropertyDescriptionTest$SampleBean#size. Value: -120"));
        assertThat(ex.getCause(), instanceOf(IllegalArgumentException.class));
    }

    @Test
    void shouldHandlePropertyGetError() {
        // given
        BeanPropertyDescription sizeProperty = getDescriptor("size", SampleBean.class);
        String wrongObject = "test";

        // when
        ConfigMeException ex = assertThrows(ConfigMeException.class,
            () -> sizeProperty.getValue(wrongObject));

        // then
        assertThat(ex.getMessage(), equalTo("Failed to get value for field BeanFieldPropertyDescriptionTest$SampleBean#size"));
        assertThat(ex.getCause(), instanceOf(IllegalArgumentException.class));
    }

    @Test
    void shouldHaveAppropriateStringRepresentation() {
        // given
        Collection<BeanFieldPropertyDescription> properties = new BeanDescriptionFactoryImpl()
            .collectProperties(AnnotatedEntry.class);
        BeanPropertyDescription hasIdProperty = properties.stream()
            .filter(prop -> "has-id".equals(prop.getName())).findFirst().get();

        // when
        String output = "Found " + hasIdProperty;

        // then
        assertThat(output, equalTo("Found FieldProperty 'has-id' for field 'AnnotatedEntry#hasId'"));
    }

    private static BeanFieldPropertyDescription getDescriptor(String name, Class<?> clazz) {
        return new BeanDescriptionFactoryImpl().collectProperties(clazz)
            .stream()
            .filter(prop -> name.equals(prop.getName()))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }

    private static class SampleBean {

        private int size;

    }
}
