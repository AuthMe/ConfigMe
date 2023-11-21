package ch.jalu.configme.properties;

import ch.jalu.configme.TestUtils;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.properties.convertresult.PropertyValue;
import ch.jalu.configme.properties.types.NumberType;
import ch.jalu.configme.properties.types.PropertyType;
import ch.jalu.configme.properties.types.StringType;
import ch.jalu.configme.resource.PropertyReader;
import ch.jalu.configme.resource.YamlFileResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
import static org.hamcrest.collection.IsMapWithSize.anEmptyMap;
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
        MapProperty<String> property = new MapProperty<>("map", StringType.STRING, new HashMap<>());
        Map<String, String> mapFromReader = createSampleMap();
        given(reader.getObject("map")).willReturn(mapFromReader);

        // when / then
        assertThat(property.determineValue(reader), isValidValueOf(mapFromReader));
    }

    @Test
    void shouldReturnDefaultValue() {
        // given
        MapProperty<String> property = new MapProperty<>("map", StringType.STRING, createSampleMap());
        given(reader.getObject("map")).willReturn(null);

        // when / then
        assertThat(property.determineValue(reader), isErrorValueOf(property.getDefaultValue()));
    }

    @Test
    void shouldReturnValueAsExportValue() {
        // given
        MapProperty<String> property = new MapProperty<>("map", StringType.STRING, new HashMap<>());
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
        MapProperty<Integer> property = new MapProperty<>("", new AlwaysFourPropertyType(), Collections.emptyMap());
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
        MapProperty<Integer> property = new MapProperty<>("", new AlwaysFourPropertyType(), Collections.emptyMap());

        // when
        Object exportValue = property.toExportValue(value);

        // then
        assertThat(exportValue, instanceOf(Map.class));
        assertThat(((Map<Integer, String>) exportValue).keySet(), contains("first", "second", "third", "fourth"));
    }

    @Test
    void shouldUseEmptyMapAsDefaultValue() {
        // given
        MapProperty<Integer> property = new MapProperty<>("test", NumberType.INTEGER);

        //when
        Map<String, Integer> actualDefaultValue = property.getDefaultValue();
        String actualPath = property.getPath();

        // then
        assertThat(actualDefaultValue, anEmptyMap());
        assertThat(actualPath, equalTo("test"));
    }

    private static Map<String, String> createSampleMap() {
        Map<String, String> map = new HashMap<>();
        map.put("test", "keks");
        return map;
    }

    private static class AlwaysFourPropertyType implements PropertyType<Integer> {

        @Override
        public @Nullable Integer convert(@Nullable Object object, @NotNull ConvertErrorRecorder errorRecorder) {
            return object == null ? null : 4;
        }

        @Override
        public @NotNull Integer toExportValue(@NotNull Integer value) {
            return value;
        }
    }
}
