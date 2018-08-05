package ch.jalu.configme.properties;

import ch.jalu.configme.resource.PropertyReader;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

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
        List mixedList = Arrays.asList("test1", "toast", 1);
        when(reader.getList("list.path.mixed")).thenReturn(mixedList);
    }

    @Test
    public void shouldGetStringListValue() {
        // given
        Property<List<String>> property = new StringListProperty("list.path.test", "1", "b");

        // when
        List<String> result = property.determineValue(reader);

        // then
        assertThat(result, contains("test1", "Test2", "3rd test"));
    }

    @Test
    public void shouldGetStringListDefault() {
        // given
        Property<List<String>> property = new StringListProperty("list.path.wrong", "default", "list", "elements");

        // when
        List<String> result = property.determineValue(reader);

        // then
        assertThat(result, contains("default", "list", "elements"));
    }

    @Test
    public void shouldGetStringListDefaultForMixedListFromResource() {
        // given
        Property<List<String>> property = new StringListProperty("list.path.mixed", "My", "default", "values");

        // when
        List<String> result = property.determineValue(reader);

        // then
        assertThat(result, contains("My", "default", "values"));
    }

    @Test
    public void shouldCheckIfValueIsListForPresenceCheck() {
        // given
        Property<List<String>> property1 = new StringListProperty("list.path.wrong");
        Property<List<String>> property2 = new StringListProperty("list.path.mixed");

        // when
        boolean result1 = property1.isPresent(reader);
        boolean result2 = property2.isPresent(reader);

        // then
        assertThat(result1, equalTo(false));
        assertThat(result2, equalTo(true));
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
}
