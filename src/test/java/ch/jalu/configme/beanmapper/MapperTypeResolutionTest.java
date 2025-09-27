package ch.jalu.configme.beanmapper;

import ch.jalu.configme.TestUtils;
import ch.jalu.configme.beanmapper.worldgroup.GameMode;
import ch.jalu.configme.beanmapper.worldgroup.Group;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorderImpl;
import ch.jalu.configme.properties.types.BeanPropertyType;
import ch.jalu.configme.resource.YamlFileReader;
import ch.jalu.typeresolver.reference.TypeReference;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for {@link MapperImpl} for the resolution of type parameters.
 */
class MapperTypeResolutionTest {

    private final Mapper mapper = new MapperImpl();

    @Test
    void shouldMapToBeanWithGenericType() {
        // given
        Map<String, Object> map = new HashMap<>();
        map.put("title", "First test");
        map.put("length", createMap("value", 5, "comment", "Chosen randomly"));
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorderImpl();

        // when
        SimpleBean result = mapper.convertToBean(map, SimpleBean.class, errorRecorder);

        // then
        assertThat(result, notNullValue());
        assertThat(result.title, equalTo("First test"));
        CommentedValue.assertValues(result.length, 5, "Chosen randomly");
        assertThat(errorRecorder.isFullyValid(), equalTo(true));
    }

    @Test
    void shouldMapToBeanWithComplexGenericType() {
        // given
        Map<String, Object> map = new HashMap<>();
        map.put("interval", createMap("value", "MINUTES", "comment", "Longer than seconds"));
        map.put("username", createMap("value", "Bobby", "comment", "",
                                      "previousValues", Arrays.asList("Bobster", "Bober")));
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorderImpl();

        // when
        ComplexBean result = mapper.convertToBean(map, ComplexBean.class, errorRecorder);

        // then
        assertThat(result, notNullValue());
        CommentedValue.assertValues(result.interval, TimeUnit.MINUTES, "Longer than seconds");
        CommentedValue.assertValues(result.username, "Bobby", "");
        assertThat(result.username.previousValues, contains("Bobster", "Bober"));
        assertThat(errorRecorder.isFullyValid(), equalTo(true));
    }

    @Test
    void shouldMapToBeanWithTypeArg() {
        // given
        TypeReference<BeanWithTypeArg<BigDecimal>> typeReference = new TypeReference<BeanWithTypeArg<BigDecimal>>() { };
        Map<String, Object> map = new HashMap<>();
        map.put("version", 4);
        map.put("value", createMap("value", 15, "comment", "Multiple of five"));
        map.put("vectors", Arrays.asList(1.4, 1.7, 2.0));
        map.put("factors", createMap("a", 14, "d", 8, "f", 6));
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorderImpl();

        // when
        BeanWithTypeArg<BigDecimal> bean = (BeanWithTypeArg<BigDecimal>) mapper.convertToBean(map, typeReference, errorRecorder);

        // then
        assertThat(bean, notNullValue());
        assertThat(bean.version, equalTo(BigDecimal.valueOf(4)));
        CommentedValue.assertValues(bean.value, BigDecimal.valueOf(15), "Multiple of five");
        assertThat(bean.vectors, equalTo(Optional.of(Arrays.asList(
            new BigDecimal("1.4"), new BigDecimal("1.7"), new BigDecimal("2.0")))));
        assertThat(bean.factors, equalTo(createMap(
            "a", new BigDecimal("14"), "d", new BigDecimal("8"), "f", new BigDecimal("6"))));
        assertThat(errorRecorder.isFullyValid(), equalTo(true));
    }

