package ch.jalu.configme.properties.types;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.samples.TestEnum;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.sameInstance;

/**
 * Test for {@link MapPropertyType}.
 */
class MapPropertyTypeTest {

    @Test
    void shouldBuildMap() {
        // given
        MapPropertyType<TestEnum> mapType = new MapPropertyType<>(EnumPropertyType.of(TestEnum.class));
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        Map<Integer, String> inputMap = new LinkedHashMap<>();
        inputMap.put(1, "SECOND");
        inputMap.put(3, "THIRD");
        inputMap.put(2, "FIRST");

        // when
        Map<String, TestEnum> result = mapType.convert(inputMap, errorRecorder);

        // then
        assertThat(result, instanceOf(LinkedHashMap.class));
        assertThat(result.keySet(), contains("1", "3", "2")); // retains key order
        assertThat(result.get("1"), equalTo(TestEnum.SECOND));
        assertThat(result.get("3"), equalTo(TestEnum.THIRD));
        assertThat(result.get("2"), equalTo(TestEnum.FIRST));
        assertThat(errorRecorder.isFullyValid(), equalTo(true));
    }

    @Test
    void shouldBuildMapAndSkipInvalidValue() {
        // given
        MapPropertyType<TestEnum> mapType = new MapPropertyType<>(EnumPropertyType.of(TestEnum.class));
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        Map<Integer, String> inputMap = new LinkedHashMap<>();
        inputMap.put(1, "SECOND");
        inputMap.put(3, "bogus");
        inputMap.put(2, "FIRST");

        // when
        Map<String, TestEnum> result = mapType.convert(inputMap, errorRecorder);

        // then
        assertThat(result, instanceOf(LinkedHashMap.class));
        assertThat(result.keySet(), contains("1", "2"));
        assertThat(result.get("1"), equalTo(TestEnum.SECOND));
        assertThat(result.get("2"), equalTo(TestEnum.FIRST));
        assertThat(errorRecorder.isFullyValid(), equalTo(false));
    }

    @Test
    void shouldBuildMapAndSkipNullKey() {
        // given
        MapPropertyType<TestEnum> mapType = new MapPropertyType<>(EnumPropertyType.of(TestEnum.class));
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        Map<Integer, String> inputMap = new LinkedHashMap<>();
        inputMap.put(1, "SECOND");
        inputMap.put(3, "THIRD");
        inputMap.put(null, "FIRST");

        // when
        Map<String, TestEnum> result = mapType.convert(inputMap, errorRecorder);

        // then
        assertThat(result, instanceOf(LinkedHashMap.class));
        assertThat(result.keySet(), contains("1", "3"));
        assertThat(result.get("1"), equalTo(TestEnum.SECOND));
        assertThat(result.get("3"), equalTo(TestEnum.THIRD));
        assertThat(errorRecorder.isFullyValid(), equalTo(false));
    }

    @Test
    void shouldRegisterErrorOnDuplicateKey() {
        // given
        MapPropertyType<TestEnum> mapType = new MapPropertyType<>(EnumPropertyType.of(TestEnum.class));
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        Map<Object, String> inputMap = new LinkedHashMap<>();
        inputMap.put(1, "SECOND");
        inputMap.put("3", "THIRD");
        inputMap.put(3, "FIRST");

        // when
        Map<String, TestEnum> result = mapType.convert(inputMap, errorRecorder);

        // then
        assertThat(result, instanceOf(LinkedHashMap.class));
        assertThat(result.keySet(), contains("1", "3"));
        assertThat(result.get("1"), equalTo(TestEnum.SECOND));
        assertThat(result.get("3"), equalTo(TestEnum.FIRST));
        assertThat(errorRecorder.isFullyValid(), equalTo(false));
    }

    @Test
    void shouldExportMap() {
        // given
        MapPropertyType<TestEnum> mapType = new MapPropertyType<>(EnumPropertyType.of(TestEnum.class));

        Map<String, TestEnum> propertyValue = new LinkedHashMap<>();
        propertyValue.put("1", TestEnum.SECOND);
        propertyValue.put("3", TestEnum.THIRD);
        propertyValue.put("2", TestEnum.FIRST);

        // when
        Map<String, Object> exportMap = mapType.toExportValue(propertyValue);

        // then
        assertThat(exportMap, instanceOf(LinkedHashMap.class));
        assertThat(exportMap.keySet(), contains("1", "3", "2")); // retains key order
        assertThat(exportMap.get("1"), equalTo("SECOND"));
        assertThat(exportMap.get("3"), equalTo("THIRD"));
        assertThat(exportMap.get("2"), equalTo("FIRST"));
    }

    @Test
    void shouldReturnValueType() {
        // given
        EnumPropertyType<TestEnum> valueType = EnumPropertyType.of(TestEnum.class);
        MapPropertyType<TestEnum> mapType = new MapPropertyType<>(valueType);

        // when
        PropertyType<TestEnum> result = mapType.getValueType();

        // then
        assertThat(result, sameInstance(valueType));
    }
}
