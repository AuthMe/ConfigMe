package ch.jalu.configme.beanmapper.leafvaluehandler;

import ch.jalu.configme.beanmapper.command.CommandConfig;
import ch.jalu.configme.samples.TestEnum;
import ch.jalu.configme.utils.TypeInformation;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ch.jalu.configme.TestUtils.transform;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

/**
 * Test for {@link StandardLeafValueHandlers}.
 */
class StandardLeafValueHandlersTest {

    @Test
    void shouldReturnDefaultTransformerAsSingleton() {
        // given
        LeafValueHandler defaultTransformer = StandardLeafValueHandlers.getDefaultLeafValueHandler();

        // when
        LeafValueHandler result = StandardLeafValueHandlers.getDefaultLeafValueHandler();

        // then
        assertThat(result, sameInstance(defaultTransformer));
    }

    @Test
    void shouldCombineLeafValueHandlers() {
        // given / when
        LeafValueHandler defaultValueTransformer = StandardLeafValueHandlers.getDefaultLeafValueHandler();

        // then
        List<Class<?>> transformerTypesUsed = transform(
            ((CombiningLeafValueHandler) defaultValueTransformer).getHandlers(), Object::getClass);
        assertThat(transformerTypesUsed, contains(StringLeafValueHandler.class, EnumLeafValueHandler.class,
            BooleanLeafValueHandler.class, NumberLeafValueHandler.class, BigNumberLeafValueHandler.class,
            ObjectLeafValueHandler.class));
    }

    @Test
    void shouldNotMapUnknownValue() {
        // given
        LeafValueHandler transformer = StandardLeafValueHandlers.getDefaultLeafValueHandler();

        // when / then
        assertThat(transformer.convert(of(TestEnum.class), 45), nullValue());
        assertThat(transformer.convert(of(Map.class), Collections.emptyMap()), nullValue());
        assertThat(transformer.convert(of(Integer[].class), 3.8), nullValue());
        assertThat(transformer.convert(of(String.class), Optional.of("3")), nullValue());
    }

    @Test
    void shouldExportSimpleValues() {
        // given
        LeafValueHandler transformer = StandardLeafValueHandlers.getDefaultLeafValueHandler();

        // when / then
        assertExportValueSameAsInput(transformer, 45);
        assertExportValueSameAsInput(transformer, Boolean.FALSE);
        assertExportValueSameAsInput(transformer, -8.45);
        assertExportValueSameAsInput(transformer, (short) -2);
        assertExportValueSameAsInput(transformer, "test string");
        assertThat(transformer.toExportValue(TestEnum.THIRD), equalTo(TestEnum.THIRD.name()));
    }

    @Test
    void shouldNotExportOtherValues() {
        // given
        LeafValueHandler transformer = StandardLeafValueHandlers.getDefaultLeafValueHandler();

        // when / then
        assertThat(transformer.toExportValue(Optional.of(3253)), nullValue());
        assertThat(transformer.toExportValue(new CommandConfig()), nullValue());
        assertThat(transformer.toExportValue(String.class), nullValue());
        assertThat(transformer.toExportValue(Arrays.asList("", 5)), nullValue());
    }

    private void assertExportValueSameAsInput(LeafValueHandler transformer, Object input) {
        assertThat(transformer.toExportValue(input), sameInstance(input));
    }

    private static TypeInformation of(Type type) {
        return new TypeInformation(type);
    }
}
