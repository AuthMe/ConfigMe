package ch.jalu.configme.beanmapper.definition.properties;

import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.samples.beanannotations.AnnotatedEntry;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for {@link BeanFieldPropertyDefinition}.
 */
class BeanFieldPropertyDefinitionTest {

    @Test
    void shouldGetProperties() {
        // given
        BeanFieldPropertyDefinition sizeProperty = getDescription("size", SampleBean.class);
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
        BeanFieldPropertyDefinition sizeProperty = getDescription("size", SampleBean.class);
        String wrongObject = "test";

        // when
        ConfigMeException ex = assertThrows(ConfigMeException.class,
            () -> sizeProperty.setValue(wrongObject, -120));

        // then
        assertThat(ex.getMessage(), equalTo("Failed to set value to field BeanFieldPropertyDefinitionTest$SampleBean#size. Value: -120"));
        assertThat(ex.getCause(), instanceOf(IllegalArgumentException.class));
    }

    @Test
    void shouldHandlePropertyGetError() {
        // given
        BeanPropertyDefinition sizeProperty = getDescription("size", SampleBean.class);
        String wrongObject = "test";

        // when
        ConfigMeException ex = assertThrows(ConfigMeException.class,
            () -> sizeProperty.getValue(wrongObject));

        // then
        assertThat(ex.getMessage(), equalTo("Failed to get value for field BeanFieldPropertyDefinitionTest$SampleBean#size"));
        assertThat(ex.getCause(), instanceOf(IllegalArgumentException.class));
    }

    @Test
    void shouldHaveAppropriateStringRepresentation() {
        // given
        BeanFieldPropertyDefinition hasIdProperty = getDescription("has-id", AnnotatedEntry.class);

        // when
        String output = "Found " + hasIdProperty;

        // then
        assertThat(output, equalTo("Found FieldProperty 'has-id' for field 'AnnotatedEntry#hasId'"));
    }

    @Test
    void shouldReturnExportName() {
        // given
        BeanFieldPropertyDefinition hasIdProperty = getDescription("has-id", AnnotatedEntry.class);
        BeanFieldPropertyDefinition sizeProperty = getDescription("size", SampleBean.class);

        // when
        String hasIdExportName = hasIdProperty.getExportName();
        String sizeExportName = sizeProperty.getExportName();

        // then
        assertThat(hasIdExportName, equalTo("has-id"));
        assertThat(sizeExportName, nullValue());
    }

    private static BeanFieldPropertyDefinition getDescription(String name, Class<?> clazz) {
        return new BeanPropertyExtractorImpl().collectProperties(clazz)
            .stream()
            .filter(prop -> name.equals(prop.getName()))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }

    private static class SampleBean {

        private int size;

    }
}
