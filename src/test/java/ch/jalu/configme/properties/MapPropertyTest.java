package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.PrimitivePropertyType;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static ch.jalu.configme.TestUtils.isErrorValueOf;
import static ch.jalu.configme.TestUtils.isValidValueOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Test for {@link MapProperty}.
 */
@RunWith(MockitoJUnitRunner.class)
public class MapPropertyTest {

    @Mock
    private PropertyReader reader;

    @Test
    public void shouldReturnValueFromResource() {
        // given
        MapProperty<String> property = new MapProperty<>("map", new HashMap<>(), PrimitivePropertyType.STRING);
        Map<String, String> mapFromReader = createSampleMap();
        given(reader.getObject("map")).willReturn(mapFromReader);

        // when / then
        assertThat(property.determineValue(reader), isValidValueOf(mapFromReader));
    }

    @Test
    public void shouldReturnDefaultValue() {
        // given
        MapProperty<String> property = new MapProperty<>("map", createSampleMap(), PrimitivePropertyType.STRING);
        given(reader.getObject("map")).willReturn(null);

        // when / then
        assertThat(property.determineValue(reader), isErrorValueOf(property.getDefaultValue()));
    }

    @Test
    public void shouldReturnValueAsExportValue() {
        // given
        MapProperty<String> property = new MapProperty<>("map", new HashMap<>(), PrimitivePropertyType.STRING);
        Map<String, String> givenMap = createSampleMap();

        // when
        Object exportValue = property.toExportValue(givenMap);

        // then
        assertThat(exportValue, instanceOf(Map.class));
        Map<String, String> resultMap = (Map) exportValue;
        assertThat(resultMap.keySet(), contains("test"));
        assertThat(resultMap.get("test"), equalTo("keks"));
    }

    private static Map<String, String> createSampleMap() {
        Map<String, String> map = new HashMap<>();
        map.put("test", "keks");
        return map;
    }
}
