package ch.jalu.configme.properties;

import ch.jalu.configme.resource.PropertyResource;
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

    private static PropertyResource resource;

    @BeforeClass
    @SuppressWarnings("unchecked")
    public static void setUpConfiguration() {
        resource = mock(PropertyResource.class);
        // need to have the List objects unchecked so we satisfy the List<?> signature
        List stringList = Arrays.asList("test1", "Test2", "3rd test");
        when(resource.getList("list.path.test")).thenReturn(stringList);
        when(resource.getList("list.path.wrong")).thenReturn(null);
        List mixedList = Arrays.asList("test1", "toast", 1);
        when(resource.getList("list.path.mixed")).thenReturn(mixedList);
    }

    @Test
    public void shouldGetStringListValue() {
        // given
        Property<List<String>> property = new StringListProperty("list.path.test", "1", "b");

        // when
        List<String> result = property.getValue(resource);

        // then
        assertThat(result, contains("test1", "Test2", "3rd test"));
    }

    @Test
    public void shouldGetStringListDefault() {
        // given
        Property<List<String>> property = new StringListProperty("list.path.wrong", "default", "list", "elements");

        // when
        List<String> result = property.getValue(resource);

        // then
        assertThat(result, contains("default", "list", "elements"));
    }

    @Test
    public void shouldGetStringListDefaultForMixedListFromResource() {
        // given
        Property<List<String>> property = new StringListProperty("list.path.mixed", "My", "default", "values");

        // when
        List<String> result = property.getValue(resource);

        // then
        assertThat(result, contains("My", "default", "values"));
    }

    @Test
    public void shouldCheckIfValueIsListForPresenceCheck() {
        // given
        Property<List<String>> property1 = new StringListProperty("list.path.wrong");
        Property<List<String>> property2 = new StringListProperty("list.path.mixed");

        // when
        boolean result1 = property1.isPresent(resource);
        boolean result2 = property2.isPresent(resource);

        // then
        assertThat(result1, equalTo(false));
        assertThat(result2, equalTo(true));
    }
}
