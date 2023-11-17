package ch.jalu.configme.beanmapper.instantiation;

import ch.jalu.configme.beanmapper.propertydescription.BeanFieldPropertyDescription;
import ch.jalu.configme.beanmapper.propertydescription.BeanPropertyComments;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Test for {@link BeanRecordInstantiation}.
 */
@ExtendWith(MockitoExtension.class)
class BeanRecordInstantiationTest {

    @Test
    void shouldThrowForMissingConstructor() throws NoSuchFieldException {
        // given
        // Size & name properties are in the wrong order
        List<BeanFieldPropertyDescription> properties = Arrays.asList(
            new BeanFieldPropertyDescription(ExampleRecord.class.getDeclaredField("size"), null, BeanPropertyComments.EMPTY),
            new BeanFieldPropertyDescription(ExampleRecord.class.getDeclaredField("name"), null, BeanPropertyComments.EMPTY));

        // when
        ConfigMeException ex = assertThrows(ConfigMeException.class,
            () -> new BeanRecordInstantiation(ExampleRecord.class, properties));

        // then
        assertThat(ex.getMessage(), equalTo("Could not get canonical constructor of class ch.jalu.configme.beanmapper.instantiation.BeanRecordInstantiationTest$ExampleRecord"));
        assertThat(ex.getCause(), nullValue());
    }

    @Test
    void shouldInstantiateRecord() throws NoSuchFieldException {
        // given
        List<BeanFieldPropertyDescription> properties = Arrays.asList(
            new BeanFieldPropertyDescription(ExampleRecord.class.getDeclaredField("name"), null, BeanPropertyComments.EMPTY),
            new BeanFieldPropertyDescription(ExampleRecord.class.getDeclaredField("size"), null, BeanPropertyComments.EMPTY));

        BeanRecordInstantiation instantiation = new BeanRecordInstantiation(ExampleRecord.class, properties);
        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);

        // when
        ExampleRecord result = (ExampleRecord) instantiation.create(Arrays.asList("Toast", 10), errorRecorder);

        // then
        assertThat(result.name(), equalTo("Toast"));
        assertThat(result.size(), equalTo(10));
        verifyNoInteractions(errorRecorder);
    }

    @Test
    void shouldReturnNullIfAnyPropertyIsNull() throws NoSuchFieldException {
        // given
        List<BeanFieldPropertyDescription> properties = Arrays.asList(
            new BeanFieldPropertyDescription(ExampleRecord.class.getDeclaredField("name"), null, BeanPropertyComments.EMPTY),
            new BeanFieldPropertyDescription(ExampleRecord.class.getDeclaredField("size"), null, BeanPropertyComments.EMPTY));

        BeanRecordInstantiation instantiation = new BeanRecordInstantiation(ExampleRecord.class, properties);
        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);

        // when / then
        assertThat(instantiation.create(Arrays.asList("Toast", null), errorRecorder), nullValue());
        assertThat(instantiation.create(Arrays.asList(null, 33), errorRecorder), nullValue());

        verifyNoInteractions(errorRecorder);
    }

    @Test
    void shouldHandleWrongPropertyValuesGracefully() throws NoSuchFieldException {
        // given
        List<BeanFieldPropertyDescription> properties = Arrays.asList(
            new BeanFieldPropertyDescription(ExampleRecord.class.getDeclaredField("name"), null, BeanPropertyComments.EMPTY),
            new BeanFieldPropertyDescription(ExampleRecord.class.getDeclaredField("size"), null, BeanPropertyComments.EMPTY));

        BeanRecordInstantiation instantiation = new BeanRecordInstantiation(ExampleRecord.class, properties);
        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);

        // when
        ConfigMeException ex = assertThrows(ConfigMeException.class,
            () -> instantiation.create(Arrays.asList(3, 3), errorRecorder));

        // then
        assertThat(ex.getMessage(), equalTo("Error calling record constructor for class ch.jalu.configme.beanmapper.instantiation.BeanRecordInstantiationTest$ExampleRecord"));
        assertThat(ex.getCause(), instanceOf(IllegalArgumentException.class));
        assertThat(ex.getCause().getMessage(), equalTo("argument type mismatch"));
    }

    private static class ExampleRecord { // #347: Change to an actual record :)

        private final String name;
        private final int size;

        ExampleRecord(String name, int size) {
            this.name = name;
            this.size = size;
        }

        String name() {
            return name;
        }

        int size() {
            return size;
        }
    }
}
