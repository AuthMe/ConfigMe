package com.github.authme.configme.properties;

import org.bukkit.configuration.file.FileConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link BooleanProperty}.
 */
public class BooleanPropertyTest {

    private static FileConfiguration configuration;

    @BeforeClass
    public static void setUpConfiguration() {
        configuration = mock(FileConfiguration.class);
        when(configuration.getBoolean(eq("bool.path.test"), anyBoolean())).thenReturn(true);
        when(configuration.getBoolean(eq("bool.path.wrong"), anyBoolean())).thenAnswer(new ReturnsArgumentAt(1));
    }

    @Test
    public void shouldGetBoolValue() {
        // given
        Property<Boolean> property = new BooleanProperty("bool.path.test", false);

        // when
        boolean result = property.getFromFile(configuration);

        // then
        assertThat(result, equalTo(true));
    }

    @Test
    public void shouldGetBoolDefault() {
        // given
        Property<Boolean> property = new BooleanProperty("bool.path.wrong", true);

        // when
        boolean result = property.getFromFile(configuration);

        // then
        assertThat(result, equalTo(true));
    }
}
