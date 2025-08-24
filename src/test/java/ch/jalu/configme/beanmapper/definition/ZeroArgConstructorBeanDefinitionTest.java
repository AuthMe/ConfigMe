package ch.jalu.configme.beanmapper.definition;

import ch.jalu.configme.beanmapper.definition.properties.BeanFieldPropertyDescription;
import ch.jalu.configme.beanmapper.definition.properties.BeanPropertyComments;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.typeresolver.reflect.ConstructorUtils;
import ch.jalu.typeresolver.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
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
 * Test for {@link ZeroArgConstructorBeanDefinition}.
 */
@ExtendWith(MockitoExtension.class)
class ZeroArgConstructorBeanDefinitionTest {

    @Test
    void shouldCreateBeanWithProperties() {
        // given
        ZeroArgConstructorBeanDefinition definition = SampleBean.createDefinition();
        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);

        // when
        SampleBean result = (SampleBean) definition.create(Arrays.asList("Toast", 12), errorRecorder);

        // then
        assertThat(result.getName(), equalTo("Toast"));
        assertThat(result.getSize(), equalTo(12));
        verifyNoInteractions(errorRecorder);
    }

    @Test
    void shouldNotCreateBeanForNullValue() {
        // given
        ZeroArgConstructorBeanDefinition definition = SampleBean.createDefinition();
        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);

        // when
        Object result = definition.create(Arrays.asList(null, 534), errorRecorder);

        // then
        assertThat(result, nullValue());
        verifyNoInteractions(errorRecorder);
    }

    @Test
    void shouldThrowForValueThatDoesNotMatchField() {
        // given
        ZeroArgConstructorBeanDefinition definition = SampleBean.createDefinition();
        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);

        // when
        ConfigMeException ex = assertThrows(ConfigMeException.class,
            () -> definition.create(Arrays.asList("Toast", "wrong"), errorRecorder));

        // then
        assertThat(ex.getMessage(), equalTo("Failed to set value to field ZeroArgConstructorBeanDefinitionTest$SampleBean#size. Value: wrong"));
        assertThat(ex.getCause(), instanceOf(IllegalArgumentException.class));
    }

    @Test
    void shouldPropagateExceptionInConstructor() throws NoSuchMethodException {
        // given
        ZeroArgConstructorBeanDefinition definition = new ZeroArgConstructorBeanDefinition(
            BeanWithThrowingConstructor.class.getDeclaredConstructor(),
            Collections.emptyList());
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when
        ConfigMeException ex = assertThrows(ConfigMeException.class,
            () -> definition.create(Collections.emptyList(), errorRecorder));

        // then
        assertThat(ex.getMessage(), equalTo("Failed to call constructor for class ch.jalu.configme.beanmapper.definition.ZeroArgConstructorBeanDefinitionTest$BeanWithThrowingConstructor"));
        assertThat(ex.getCause(), instanceOf(InvocationTargetException.class));
    }

    @Test
    void shouldAcceptNullValuesIfFieldHasDefault() throws NoSuchMethodException {
        // given
        List<BeanFieldPropertyDescription> properties = Arrays.stream(BeanWithFieldDefaults.class.getDeclaredFields())
            .filter(FieldUtils::isRegularInstanceField)
            .map(field -> new BeanFieldPropertyDescription(field, null, BeanPropertyComments.EMPTY))
            .collect(Collectors.toList());

        ZeroArgConstructorBeanDefinition definition = new ZeroArgConstructorBeanDefinition(
            BeanWithFieldDefaults.class.getDeclaredConstructor(),
            properties);

        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);

        // when
        BeanWithFieldDefaults bean1 = (BeanWithFieldDefaults) definition.create(Arrays.asList(null, 3), errorRecorder);
        BeanWithFieldDefaults bean2 = (BeanWithFieldDefaults) definition.create(Arrays.asList("test", null), errorRecorder);

        // then
        assertThat(bean1, notNullValue());
        assertThat(bean1.name, equalTo("def"));
        assertThat(bean1.size, equalTo(3));
        verify(errorRecorder).setHasError("Fallback to default value for FieldProperty 'name' for field 'ZeroArgConstructorBeanDefinitionTest$BeanWithFieldDefaults#name'");

        assertThat(bean2, notNullValue());
        assertThat(bean2.name, equalTo("test"));
        assertThat(bean2.size, equalTo(0));
        verify(errorRecorder).setHasError("Fallback to default value for FieldProperty 'size' for field 'ZeroArgConstructorBeanDefinitionTest$BeanWithFieldDefaults#size'");

        verifyNoMoreInteractions(errorRecorder);
    }

    @Test
    void shouldThrowForPropertyValuesMismatch() {
        // given
        ZeroArgConstructorBeanDefinition definition = SampleBean.createDefinition();

        // when
        ConfigMeException ex = assertThrows(ConfigMeException.class,
            () -> definition.create(Arrays.asList(3, 4, 5), new ConvertErrorRecorder()));

        // then
        assertThat(ex.getMessage(), equalTo("Invalid property values, 3 were given, but class "
            + "ch.jalu.configme.beanmapper.definition.ZeroArgConstructorBeanDefinitionTest$SampleBean has 2 properties"));
    }

    @Test
    void shouldReturnFieldsInGetters() {
        // given
        ZeroArgConstructorBeanDefinition definition = SampleBean.createDefinition();

        // when
        Constructor<?> zeroArgsConstructor = definition.getZeroArgsConstructor();
        List<BeanFieldPropertyDescription> fieldProperties = definition.getFieldProperties();

        // then
        assertThat(zeroArgsConstructor, equalTo(ConstructorUtils.getConstructorOrThrow(SampleBean.class)));
        assertThat(fieldProperties, equalTo(definition.getProperties()));
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

        static ZeroArgConstructorBeanDefinition createDefinition() {
            List<BeanFieldPropertyDescription> properties = Arrays.stream(SampleBean.class.getDeclaredFields())
                .filter(FieldUtils::isRegularInstanceField)
                .map(field -> new BeanFieldPropertyDescription(field, null, BeanPropertyComments.EMPTY))
                .collect(Collectors.toList());

            try {
                return new ZeroArgConstructorBeanDefinition(SampleBean.class.getDeclaredConstructor(), properties);
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
