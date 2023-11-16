package ch.jalu.configme.beanmapper.propertydescription;

import ch.jalu.configme.samples.beanannotations.AnnotatedEntry;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Test for {@link FieldProperty}.
 */
class FieldPropertyTest {

    @Test
    void shouldGetProperties() {
        // given
        BeanPropertyDescription sizeProperty = getDescriptor("size", SampleBean.class);
        SampleBean bean = new SampleBean();
        bean.setSize(77);

        // when
        Object result1 = sizeProperty.getValue(bean);

        // then
        assertThat(result1, equalTo(77));
    }

    @Test
    void shouldHaveAppropriateStringRepresentation() {
        // given
        Collection<FieldProperty> properties = new BeanDescriptionFactoryImpl()
            .collectAllProperties(AnnotatedEntry.class);
        BeanPropertyDescription hasIdProperty = properties.stream()
            .filter(prop -> "has-id".equals(prop.getName())).findFirst().get();

        // when
        String output = "Found " + hasIdProperty;

        // then
        assertThat(output, equalTo("Found FieldProperty 'has-id' for field 'AnnotatedEntry#hasId'"));
    }

    private static BeanPropertyDescription getDescriptor(String name, Class<?> clazz) {
        return new BeanDescriptionFactoryImpl().collectAllProperties(clazz)
            .stream()
            .filter(prop -> name.equals(prop.getName()))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
    
    private static class SampleBean {
        private int size;

        // Need explicit default constructor so Java sees that it's visible
        public SampleBean() {
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }

    private static final class ThrowingBean extends SampleBean {
        @Override
        public void setSize(int size) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getSize() {
            throw new UnsupportedOperationException();
        }
    }

}
