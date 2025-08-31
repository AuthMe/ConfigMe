package ch.jalu.configme.beanmapper;

import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.BeanProperty;
import ch.jalu.configme.properties.convertresult.PropertyValue;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Mapper integration test for beans with final fields.
 */
class BeanWithFinalFieldsTest {

    @Test
    void shouldThrowForFinalField() {
        // given
        BeanProperty<BeanWithFinalField> property = new BeanProperty<>("", BeanWithFinalField.class, new BeanWithFinalField());
        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getObject("")).willReturn(newMapWithName("t"));

        // when
        ConfigMeException ex = assertThrows(ConfigMeException.class, () -> property.determineValue(reader));

        // then
        assertThat(ex.getMessage(), containsString("Final fields cannot be set by the mapper"));
    }

    @Test
    void shouldNotThrowForFinalTransientField() {
        // given
        BeanProperty<BeanWithFinalTransientField> property =
            new BeanProperty<>("", BeanWithFinalTransientField.class, new BeanWithFinalTransientField());
        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getObject("")).willReturn(newMapWithName("Zoran"));

        // when
        PropertyValue<BeanWithFinalTransientField> value = property.determineValue(reader);

        // then
        assertThat(value.getValue().name, equalTo("Zoran"));
    }

    @Test
    void shouldNotThrowForFinalIgnoredField() {
        // given
        BeanProperty<BeanWithFinalIgnoredField> property =
            new BeanProperty<>("", BeanWithFinalIgnoredField.class, new BeanWithFinalIgnoredField());
        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getObject("")).willReturn(newMapWithName("Goran"));

        // when
        PropertyValue<BeanWithFinalIgnoredField> value = property.determineValue(reader);

        // then
        assertThat(value.getValue().name, equalTo("Goran"));
    }

    @Test
    void shouldNotThrowForOverriddenField() {
        // given
        BeanProperty<BeanWithFinalOverriddenField> property =
            new BeanProperty<>("", BeanWithFinalOverriddenField.class, new BeanWithFinalOverriddenField());
        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getObject("")).willReturn(newMapWithName("Bojan"));

        // when
        PropertyValue<BeanWithFinalOverriddenField> value = property.determineValue(reader);

        // then
        assertThat(value.getValue().name, equalTo("Bojan"));
    }

    private static Map<String, Object> newMapWithName(String name) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        return map;
    }

    public static class BeanWithFinalField {

        String name;
        final int version = 1;

    }

    public static class BeanWithFinalTransientField {

        String name;
        final transient int version = 2;

    }

    public static class BeanWithFinalIgnoredField {

        String name;
        @IgnoreInMapping
        final int version = 3;

    }

    public static class BeanWithFinalOverriddenField extends BeanWithFinalField {

        int version = 4;

    }
}
