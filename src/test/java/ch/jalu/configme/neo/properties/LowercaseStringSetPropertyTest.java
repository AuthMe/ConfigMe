package ch.jalu.configme.neo.properties;

import ch.jalu.configme.neo.resource.PropertyReader;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link LowercaseStringSetProperty}.
 */
public class LowercaseStringSetPropertyTest {

    private static PropertyReader resource;

    @BeforeClass
    @SuppressWarnings("unchecked")
    public static void setUpConfiguration() {
        resource = mock(PropertyReader.class);
        // need to have the List objects unchecked so we satisfy the List<?> signature
        List stringList = Arrays.asList("test1", "Test2", "3rd TEST");
        when(resource.getList("lowercaselist.path.test")).thenReturn(stringList);
        when(resource.getList("lowercaselist.path.wrong")).thenReturn(null);
        List mixedList = Arrays.asList('b', "test", 1);
        when(resource.getList("lowercaselist.path.mixed")).thenReturn(mixedList);
    }

    @Test
    public void shouldGetLowercaseStringListValue() {
        // given
        Property<Set<String>> property = new LowercaseStringSetProperty("lowercaselist.path.test", "1", "b");

        // when
        Set<String> result = property.determineValue(resource);

        // then
        assertThat(result, contains("test1", "test2", "3rd test"));
    }

    @Test
    public void shouldGetLowercaseStringListDefault() {
        // given
        Property<Set<String>> property =
            new LowercaseStringSetProperty("lowercaselist.path.wrong", "default", "list", "elements");

        // when
        Set<String> result = property.determineValue(resource);

        // then
        assertThat(result, contains("default", "list", "elements"));
    }

    @Test
    public void shouldGetStringListDefaultForMixedListFromResource() {
        // given
        Property<Set<String>> property =
            new LowercaseStringSetProperty("lowercaselist.path.mixed", "my", "default", "values");

        // when
        Set<String> result = property.determineValue(resource);

        // then
        assertThat(result, contains("b", "test", "1"));
    }

    @Test
    public void shouldHandleNull() {
        // given
        Property<Set<String>> property = new LowercaseStringSetProperty("path");
        List list = Arrays.asList(null, "test", null, "test");
        given(resource.getList(property.getPath())).willReturn(list);

        // when
        Set<String> result = property.determineValue(resource);

        // then
        assertThat(result, contains("null", "test"));
    }

    @Test
    public void shouldDefineExportValue() {
        // given
        Property<Set<String>> property = new LowercaseStringSetProperty("path");

        // when
        Object exportValue = property.toExportValue(new LinkedHashSet<>(Arrays.asList("first", "second", "third", "fourth")));

        // then
        assertThat(exportValue, instanceOf(Collection.class));
        assertThat((Collection<?>) exportValue, contains("first", "second", "third", "fourth"));
    }
}
