package ch.jalu.configme.resource;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

/**
 * Test for {@link MapNormalizer}.
 */
class MapNormalizerTest {

    @Test
    void shouldReturnNullForNull() {
        // given
        MapNormalizer mapNormalizer = new MapNormalizer();

        // when
        Map<String, Object> result = mapNormalizer.normalizeMap(null);

        // then
        assertThat(result, nullValue());
    }

    @Test
    void shouldHandleEmptyMap() {
        // given
        MapNormalizer mapNormalizer = new MapNormalizer();
        Map<Object, Object> map = new HashMap<>();

        // when
        Map<String, Object> result = mapNormalizer.normalizeMap(map);

        // then
        assertThat(result, sameInstance(map));
        assertThat(result, anEmptyMap());
    }

    @Test
    void shouldKeepNormalizedMap() {
        // given
        MapNormalizer mapNormalizer = new MapNormalizer();
        Map<Object, Object> map1 = new HashMap<>();
        Map<Object, Object> map2 = new HashMap<>();
        Map<Object, Object> map3 = new HashMap<>();

        Map<Object, Object> map = new HashMap<>();
        map.put("one", map1);
        map.put("two", map2);
        map1.put("three", map3);

        // when
        Map<String, Object> result = mapNormalizer.normalizeMap(map);

        // then
        assertThat(result, sameInstance(map));
        assertThat(result.get("one"), sameInstance(map1));
        assertThat(result.get("two"), sameInstance(map2));
        assertThat(map1.get("three"), sameInstance(map3));
        assertThat(map.keySet(), containsInAnyOrder("one", "two"));
        assertThat(map1.keySet(), contains("three"));
        assertThat(map2, anEmptyMap());
        assertThat(map3, anEmptyMap());
    }

    @Test
    void shouldConvertKeysToStrings() {
        // given
        MapNormalizer mapNormalizer = new MapNormalizer();
        Map<Object, Object> map = new LinkedHashMap<>();
        map.put("test", "test");
        map.put("other", "other");
        Map<Object, Object> mapWithNonStringKeys = new LinkedHashMap<>();
        mapWithNonStringKeys.put(2, "two");
        mapWithNonStringKeys.put("text", "hi");
        mapWithNonStringKeys.put('c', "cee");
        mapWithNonStringKeys.put(5.25, "5 quarter");
        map.put("map", mapWithNonStringKeys);

        // when
        Map<String, Object> result = mapNormalizer.normalizeMap(map);

        // then
        assertThat(result.keySet(), contains("test", "other", "map"));
        Map<String, Object> mapChild = (Map) result.get("map");
        assertThat(mapChild.keySet(), contains("2", "text", "c", "5.25"));
        assertThat(mapChild.get("2"), equalTo("two"));
        assertThat(mapChild.get("5.25"), equalTo("5 quarter"));
    }

    @Test
    void shouldNotSplitDots() {
        // given
        MapNormalizer mapNormalizer = new MapNormalizer();
        Map<Object, Object> nestedMap = new LinkedHashMap<>();
        nestedMap.put("entry.foo", "bar");
        nestedMap.put("other.entry", false);

        Map<Object, Object> map = new LinkedHashMap<>();
        map.put("ch.jalu.sub", nestedMap);
        map.put("ch.jalu.sup", 1);
        map.put("ch.jalu.third", 2);
        map.put(true, 3);

        // when
        Map<String, Object> result = mapNormalizer.normalizeMap(map);

        // then
        assertThat(result.keySet(), contains("ch.jalu.sub", "ch.jalu.sup", "ch.jalu.third", "true"));
        Map<String, Object> subMap = (Map) result.get("ch.jalu.sub");
        assertThat(subMap.keySet(), contains("entry.foo", "other.entry"));
    }
}
