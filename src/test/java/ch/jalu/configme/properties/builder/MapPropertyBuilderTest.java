package ch.jalu.configme.properties.builder;

import ch.jalu.configme.properties.MapProperty;
import ch.jalu.configme.properties.types.NumberType;
import ch.jalu.configme.properties.types.StringType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for {@link MapPropertyBuilder}.
 */
class MapPropertyBuilderTest {

    @Test
    void shouldCreateMapProperty() {
        // given / when
        MapProperty<Double> result = MapPropertyBuilder.mapBuilder(NumberType.DOUBLE)
            .path("the.path")
            .addToDefaultValue("leet", 1337.0)
            .addToDefaultValue("all", 411.411)
            .build();

        // then
        assertThat(result.getPath(), equalTo("the.path"));
        Map<String, Double> defaultValue = result.getDefaultValue();
        assertThat(defaultValue.keySet(), contains("leet", "all"));
        assertThat(defaultValue.get("leet"), equalTo(1337.0));
        assertThat(defaultValue.get("all"), equalTo(411.411));
    }

    @Test
    void shouldCreateMapPropertyWithEmptyMapAsDefault() {
        // given / when
        MapProperty<Double> result = MapPropertyBuilder.mapBuilder(NumberType.DOUBLE)
            .path("some.path")
            .build();

        // then
        assertThat(result.getDefaultValue(), anEmptyMap());
    }

    @Test
    void shouldThrowForMissingPathInMapBuilder() {
        // given / when
        Exception ex = assertThrows(IllegalStateException.class,
            () -> MapPropertyBuilder.mapBuilder(NumberType.DOUBLE).build());

        // then
        assertThat(ex.getMessage(), equalTo("The path of the property must be defined"));
    }

    @Test
    void shouldThrowIfNonEmptyDefaultValueIsOverwritten() {
        // given
        MapPropertyBuilder<String, ?, MapProperty<String>> builder = MapPropertyBuilder.mapBuilder(StringType.STRING)
            .path("aliases")
            .addToDefaultValue("cd", "dir");
        Map<String, String> newDefaultValues = new HashMap<>();

        // when
        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> builder.defaultValue(newDefaultValues));

        // then
        assertThat(ex.getMessage(), equalTo("Default values have already been defined! Use addToDefaultValue to add entries individually"));
    }

    @Test
    void shouldCreatePropertyWithDefaultValueFromOtherMap() {
        // given
        TreeMap<String, Integer> defaultValues = new TreeMap<>();
        defaultValues.put("three", 3);
        defaultValues.put("four", 4);
        defaultValues.put("one", 1);

        // when
        MapProperty<Integer> property = MapPropertyBuilder.mapBuilder(NumberType.INTEGER)
            .defaultValue(defaultValues)
            .path("translations")
            .build();

        // then
        assertThat(property.getPath(), equalTo("translations"));
        assertThat(property.getDefaultValue().keySet(), contains("four", "one", "three")); // TreeSet keeps its keys sorted
    }

    @Test
    void shouldCreatePropertyWithDefaultValueFromMapEntries() {
        // given
        TreeMap<String, Integer> map = new TreeMap<>();
        map.put("three", 3);
        map.put("four", 4);
        map.put("one", 1);

        List<Map.Entry<String, Integer>> entriesForDefaultValue = map.entrySet().stream()
            .filter(e -> e.getValue() % 2 == 1)
            .collect(Collectors.toList());

        MapPropertyBuilder<Integer, ?, MapProperty<Integer>> builder = MapPropertyBuilder.mapBuilder(NumberType.INTEGER)
            .path("translations");

        // when
        entriesForDefaultValue.forEach(builder::addToDefaultValue);

        // then
        MapProperty<Integer> property = builder.build();
        assertThat(property.getDefaultValue().keySet(), contains("one", "three"));
    }

    /** Ensures that this builder has a method {@code addToDefaultValue}, which is referenced in an exception. */
    @Test
    void shouldHaveAddToDefaultValueMethod() {
        // given / when
        boolean hasMethod = Arrays.stream(MapPropertyBuilder.class.getDeclaredMethods())
            .anyMatch(m -> PropertyBuilderUtils.ADD_TO_DEFAULT_VALUE_METHOD.equals(m.getName()));

        // then
        assertThat(hasMethod, equalTo(true));
    }
}
