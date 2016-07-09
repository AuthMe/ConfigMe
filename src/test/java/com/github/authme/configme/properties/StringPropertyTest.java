package com.github.authme.configme.properties;

import org.bukkit.configuration.file.FileConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link StringProperty}.
 */
public class StringPropertyTest {

    private static FileConfiguration configuration;

    @BeforeClass
    public static void setUpConfiguration() {
        configuration = mock(FileConfiguration.class);
        when(configuration.getString(eq("str.path.test"), anyString())).thenReturn("Test value");
        when(configuration.getString(eq("str.path.wrong"), anyString())).thenAnswer(new ReturnsArgumentAt(1));
    }

    @Test
    public void shouldGetStringValue() {
        // given
        Property<String> property = new StringProperty("str.path.test", "unused default");

        // when
        String result = property.getFromFile(configuration);

        // then
        assertThat(result, equalTo("Test value"));
    }

    @Test
    public void shouldGetStringDefault() {
        // given
        Property<String> property = new StringProperty("str.path.wrong", "given default value");

        // when
        String result = property.getFromFile(configuration);

        // then
        assertThat(result, equalTo("given default value"));
    }
}
