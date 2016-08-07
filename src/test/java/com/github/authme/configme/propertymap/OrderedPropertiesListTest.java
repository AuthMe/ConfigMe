package com.github.authme.configme.propertymap;

import com.github.authme.configme.properties.Property;
import com.github.authme.configme.propertymap.list.OrderedPropertiesList;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link OrderedPropertiesList}.
 */
public class OrderedPropertiesListTest {

    @Test
    public void shouldKeepEntriesByInsertionAndGroup() {
        // given
        List<String> paths = Arrays.asList("japan.toyota", "indonesia.jakarta.koja", "japan.tokyo.sumida",
            "china.shanghai", "egypt.cairo", "china.shenzhen", "china.tianjin", "indonesia.jakarta.tugu",
            "egypt.luxor", "japan.nagoya", "japan.tokyo.taito");
        OrderedPropertiesList map = new OrderedPropertiesList();

        // when
        for (String path : paths) {
            Property<?> property = createPropertyWithPath(path);
            map.add(property);
        }

        // then
        List<String> resultPaths = new ArrayList<>();
        for (PropertyEntry entry : map.getEntries()) {
            resultPaths.add(entry.getProperty().getPath());
        }

        assertThat(map.getEntries(), hasSize(paths.size()));
        assertThat(map.getEntries(), hasSize(resultPaths.size()));
        assertThat(resultPaths, contains("japan.toyota", "japan.tokyo.sumida", "japan.tokyo.taito", "japan.nagoya",
            "indonesia.jakarta.koja", "indonesia.jakarta.tugu", "china.shanghai", "china.shenzhen", "china.tianjin",
            "egypt.cairo", "egypt.luxor"));
    }

    private static Property<?> createPropertyWithPath(String path) {
        Property<?> property = mock(Property.class);
        when(property.getPath()).thenReturn(path);
        return property;
    }
}
