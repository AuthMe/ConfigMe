package ch.jalu.configme.beanmapper.definition.properties;

import ch.jalu.configme.beanmapper.ConfigMeMapperException;
import ch.jalu.configme.samples.beanannotations.AnnotatedEntry;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for {@link BeanFieldPropertyDefinition}.
 */
public class BeanFieldPropertyDefinitionTest {

    @Test
    void shouldGetAndSetProperties() {
        // given
        BeanPropertyDefinition sizeProperty = getDescriptor("size", SampleBean.class);
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

    @Test
    void shouldHandlePropertySetError() {
        // given
        BeanPropertyDefinition sizeProperty = getDescriptor("size", SampleBean.class);
        SampleBean bean = new ThrowingBean();

        // when / then
        assertThrows(ConfigMeMapperException.class,
            () -> sizeProperty.setValue(bean, -120));
    }

    @Test
    void shouldHandlePropertyGetError() {
        // given
        BeanPropertyDefinition sizeProperty = getDescriptor("size", SampleBean.class);
        SampleBean bean = new ThrowingBean();

        // when / then
        assertThrows(ConfigMeMapperException.class,
            () -> sizeProperty.getValue(bean));
    }

    @Test
    void shouldHaveAppropriateStringRepresentation() {
        // given
        Collection<BeanPropertyDefinition> properties = new BeanPropertyExtractorImpl()
            .collectAllProperties(AnnotatedEntry.class);
        BeanPropertyDefinition hasIdProperty = properties.stream()
            .filter(prop -> "has-id".equals(prop.getName())).findFirst().get();

        // when
        String output = "Found " + hasIdProperty;

        // then
        assertThat(output, equalTo("Found Bean property 'has-id' with getter "
            + "'public boolean ch.jalu.configme.samples.beanannotations.AnnotatedEntry.getHasId()'"));
    }

    private static BeanPropertyDefinition getDescriptor(String name, Class<?> clazz) {
        return new BeanPropertyExtractorImpl().collectAllProperties(clazz)
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
