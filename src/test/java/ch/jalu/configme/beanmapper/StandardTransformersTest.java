package ch.jalu.configme.beanmapper;

import ch.jalu.configme.beanmapper.command.CommandConfig;
import ch.jalu.configme.samples.TestEnum;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static ch.jalu.configme.TestUtils.transform;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link StandardTransformers}.
 */
public class StandardTransformersTest {

    @Test
    public void shouldReturnDefaultTransformerAsSingleton() {
        // given
        ValueTransformer defaultTransformer = StandardTransformers.getDefaultValueTransformer();

        // when
        ValueTransformer result = StandardTransformers.getDefaultValueTransformer();

        // then
        assertThat(result, sameInstance(defaultTransformer));
    }

    @Test
    public void shouldUseAllContainedClassInDefaultTransformer() {
        // given
        Class<?>[] innerClasses = StandardTransformers.class.getDeclaredClasses();

        // when
        ValueTransformer defaultValueTransformer = StandardTransformers.getDefaultValueTransformer();

        // then
        List<Class<?>> transformerTypesUsed = transform(
            ((CombiningValueTransformer) defaultValueTransformer).transformers, Object::getClass);
        assertThat(transformerTypesUsed, containsInAnyOrder(innerClasses));
    }

    @Test
    public void shouldMapToEnum() {
        // given
        String input1 = TestEnum.SECOND.name();
        String input2 = TestEnum.SECOND.name() + "bogus";
        String input3 = null;
        ValueTransformer transformer = StandardTransformers.getDefaultValueTransformer();

        // when / then
        assertThat(transformer.value(TestEnum.class, input1), equalTo(TestEnum.SECOND));
        assertThat(transformer.value(TestEnum.class, input2), nullValue());
        assertThat(transformer.value(TestEnum.class, input3), nullValue());
    }

    @Test
    public void shouldMapToString() {
        // given
        Object input1 = "str";
        Object input2 = Collections.emptyMap();
        Object input3 = null;
        ValueTransformer transformer = StandardTransformers.getDefaultValueTransformer();

        // when / then
        assertThat(transformer.value(String.class, input1), equalTo(input1));
        assertThat(transformer.value(String.class, input2), nullValue());
        assertThat(transformer.value(String.class, input3), nullValue());
    }

    @Test
    public void shouldMapToBoolean() {
        // given
        Object input1 = true;
        Object input2 = Collections.emptyMap();
        Object input3 = null;
        ValueTransformer transformer = StandardTransformers.getDefaultValueTransformer();

        // when / then
        assertThat(transformer.value(Boolean.class, input1), equalTo(true));
        assertThat(transformer.value(Boolean.class, input2), nullValue());
        assertThat(transformer.value(Boolean.class, input3), nullValue());

        assertThat(transformer.value(boolean.class, input1), equalTo(true));
        assertThat(transformer.value(boolean.class, input2), nullValue());
        assertThat(transformer.value(boolean.class, input3), nullValue());
    }

    @Test
    public void shouldMapToNumbers() {
        // given
        Object input1 = 3;
        Object input2 = Collections.emptyMap();
        Object input3 = null;
        Object input4 = -5.9;
        ValueTransformer transformer = StandardTransformers.getDefaultValueTransformer();

        // when / then
        Stream.of(Integer.class, int.class).forEach(clz -> {
            assertThat(transformer.value(clz, input1), equalTo(3));
            assertThat(transformer.value(clz, input2), nullValue());
            assertThat(transformer.value(clz, input3), nullValue());
            assertThat(transformer.value(clz, input4), equalTo(-5));
        });

        Stream.of(Double.class, double.class).forEach(clz -> {
            assertThat(transformer.value(clz, input1), equalTo(3.0));
            assertThat(transformer.value(clz, input2), nullValue());
            assertThat(transformer.value(clz, input3), nullValue());
            assertThat(transformer.value(clz, input4), equalTo(-5.9));
        });

        Stream.of(Float.class, float.class).forEach(clz -> {
            assertThat(transformer.value(clz, input1), equalTo(3.0f));
            assertThat(transformer.value(clz, input2), nullValue());
            assertThat(transformer.value(clz, input3), nullValue());
            assertThat(transformer.value(clz, input4), equalTo(-5.9f));
        });

        Stream.of(Byte.class, byte.class).forEach(clz -> {
            assertThat(transformer.value(clz, input1), equalTo((byte) 3));
            assertThat(transformer.value(clz, input2), nullValue());
            assertThat(transformer.value(clz, input3), nullValue());
            assertThat(transformer.value(clz, input4), equalTo((byte) -5));
        });

        Stream.of(Short.class, short.class).forEach(clz -> {
            assertThat(transformer.value(clz, input1), equalTo((short) 3));
            assertThat(transformer.value(clz, input2), nullValue());
            assertThat(transformer.value(clz, input3), nullValue());
            assertThat(transformer.value(clz, input4), equalTo((short) -5));
        });

        Stream.of(Long.class, long.class).forEach(clz -> {
            assertThat(transformer.value(clz, input1), equalTo(3L));
            assertThat(transformer.value(clz, input2), nullValue());
            assertThat(transformer.value(clz, input3), nullValue());
            assertThat(transformer.value(clz, input4), equalTo(-5L));
        });
    }

    @Test
    public void shouldNotMapUnknownValue() {
        // given
        ValueTransformer transformer = StandardTransformers.getDefaultValueTransformer();

        // when / then
        assertThat(transformer.value(TestEnum.class, 45), nullValue());
        assertThat(transformer.value(Map.class, Collections.emptyMap()), nullValue());
        assertThat(transformer.value(Integer[].class, 3.8), nullValue());
        assertThat(transformer.value(String.class, Optional.of("3")), nullValue());
    }

    @Test
    public void shouldExportSimpleValues() {
        // given
        ValueTransformer transformer = StandardTransformers.getDefaultValueTransformer();

        // when / then
        assertExportValueSameAsInput(transformer, 45);
        assertExportValueSameAsInput(transformer, Boolean.FALSE);
        assertExportValueSameAsInput(transformer, -8.45);
        assertExportValueSameAsInput(transformer, (short) -2);
        assertExportValueSameAsInput(transformer, "test string");
        assertThat(transformer.toExportValue(TestEnum.THIRD), equalTo(TestEnum.THIRD.name()));
    }

    @Test
    public void shouldNotExportOtherValues() {
        // given
        ValueTransformer transformer = StandardTransformers.getDefaultValueTransformer();

        // when / then
        assertThat(transformer.toExportValue(Optional.of(3253)), nullValue());
        assertThat(transformer.toExportValue(new CommandConfig()), nullValue());
        assertThat(transformer.toExportValue(String.class), nullValue());
        assertThat(transformer.toExportValue(Arrays.asList("", 5)), nullValue());
    }

    private void assertExportValueSameAsInput(ValueTransformer transformer, Object input) {
        assertThat(transformer.toExportValue(input), sameInstance(input));
    }
}
