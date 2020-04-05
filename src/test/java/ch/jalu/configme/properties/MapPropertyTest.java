package ch.jalu.configme.properties;

import ch.jalu.configme.TestUtils;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.properties.convertresult.PropertyValue;
import ch.jalu.configme.properties.types.PrimitivePropertyType;
import ch.jalu.configme.properties.types.PropertyType;
import ch.jalu.configme.resource.PropertyReader;
import ch.jalu.configme.resource.YamlFileResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static ch.jalu.configme.TestUtils.isErrorValueOf;
import static ch.jalu.configme.TestUtils.isValidValueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.BDDMockito.given;

/**
 * Test for {@link MapProperty}.
 */
@ExtendWith(MockitoExtension.class)
class MapPropertyTest {

    @Mock
    private PropertyReader reader;

    @TempDir
    public Path temporaryFolder;

    @Test
    void shouldReturnValueFromResource() {
        // given
        MapProperty<String> property = new MapProperty<>("map", new HashMap<>(), PrimitivePropertyType.STRING);
        Map<String, String> mapFromReader = createSampleMap();
        given(reader.getObject("map")).willReturn(mapFromReader);

        // when / then
        assertThat(property.determineValue(reader), isValidValueOf(mapFromReader));
    }

    @Test
    void shouldReturnDefaultValue() {
        // given
        MapProperty<String> property = new MapProperty<>("map", createSampleMap(), PrimitivePropertyType.STRING);
        given(reader.getObject("map")).willReturn(null);

        // when / then
        assertThat(property.determineValue(reader), isErrorValueOf(property.getDefaultValue()));
    }

    @Test
    void shouldReturnValueAsExportValue() {
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

    @Test
    void shouldRetainOrderAsInFile() {
        // given
        MapProperty<Integer> property = new MapProperty<>("", Collections.emptyMap(), new AlwaysFourPropertyType());
        Path file = TestUtils.copyFileFromResources("/config-sample.yml", temporaryFolder);
        YamlFileResource resource = new YamlFileResource(file);

        // when
        PropertyValue<Map<String, Integer>> result = property.determineValue(resource.createReader());

        // then
        assertThat(result.isValidInResource(), equalTo(true));
        assertThat(result.getValue().keySet(), contains("test", "sample", "version", "features", "security"));
    }

    @Test
    void shouldKeepOrderInExportValue() {
        // given
        Map<String, Integer> value = new LinkedHashMap<>();
        value.put("first", 1);
        value.put("second", 2);
        value.put("third", 3);
        value.put("fourth", 4);
        MapProperty<Integer> property = new MapProperty<>("", Collections.emptyMap(), new AlwaysFourPropertyType());

        // when
        Object exportValue = property.toExportValue(value);

        // then
        assertThat(exportValue, instanceOf(Map.class));
        assertThat(((Map<Integer, String>) exportValue).keySet(), contains("first", "second", "third", "fourth"));
    }

    private static Map<String, String> createSampleMap() {
        Map<String, String> map = new HashMap<>();
        map.put("test", "keks");
        return map;
    }

    private static class AlwaysFourPropertyType implements PropertyType<Integer> {

        @Override
        public Integer convert(Object object, ConvertErrorRecorder errorRecorder) {
            return object == null ? null : 4;
        }

        @Override
        public Object toExportValue(Integer value) {
            return value;
        }
    }
}