    @Test
    void shouldMapToRecursiveBean(@TempDir Path tempDir) throws IOException {
        // given
        TypeReference<RecursiveBean<TimeUnit>> typeReference = new TypeReference<RecursiveBean<TimeUnit>>() { };
        List<String> yaml = Arrays.asList(
            "value: 'MILLISECONDS'",
            "optionalExtension:",
            "    comment: 'Fallback value'",
            "    value:",
            "        value: 'HOURS'",
            "        children: []",
            "children:",
            "    - value: 'DAYS'",
            "      children: []",
            "    - value: 'NANOSECONDS'",
            "      optionalExtension:",
            "          comment: 'more'",
            "          value: {value: 'MINUTES', children: []}",
            "          children: []",
            "      children: []");
        Path file = TestUtils.createTemporaryFile(tempDir);
        Files.write(file, yaml);
        YamlFileReader reader = new YamlFileReader(file);
        BeanPropertyType<RecursiveBean<TimeUnit>> beanPropertyType = new BeanPropertyType<>(typeReference, mapper);
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorderImpl();

        // when
        RecursiveBean<TimeUnit> result = beanPropertyType.convert(reader.getObject(""), errorRecorder);

        // then
        assertThat(result, notNullValue());
        assertThat(result.value, equalTo(TimeUnit.MILLISECONDS));
        assertThat(result.optionalExtension.isPresent(), equalTo(true));
        assertThat(result.optionalExtension.get().value.value, equalTo(TimeUnit.HOURS));
        assertThat(result.optionalExtension.get().comment, equalTo("Fallback value"));
        assertThat(result.children, hasSize(2));
        assertThat(result.children.get(0).value, equalTo(TimeUnit.DAYS));
        assertThat(result.children.get(1).value, equalTo(TimeUnit.NANOSECONDS));
        assertThat(result.children.get(1).optionalExtension.isPresent(), equalTo(true));
        assertThat(result.children.get(1).optionalExtension.get().value.value, equalTo(TimeUnit.MINUTES));
        assertThat(result.children.get(1).optionalExtension.get().comment, equalTo("more"));
        assertThat(errorRecorder.isFullyValid(), equalTo(true));
    }

    @Test
    void shouldMapToClassWithGenericTypeViaParent() {
        // given
        Map<String, Object> map = new HashMap<>();
        map.put("comment", "Howdy");
        map.put("value", 4.0);
        map.put("previousValues", Arrays.asList(8.0, 3.0));
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorderImpl();

        // when
        IntegerHistorizedValue result = mapper.convertToBean(map, IntegerHistorizedValue.class, errorRecorder);

        // then
        assertThat(result, notNullValue());
        assertThat(result.comment, equalTo("Howdy"));
        assertThat(result.value, equalTo(4));
        assertThat(result.previousValues, contains(8, 3));
        assertThat(errorRecorder.isFullyValid(), equalTo(true));
    }

    @Test
    void shouldThrowForUnclearTypeArgument() {
        // given
        Map<String, Object> map = new HashMap<>();
        map.put("comment", "Current");
        map.put("value", true);

        // when
        ConfigMeMapperException ex = assertThrows(ConfigMeMapperException.class,
            () -> mapper.convertToBean(map, HistorizedValue.class, new ConvertErrorRecorderImpl()));

        // then
        assertThat(ex.getMessage(), equalTo("The target type cannot be converted to a class, for mapping of: [Bean path: 'value', type: 'V']"));
    }

    @Test
    void shouldSupportMapWithTypeArgument() {
        // given
        TypeReference<Map<String, HistorizedValue<Double>>> typeReference = new TypeReference<Map<String, HistorizedValue<Double>>>() { };
        Map<String, Object> map = new HashMap<>();
        map.put("float", createMap("value", 5.0f, "comment", "From float", "previousValues", Arrays.asList(3.0f)));
        map.put("int", createMap("value", 12, "comment", "From integer", "previousValues", Arrays.asList(15, 8)));
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorderImpl();

        // when
        Map<String, HistorizedValue<Double>> result =
            (Map<String, HistorizedValue<Double>>) mapper.convertToBean(map, typeReference, errorRecorder);

        // then
        assertThat(result.keySet(), containsInAnyOrder("float", "int"));
        CommentedValue.assertValues(result.get("float"), 5d, "From float");
        assertThat(result.get("float").previousValues, contains(3d));
        CommentedValue.assertValues(result.get("int"), 12d, "From integer");
        assertThat(result.get("int").previousValues, contains(15d, 8d));
        assertThat(errorRecorder.isFullyValid(), equalTo(true));
    }

