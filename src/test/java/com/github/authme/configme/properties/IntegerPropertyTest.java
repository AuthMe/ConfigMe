package com.github.authme.configme.properties;

import com.github.authme.configme.resource.PropertyResource;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link IntegerProperty}.
 */
public class IntegerPropertyTest {

    private static PropertyResource resource;

    @BeforeClass
    public static void setUpConfiguration() {
        resource = mock(PropertyResource.class);
        when(resource.getInt("int.path.test")).thenReturn(27);
        when(resource.getInt("int.path.wrong")).thenReturn(null);
    }

    @Test
    public void shouldGetIntValue() {
        // given
        Property<Integer> property = new IntegerProperty("int.path.test", 3);

        // when
        int result = property.getValue(resource);

        // then
        assertThat(result, equalTo(27));
    }

    @Test
    public void shouldGetIntDefault() {
        // given
        Property<Integer> property = new IntegerProperty("int.path.wrong", -10);

        // when
        int result = property.getValue(resource);

        // then
        assertThat(result, equalTo(-10));
    }
}
