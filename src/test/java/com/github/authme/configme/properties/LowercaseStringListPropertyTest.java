package com.github.authme.configme.properties;

import org.bukkit.configuration.file.FileConfiguration;
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

    private static FileConfiguration configuration;

    @BeforeClass
    public static void setUpConfiguration() {
        configuration = mock(FileConfiguration.class);
        when(configuration.isList("lowercaselist.path.test")).thenReturn(true);
        when(configuration.getStringList("lowercaselist.path.test"))
            .thenReturn(Arrays.asList("test1", "Test2", "3rd test"));
        when(configuration.isList("lowercaselist.path.wrong")).thenReturn(false);
    }

    @Test
    public void shouldGetLowercaseStringListValue() {
        // given
        Property<List<String>> property = new LowercaseStringListProperty("lowercaselist.path.test", "1", "b");

        // when
        List<String> result = property.getFromFile(configuration);

        // then
        assertThat(result, contains("test1", "test2", "3rd test"));
    }

    @Test
    public void shouldGetLowercaseStringListDefault() {
        // given
        Property<List<String>> property =
            new LowercaseStringListProperty("lowercaselist.path.wrong", "default", "list", "elements");

        // when
        List<String> result = property.getFromFile(configuration);

        // then
        assertThat(result, contains("default", "list", "elements"));
    }
}
