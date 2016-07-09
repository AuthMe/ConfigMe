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
 * Test for {@link StringListProperty}.
 */
public class StringListPropertyTest {

    private static FileConfiguration configuration;

    @BeforeClass
    public static void setUpConfiguration() {
        configuration = mock(FileConfiguration.class);
        when(configuration.isList("list.path.test")).thenReturn(true);
        when(configuration.getStringList("list.path.test")).thenReturn(Arrays.asList("test1", "Test2", "3rd test"));
        when(configuration.isList("list.path.wrong")).thenReturn(false);
    }

    @Test
    public void shouldGetStringListValue() {
        // given
        Property<List<String>> property = new StringListProperty("list.path.test", "1", "b");

        // when
        List<String> result = property.getFromFile(configuration);

        // then
        assertThat(result, contains("test1", "Test2", "3rd test"));
    }

    @Test
    public void shouldGetStringListDefault() {
        // given
        Property<List<String>> property = new StringListProperty("list.path.wrong", "default", "list", "elements");

        // when
        List<String> result = property.getFromFile(configuration);

        // then
        assertThat(result, contains("default", "list", "elements"));
    }
}
