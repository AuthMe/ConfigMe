package ch.jalu.configme.beanmapper;

import ch.jalu.configme.samples.beanannotations.AnnotatedEntry;
import org.junit.Test;

import java.util.Collection;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link BeanPropertyDescription}.
 */
public class BeanPropertyDescriptionTest {

    @Test
    public void shouldGetAndSetProperties() {
        // given
        BeanPropertyDescription sizeProperty = getDescriptor("size", SampleBean.class);
        SampleBean bean = new SampleBean();
        bean.setSize(77);

        // when
        Object result1 = sizeProperty.getValue(bean);
        sizeProperty.setValue(bean, -120);
        Object result2 = sizeProperty.getValue(bean);

        // then
        assertThat(bean.getSize(), equalTo(-120));
        assertThat(77, equalTo(result1));
        assertThat(-120, equalTo(result2));
    }

    @Test(expected = ConfigMeMapperException.class)
    public void shouldHandlePropertySetError() {
        // given
        BeanPropertyDescription sizeProperty = getDescriptor("size", SampleBean.class);
        SampleBean bean = new ThrowingBean();

        // when
        sizeProperty.setValue(bean, -120);
    }

    @Test(expected = ConfigMeMapperException.class)
    public void shouldHandlePropertyGetError() {
        // given
        BeanPropertyDescription sizeProperty = getDescriptor("size", SampleBean.class);
        SampleBean bean = new ThrowingBean();

        // when
        sizeProperty.getValue(bean);
    }

    @Test
    public void shouldHaveAppropriateStringRepresentation() {
        // given
        Collection<BeanPropertyDescription> properties = new BeanDescriptionFactoryImpl()
            .findAllWritableProperties(AnnotatedEntry.class);
        BeanPropertyDescription hasIdProperty = properties.stream()
            .filter(prop -> "has-id".equals(prop.getName())).findFirst().get();

        // when
        String output = "Found " + hasIdProperty;

        // then
        assertThat(output, equalTo("Found Bean property 'has-id' with getter "
            + "'public boolean ch.jalu.configme.samples.beanannotations.AnnotatedEntry.getHasId()'"));
    }

    private static BeanPropertyDescription getDescriptor(String name, Class<?> clazz) {
        return new BeanDescriptionFactoryImpl().findAllWritableProperties(clazz)
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
