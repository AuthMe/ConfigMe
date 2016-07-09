package com.github.authme.configme.properties;

import org.bukkit.configuration.file.FileConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link IntegerProperty}.
 */
public class IntegerPropertyTest {

    private static FileConfiguration configuration;

    @BeforeClass
    public static void setUpConfiguration() {
        configuration = mock(FileConfiguration.class);
        when(configuration.getInt(eq("int.path.test"), anyInt())).thenReturn(27);
        when(configuration.getInt(eq("int.path.wrong"), anyInt())).thenAnswer(new ReturnsArgumentAt(1));
    }

    @Test
    public void shouldGetIntValue() {
        // given
        Property<Integer> property = new IntegerProperty("int.path.test", 3);

        // when
        int result = property.getFromFile(configuration);

        // then
        assertThat(result, equalTo(27));
    }

    @Test
    public void shouldGetIntDefault() {
        // given
        Property<Integer> property = new IntegerProperty("int.path.wrong", -10);

        // when
        int result = property.getFromFile(configuration);

        // then
        assertThat(result, equalTo(-10));
    }
}
