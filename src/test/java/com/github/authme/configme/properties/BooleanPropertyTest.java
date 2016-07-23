package com.github.authme.configme.properties;

import com.github.authme.configme.resource.PropertyResource;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link BooleanProperty}.
 */
public class BooleanPropertyTest {

    private static PropertyResource resource;

    @BeforeClass
    public static void setUpConfiguration() {
        resource = mock(PropertyResource.class);
        when(resource.getBoolean("bool.path.test")).thenReturn(true);
        when(resource.getBoolean("bool.path.wrong")).thenReturn(null);
    }

    @Test
    public void shouldGetBoolValue() {
        // given
        Property<Boolean> property = new BooleanProperty("bool.path.test", false);

        // when
        boolean result = property.getValue(resource);

        // then
        assertThat(result, equalTo(true));
    }

    @Test
    public void shouldGetBoolDefault() {
        // given
        Property<Boolean> property = new BooleanProperty("bool.path.wrong", true);

        // when
        boolean result = property.getValue(resource);

        // then
        assertThat(result, equalTo(true));
    }
}
