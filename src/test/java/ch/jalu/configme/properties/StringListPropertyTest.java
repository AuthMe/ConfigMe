package ch.jalu.configme.properties;

import ch.jalu.configme.properties.convertresult.PropertyValue;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ch.jalu.configme.TestUtils.isErrorValueOf;
import static ch.jalu.configme.TestUtils.isValidValueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link StringListProperty}.
 */
class StringListPropertyTest {

    private static PropertyReader reader;

    @BeforeAll
    static void setUpConfiguration() {
        reader = mock(PropertyReader.class);
        List<String> stringList = Arrays.asList("test1", "Test2", "3rd test");
        given(reader.getObject("list.path.test")).willReturn(stringList);
        given(reader.getObject("list.path.wrong")).willReturn(null);
        List<Object> mixedList = Arrays.asList("test1", false, "toast", 1);
        given(reader.getObject("list.path.mixed")).willReturn(mixedList);
    }

    @Test
    void shouldGetStringListValue() {
        // given
        Property<List<String>> property = new StringListProperty("list.path.test", "1", "b");

        // when
        PropertyValue<List<String>> result = property.determineValue(reader);

        // then
        assertThat(result, isValidValueOf(Arrays.asList("test1", "Test2", "3rd test")));
    }

    @Test
    void shouldGetStringListDefault() {
        // given
        Property<List<String>> property = new StringListProperty("list.path.wrong", "default", "list", "elements");

        // when
        PropertyValue<List<String>> result = property.determineValue(reader);

        // then
        assertThat(result, isErrorValueOf(Arrays.asList("default", "list", "elements")));
    }

    @Test
    void shouldGetStringListDefaultForMixedListFromResource() {
        // given
        Property<List<String>> property = new StringListProperty("list.path.mixed", "My", "default", "values");

        // when
        PropertyValue<List<String>> result = property.determineValue(reader);

        // then
        assertThat(result, isValidValueOf(Arrays.asList("test1", "false", "toast", "1")));
    }

    @Test
    void shouldReturnValueAsExportValue() {
        // given
        Property<List<String>> property = new StringListProperty("test.path");
        List<String> value = Arrays.asList("one", "two");

        // when
        Object exportValue = property.toExportValue(value);

        // then
        assertThat(exportValue, equalTo(value));
    }

    @Test
    void shouldHaveImmutableListAsDefaultValue() {
        // given
        List<String> list = new ArrayList<>();
        list.add("Two");
        list.add("Three");
        StringListProperty propertyCreatedWithList = new StringListProperty("test.path", list);
        StringListProperty propertyCreatedWithVarargs = new StringListProperty("test.path", "One", "Two");

        // when
        List<String> default1 = propertyCreatedWithList.getDefaultValue();
        List<String> default2 = propertyCreatedWithVarargs.getDefaultValue();

        // then
        assertThat(default1, contains("Two", "Three"));
        assertThat(default1.getClass().getName(), equalTo("java.util.Collections$UnmodifiableRandomAccessList"));
        assertThat(default2, contains("One", "Two"));
        assertThat(default2.getClass().getName(), equalTo("java.util.Collections$UnmodifiableRandomAccessList"));
    }
}
