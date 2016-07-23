package com.github.authme.configme.properties;

import com.github.authme.configme.resource.PropertyResource;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link StringProperty}.
 */
public class StringPropertyTest {

    private static PropertyResource resource;

    @BeforeClass
    public static void setUpConfiguration() {
        resource = mock(PropertyResource.class);
        when(resource.getString("str.path.test")).thenReturn("Test value");
        when(resource.getString("str.path.wrong")).thenReturn(null);
    }

    @Test
    public void shouldGetStringValue() {
        // given
        Property<String> property = new StringProperty("str.path.test", "unused default");

        // when
        String result = property.getValue(resource);

        // then
        assertThat(result, equalTo("Test value"));
    }

    @Test
    public void shouldGetStringDefault() {
        // given
        Property<String> property = new StringProperty("str.path.wrong", "given default value");

        // when
        String result = property.getValue(resource);

        // then
        assertThat(result, equalTo("given default value"));
    }
}
