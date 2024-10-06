package ch.jalu.configme.beanmapper.instantiation;

import ch.jalu.configme.beanmapper.propertydescription.BeanFieldPropertyDescription;
import ch.jalu.configme.beanmapper.propertydescription.BeanPropertyComments;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.typeresolver.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Test for {@link BeanZeroArgConstructorInstantiation}.
 */
@ExtendWith(MockitoExtension.class)
class BeanZeroArgConstructorInstantiationTest {

    @Test
    void shouldCreateBeanWithProperties() {
        // given
        BeanZeroArgConstructorInstantiation instantiation = SampleBean.createInstantiation();
        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);

        // when
        SampleBean result = (SampleBean) instantiation.create(Arrays.asList("Toast", 12), errorRecorder);

        // then
        assertThat(result.getName(), equalTo("Toast"));
        assertThat(result.getSize(), equalTo(12));
        verifyNoInteractions(errorRecorder);
    }

    @Test
    void shouldNotCreateBeanForNullValue() {
        // given
        BeanZeroArgConstructorInstantiation instantiation = SampleBean.createInstantiation();
        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);

        // when
        Object result = instantiation.create(Arrays.asList(null, 534), errorRecorder);

        // then
        assertThat(result, nullValue());
        verifyNoInteractions(errorRecorder);
    }

    @Test
    void shouldThrowForValueThatDoesNotMatchField() {
        // given
        BeanZeroArgConstructorInstantiation instantiation = SampleBean.createInstantiation();
        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);

        // when
        ConfigMeException ex = assertThrows(ConfigMeException.class,
            () -> instantiation.create(Arrays.asList("Toast", "wrong"), errorRecorder));

        // then
        assertThat(ex.getMessage(), equalTo("Failed to set value to field BeanZeroArgConstructorInstantiationTest$SampleBean#size. Value: wrong"));
        assertThat(ex.getCause(), instanceOf(IllegalArgumentException.class));
    }

    @Test
    void shouldPropagateExceptionInConstructor() throws NoSuchMethodException {
        // given
        BeanZeroArgConstructorInstantiation instantiation = new BeanZeroArgConstructorInstantiation(
            BeanWithThrowingConstructor.class.getDeclaredConstructor(),
            Collections.emptyList());
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when
        ConfigMeException ex = assertThrows(ConfigMeException.class,
            () -> instantiation.create(Collections.emptyList(), errorRecorder));

        // then
        assertThat(ex.getMessage(), equalTo("Failed to call constructor for class ch.jalu.configme.beanmapper.instantiation.BeanZeroArgConstructorInstantiationTest$BeanWithThrowingConstructor"));
        assertThat(ex.getCause(), instanceOf(InvocationTargetException.class));
    }

    @Test
    void shouldAcceptNullValuesIfFieldHasDefault() throws NoSuchMethodException {
        // given
        List<BeanFieldPropertyDescription> properties = Arrays.stream(BeanWithFieldDefaults.class.getDeclaredFields())
            .filter(FieldUtils::isRegularInstanceField)
            .map(field -> new BeanFieldPropertyDescription(field, null, BeanPropertyComments.EMPTY))
            .collect(Collectors.toList());

        BeanZeroArgConstructorInstantiation instantiation = new BeanZeroArgConstructorInstantiation(
            BeanWithFieldDefaults.class.getDeclaredConstructor(),
            properties);

        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);

        // when
        BeanWithFieldDefaults bean1 = (BeanWithFieldDefaults) instantiation.create(Arrays.asList(null, 3), errorRecorder);
        BeanWithFieldDefaults bean2 = (BeanWithFieldDefaults) instantiation.create(Arrays.asList("test", null), errorRecorder);

        // then
        assertThat(bean1, notNullValue());
        assertThat(bean1.name, equalTo("def"));
        assertThat(bean1.size, equalTo(3));
        verify(errorRecorder).setHasError("Fallback to default value for FieldProperty 'name' for field 'BeanZeroArgConstructorInstantiationTest$BeanWithFieldDefaults#name'");

        assertThat(bean2, notNullValue());
        assertThat(bean2.name, equalTo("test"));
        assertThat(bean2.size, equalTo(0));
        verify(errorRecorder).setHasError("Fallback to default value for FieldProperty 'size' for field 'BeanZeroArgConstructorInstantiationTest$BeanWithFieldDefaults#size'");

        verifyNoMoreInteractions(errorRecorder);
    }

    @Test
    void shouldThrowForPropertyValuesMismatch() {
        // given
        BeanZeroArgConstructorInstantiation instantiation = SampleBean.createInstantiation();

        // when
        ConfigMeException ex = assertThrows(ConfigMeException.class,
            () -> instantiation.create(Arrays.asList(3, 4, 5), new ConvertErrorRecorder()));

        // then
        assertThat(ex.getMessage(), equalTo("Invalid property values, 3 were given, but class "
            + "ch.jalu.configme.beanmapper.instantiation.BeanZeroArgConstructorInstantiationTest$SampleBean has 2 properties"));
    }

    private static final class SampleBean {

        private String name;
        private int size;

        public SampleBean() {
        }

        public String getName() {
            return name;
        }

        public int getSize() {
            return size;
        }

        static BeanZeroArgConstructorInstantiation createInstantiation() {
            List<BeanFieldPropertyDescription> properties = Arrays.stream(SampleBean.class.getDeclaredFields())
                .filter(FieldUtils::isRegularInstanceField)
                .map(field -> new BeanFieldPropertyDescription(field, null, BeanPropertyComments.EMPTY))
                .collect(Collectors.toList());

            try {
                return new BeanZeroArgConstructorInstantiation(SampleBean.class.getDeclaredConstructor(), properties);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private static final class BeanWithThrowingConstructor {

        private BeanWithThrowingConstructor() {
            throw new IllegalStateException("Yikers");
        }
    }

    private static final class BeanWithFieldDefaults {

        private String name = "def";
        private int size;

        public BeanWithFieldDefaults() {
        }

    }
}
