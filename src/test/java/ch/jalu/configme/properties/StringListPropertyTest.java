package ch.jalu.configme.properties;

import ch.jalu.configme.configurationdata.PropertyValue;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ch.jalu.configme.TestUtils.isErrorValueOf;
import static ch.jalu.configme.TestUtils.isValidValueOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link StringListProperty}.
 */
public class StringListPropertyTest {

    private static PropertyReader reader;

    @BeforeClass
    @SuppressWarnings("unchecked")
    public static void setUpConfiguration() {
        reader = mock(PropertyReader.class);
        // need to have the List objects unchecked so we satisfy the List<?> signature
        List stringList = Arrays.asList("test1", "Test2", "3rd test");
        when(reader.getList("list.path.test")).thenReturn(stringList);
        when(reader.getList("list.path.wrong")).thenReturn(null);
        List mixedList = Arrays.asList("test1", false, "toast", 1);
        when(reader.getList("list.path.mixed")).thenReturn(mixedList);
    }

    @Test
    public void shouldGetStringListValue() {
        // given
        Property<List<String>> property = new StringListProperty("list.path.test", "1", "b");

        // when
        PropertyValue<List<String>> result = property.determineValue(reader);

        // then
        assertThat(result, isValidValueOf(Arrays.asList("test1", "Test2", "3rd test")));
    }

    @Test
    public void shouldGetStringListDefault() {
        // given
        Property<List<String>> property = new StringListProperty("list.path.wrong", "default", "list", "elements");

        // when
        PropertyValue<List<String>> result = property.determineValue(reader);

        // then
        assertThat(result, isErrorValueOf(Arrays.asList("default", "list", "elements")));
    }

    @Test
    public void shouldGetStringListDefaultForMixedListFromResource() {
        // given
        Property<List<String>> property = new StringListProperty("list.path.mixed", "My", "default", "values");

        // when
        PropertyValue<List<String>> result = property.determineValue(reader);

        // then
        assertThat(result, isValidValueOf(Arrays.asList("test1", "false", "toast", "1")));
    }

    @Test
    public void shouldReturnValueAsExportValue() {
        // given
        Property<List<String>> property = new StringListProperty("test.path");
        List<String> value = Arrays.asList("one", "two");

        // when
        Object exportValue = property.toExportValue(value);

        // then
        assertThat(exportValue, equalTo(value));
    }

    @Test
    public void shouldHaveImmutableListAsDefaultValue() {
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
