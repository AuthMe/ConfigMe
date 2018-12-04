package ch.jalu.configme.resource;

import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link MapNormalizer}.
 */
public class MapNormalizerTest {

    private MapNormalizer mapNormalizer = new MapNormalizer();

    @Test
    public void shouldReturnEmptyMapForNull() {
        // given / when
        Map<String, Object> result = mapNormalizer.normalizeMap(null);

        // then
        assertThat(result, anEmptyMap());
    }

    @Test
    public void shouldHandleEmptyMap() {
        // given
        Map<Object, Object> map = new HashMap<>();

        // when
        Map<String, Object> result = mapNormalizer.normalizeMap(map);

        // then
        assertThat(result, sameInstance(map));
        assertThat(result, anEmptyMap());
    }

    @Test
    public void shouldKeepNormalizedMap() {
        // given
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
    public void shouldExpandPathsWithPeriod() {
        // given
        Map<Object, Object> fruits = new LinkedHashMap<>();
        fruits.put("orange", "oranges");
        fruits.put("apple", "apples");
        Map<Object, Object> map = new LinkedHashMap<>();
        map.put("fruits", fruits);
        map.put("fruits.banana", "bananas");
        map.put("vegetables.lettuce", "lettuces");
        map.put("vegetables.corn", "corns");
        map.put("sweets.candy", "candies");
        Map<Object, Object> sweets = new LinkedHashMap<>();
        sweets.put("lollipop", "lollipops");
        sweets.put("chocolate", "chocolates");
        map.put("sweets", sweets);

        // when
        Map<String, Object> result = mapNormalizer.normalizeMap(map);

        // then
        assertThat(result.keySet(), contains("fruits", "vegetables", "sweets"));
        assertThat(((Map<String, Object>) result.get("fruits")).keySet(), contains("orange", "apple", "banana"));
        assertThat(((Map<String, Object>) result.get("vegetables")).keySet(), contains("lettuce", "corn"));
        assertThat(((Map<String, Object>) result.get("sweets")).keySet(), contains("candy", "lollipop", "chocolate"));
    }

    @Test
    public void shouldConvertKeysToStrings() {
        // given
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
        assertThat(mapChild.keySet(), contains("2", "text", "c", "5"));
        Map<String, Object> nestedChildMap = (Map) mapChild.get("5");
        assertThat(nestedChildMap.keySet(), contains("25"));
        assertThat(nestedChildMap.get("25"), equalTo("5 quarter"));
    }

    @Test
    public void shouldOverrideEntriesOnClash() {
        // given
        Map<Object, Object> map = new LinkedHashMap<>();
        map.put("test", "a test");
        map.put("test.one", 1);
        map.put("test.two", 2);
        map.put("other.test", 3);
        map.put("other.more.test", 4);
        map.put("other", 0);
        map.put(null, 3);

        // when
        Map<String, Object> result = mapNormalizer.normalizeMap(map);

        // then
        assertThat(result.keySet(), contains("test", "other", "null"));
        assertThat(result.get("test"), instanceOf(Map.class));
        assertThat(((Map<String, Object>) result.get("test")).keySet(), contains("one", "two"));
        assertThat(result.get("other"), equalTo(0));
    }
}
