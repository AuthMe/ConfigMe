package ch.jalu.configme.beanmapper.definition;

import ch.jalu.configme.beanmapper.definition.properties.BeanFieldPropertyDefinition;
import ch.jalu.configme.beanmapper.definition.properties.BeanPropertyComments;
import ch.jalu.configme.beanmapper.definition.properties.BeanPropertyDefinition;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.typeresolver.reflect.ConstructorUtils;
import ch.jalu.typeresolver.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Test for {@link RecordBeanDefinition}.
 */
@ExtendWith(MockitoExtension.class)
class RecordBeanDefinitionTest {

    @Test
    void shouldThrowForMissingConstructor() throws NoSuchFieldException {
        // given
        // Size & name properties are in the wrong order
        List<BeanPropertyDefinition> properties = Arrays.asList(
            new BeanFieldPropertyDefinition(ExampleRecord.class.getDeclaredField("size"), null, BeanPropertyComments.EMPTY),
            new BeanFieldPropertyDefinition(ExampleRecord.class.getDeclaredField("name"), null, BeanPropertyComments.EMPTY));

        // when
        ConfigMeException ex = assertThrows(ConfigMeException.class,
            () -> new RecordBeanDefinition(ExampleRecord.class, properties));

        // then
        assertThat(ex.getMessage(), equalTo("Could not get canonical constructor of class ch.jalu.configme.beanmapper.definition.RecordBeanDefinitionTest$ExampleRecord"));
        assertThat(ex.getCause(), nullValue());
    }

    @Test
    void shouldInstantiateRecord() {
        // given
        RecordBeanDefinition definition = ExampleRecord.createDefinition();
        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);

        // when
        ExampleRecord result = (ExampleRecord) definition.create(Arrays.asList("Toast", 10), errorRecorder);

        // then
        assertThat(result.name(), equalTo("Toast"));
        assertThat(result.size(), equalTo(10));
        verifyNoInteractions(errorRecorder);
    }

    @Test
    void shouldReturnNullIfAnyPropertyIsNull() {
        // given
        RecordBeanDefinition definition = ExampleRecord.createDefinition();
        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);

        // when / then
        assertThat(definition.create(Arrays.asList("Toast", null), errorRecorder), nullValue());
        assertThat(definition.create(Arrays.asList(null, 33), errorRecorder), nullValue());

        verifyNoInteractions(errorRecorder);
    }

    @Test
    void shouldHandleWrongPropertyValuesGracefully() {
        // given
        RecordBeanDefinition definition = ExampleRecord.createDefinition();
        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);

        // when
        ConfigMeException ex = assertThrows(ConfigMeException.class,
            () -> definition.create(Arrays.asList(3, 3), errorRecorder));

        // then
        assertThat(ex.getMessage(), equalTo("Error calling record constructor for class ch.jalu.configme.beanmapper.definition.RecordBeanDefinitionTest$ExampleRecord"));
        assertThat(ex.getCause(), instanceOf(IllegalArgumentException.class));
        assertThat(ex.getCause().getMessage(), equalTo("argument type mismatch"));
    }

    @Test
    void shouldReturnFieldsInGetters() {
        // given
        RecordBeanDefinition definition = ExampleRecord.createDefinition();

        // when
        Constructor<?> zeroArgsConstructor = definition.getCanonicalConstructor();

        // then
        assertThat(zeroArgsConstructor, equalTo(ConstructorUtils.getConstructorOrThrow(ExampleRecord.class, String.class, int.class)));
    }

    private static class ExampleRecord { // #347: Change to an actual record :)

        private final String name;
        private final int size;

        ExampleRecord(String name, int size) {
            this.name = name;
            this.size = size;
        }

        static RecordBeanDefinition createDefinition() {
            List<BeanPropertyDefinition> properties = Arrays.stream(ExampleRecord.class.getDeclaredFields())
                .filter(FieldUtils::isRegularInstanceField)
                .map(field -> new BeanFieldPropertyDefinition(field, null, BeanPropertyComments.EMPTY))
                .collect(Collectors.toList());

            return new RecordBeanDefinition(ExampleRecord.class, properties);
        }

        String name() {
            return name;
        }

        int size() {
            return size;
        }
    }
}
