package com.github.authme.configme.properties;

import com.github.authme.configme.resource.PropertyResource;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link LowercaseStringListProperty}.
 */
public class LowercaseStringListPropertyTest {

    private static PropertyResource resource;

    @BeforeClass
    public static void setUpConfiguration() {
        resource = mock(PropertyResource.class);
        // need to have the List objects unchecked so we satisfy the List<?> signature
        List stringList = Arrays.asList("test1", "Test2", "3rd TEST");
        when(resource.getList("lowercaselist.path.test")).thenReturn(stringList);
        when(resource.getList("lowercaselist.path.wrong")).thenReturn(null);
        List mixedList = Arrays.asList("test1", "toast", 1);
        when(resource.getList("lowercaselist.path.mixed")).thenReturn(mixedList);
    }

    @Test
    public void shouldGetLowercaseStringListValue() {
        // given
        Property<List<String>> property = new LowercaseStringListProperty("lowercaselist.path.test", "1", "b");

        // when
        List<String> result = property.getValue(resource);

        // then
        assertThat(result, contains("test1", "test2", "3rd test"));
    }

    @Test
    public void shouldGetLowercaseStringListDefault() {
        // given
        Property<List<String>> property =
            new LowercaseStringListProperty("lowercaselist.path.wrong", "default", "list", "elements");

        // when
        List<String> result = property.getValue(resource);

        // then
        assertThat(result, contains("default", "list", "elements"));
    }

    @Test
    public void shouldGetStringListDefaultForMixedListFromResource() {
        // given
        Property<List<String>> property =
            new LowercaseStringListProperty("lowercaselist.path.mixed", "my", "default", "values");

        // when
        List<String> result = property.getValue(resource);

        // then
        assertThat(result, contains("my", "default", "values"));
    }
}
