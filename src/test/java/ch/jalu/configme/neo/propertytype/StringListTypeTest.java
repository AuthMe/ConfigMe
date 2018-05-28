package ch.jalu.configme.neo.propertytype;

import ch.jalu.configme.neo.resource.PropertyReader;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link StringListType}.
 */
public class StringListTypeTest {

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
        PropertyType<List<String>> type = new StringListType();

        // when
        List<String> result = type.getFromReader(reader, "list.path.test");

        // then
        assertThat(result, contains("test1", "Test2", "3rd test"));
    }

    @Test
    public void shouldReturnNullForMissingValue() {
        // given
        PropertyType<List<String>> type = new StringListType();

        // when
        List<String> result = type.getFromReader(reader, "list.path.wrong");

        // then
        assertThat(result, nullValue());
    }

    @Test
    public void shouldReturnNullForMixedListFromResource() {
        // given
        PropertyType<List<String>> type = new StringListType();

        // when
        List<String> result = type.getFromReader(reader, "list.path.mixed");

        // then
        assertThat(result, nullValue()); // TODO: Behavior subject to change.
    }

    @Test
    public void shouldCheckIfValueIsListForPresenceCheck() {
        // given
        PropertyType<List<String>> type = new StringListType();

        // when
        boolean result1 = type.isPresent(reader, "list.path.wrong");
        boolean result2 = type.isPresent(reader, "list.path.mixed");

        // then
        assertThat(result1, equalTo(false));
        assertThat(result2, equalTo(true));
    }

    @Test
    public void shouldReturnValueAsExportValue() {
        // given
        PropertyType<List<String>> type = new StringListType();
        List<String> value = Arrays.asList("one", "two");

        // when
        Object exportValue = type.toExportValue(value);

        // then
        assertThat(exportValue, equalTo(value));
    }
}
