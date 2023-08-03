package ch.jalu.configme.beanmapper.propertydescription;

import ch.jalu.configme.beanmapper.ConfigMeMapperException;
import ch.jalu.configme.samples.beanannotations.AnnotatedEntry;
import ch.jalu.configme.utils.TypeInformation;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for {@link BeanPropertyDescriptionImpl}.
 */
public class BeanPropertyDescriptionImplTest {

    @Test
    void shouldGetAndSetProperties() {
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

    @Test
    void shouldHandlePropertySetError() {
        // given
        BeanPropertyDescription sizeProperty = getDescriptor("size", SampleBean.class);
        SampleBean bean = new ThrowingBean();

        // when / then
        assertThrows(ConfigMeMapperException.class,
            () -> sizeProperty.setValue(bean, -120));
    }

    @Test
    void shouldHandlePropertyGetError() {
        // given
        BeanPropertyDescription sizeProperty = getDescriptor("size", SampleBean.class);
        SampleBean bean = new ThrowingBean();

        // when / then
        assertThrows(ConfigMeMapperException.class,
            () -> sizeProperty.getValue(bean));
    }

    @Test
    void shouldHaveAppropriateStringRepresentation() {
        // given
        Collection<BeanPropertyDescription> properties = new BeanDescriptionFactoryImpl()
            .collectAllProperties(AnnotatedEntry.class);
        BeanPropertyDescription hasIdProperty = properties.stream()
            .filter(prop -> "has-id".equals(prop.getName())).findFirst().get();

        // when
        String output = "Found " + hasIdProperty;

        // then
        assertThat(output, equalTo("Found Bean property 'has-id' with getter "
            + "'public boolean ch.jalu.configme.samples.beanannotations.AnnotatedEntry.getHasId()'"));
    }

    @Test
    void shouldCreateValuesWithLegacyConstructor() throws NoSuchMethodException {
        // given
        Method sizeGetter = SampleBean.class.getDeclaredMethod("getSize");
        Method sizeSetter = SampleBean.class.getDeclaredMethod("setSize", int.class);

        // when
        BeanPropertyDescriptionImpl property =
            new BeanPropertyDescriptionImpl("name", new TypeInformation(String.class), sizeGetter, sizeSetter);

        // then
        assertThat(property.getName(), equalTo("name"));
        assertThat(property.getTypeInformation(), equalTo(new TypeInformation(String.class)));
        assertThat(property.getComments(), empty());
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