    @Test
    void shouldSupportSetWithTypeArgument() {
        // given
        TypeReference<LinkedHashSet<CommentedValue<Group>>> typeReference = new TypeReference<LinkedHashSet<CommentedValue<Group>>>() { };
        List<Object> list = Arrays.asList(
            createMap("comment", "1", "value", createMap("worlds", Arrays.asList("lobby", "surv"), "default-gamemode", "SURVIVAL")),
            createMap("comment", "2", "value", createMap("worlds", Arrays.asList("artistry"), "default-gamemode", "CREATIVE")));
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorderImpl();

        // when
        LinkedHashSet<CommentedValue<Group>> result =
            (LinkedHashSet<CommentedValue<Group>>) mapper.convertToBean(list, typeReference, errorRecorder);

        // then
        List<CommentedValue<Group>> resultAsList = new ArrayList<>(result);
        assertThat(resultAsList, hasSize(2));
        assertThat(resultAsList.get(0).comment, equalTo("1"));
        assertThat(resultAsList.get(0).value.getWorlds(), contains("lobby", "surv"));
        assertThat(resultAsList.get(0).value.getDefaultGamemode(), equalTo(GameMode.SURVIVAL));
        assertThat(resultAsList.get(1).comment, equalTo("2"));
        assertThat(resultAsList.get(1).value.getWorlds(), contains("artistry"));
        assertThat(resultAsList.get(1).value.getDefaultGamemode(), equalTo(GameMode.CREATIVE));
        assertThat(errorRecorder.isFullyValid(), equalTo(true));
    }

    @Test
    void shouldSupportOptionalWithTypeArgument() {
        // given
        TypeReference<Optional<BeanWithTypeArg<Pattern>>> typeReference = new TypeReference<Optional<BeanWithTypeArg<Pattern>>>() { };
        Map<String, Object> map = new HashMap<>();
        map.put("version", "v[0-9]+");
        map.put("value", createMap("value", "[a-z0-9]*", "comment", "Only lowercase"));
        map.put("factors", createMap("a", "\\d+", "b", "0\\.\\d+"));
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorderImpl();

        // when
        Optional<BeanWithTypeArg<Pattern>> result =
            (Optional<BeanWithTypeArg<Pattern>>) mapper.convertToBean(map, typeReference, errorRecorder);

        // then
        assertThat(result.isPresent(), equalTo(true));
        assertThat(result.get().version.pattern(), equalTo("v[0-9]+"));
        assertThat(result.get().value.comment, equalTo("Only lowercase"));
        assertThat(result.get().value.value.pattern(), equalTo("[a-z0-9]*"));
        assertThat(result.get().factors.keySet(), containsInAnyOrder("a", "b"));
        assertThat(errorRecorder.isFullyValid(), equalTo(true));
    }

    // #347: Replace with Map#of
    private static Map<String, Object> createMap(String k1, Object v1, String k2, Object v2) {
        Map<String, Object> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    // #347: Replace with Map#of
    private static Map<String, Object> createMap(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        Map<String, Object> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }


    // -------------------
    // Bean types
    // -------------------

    static class SimpleBean {

        String title = "foo";
        CommentedValue<Integer> length;

    }

    static class ComplexBean {

        CommentedValue<TimeUnit> interval;
        HistorizedValue<String> username;

    }

    static class BeanWithTypeArg<Q> {

        Q version;
        CommentedValue<Q> value;
        Optional<List<Q>> vectors;
        Map<String, Q> factors = new HashMap<>();

    }

    static class RecursiveBean<T> {

        T value;
        List<RecursiveBean<T>> children = new ArrayList<>();
        Optional<CommentedValue<RecursiveBean<T>>> optionalExtension;

    }


    // -------------------
    // Helper types
    // -------------------

    static class CommentedValue<T> {

        T value;
        String comment;

        static void assertValues(@Nullable CommentedValue<?> actualValue,
                                 Object expectedValue, String expectedComment) {
            assertThat(actualValue, notNullValue());
            assertThat(actualValue.value, equalTo(expectedValue));
            assertThat(actualValue.comment, equalTo(expectedComment));
        }
    }

    static class HistorizedValue<V> extends CommentedValue<V> {

        List<V> previousValues = new ArrayList<>();

    }

    static class IntegerHistorizedValue extends HistorizedValue<Integer> {
    }
}
