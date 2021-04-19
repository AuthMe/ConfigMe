package ch.jalu.configme.beanmapper.leafvaluehandler;

import ch.jalu.configme.beanmapper.command.CommandConfig;
import ch.jalu.configme.samples.TestEnum;
import ch.jalu.configme.utils.TypeInformation;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
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

    private final LeafValueHandler standardLeafValueHandler = StandardLeafValueHandlers.getDefaultLeafValueHandler();

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
        // given / when / then
        List<Class<?>> transformerTypesUsed = transform(
            ((CombiningLeafValueHandler) standardLeafValueHandler).getHandlers(), Object::getClass);
        assertThat(transformerTypesUsed, contains(StringLeafValueHandler.class, EnumLeafValueHandler.class,
            BooleanLeafValueHandler.class, NumberLeafValueHandler.class, BigNumberLeafValueHandler.class,
            ObjectLeafValueHandler.class));
    }

    @Test
    void shouldMapValues() {
        // given / when / then
        assertThat(standardLeafValueHandler.convert(of(TestEnum.class), "FIRST"), equalTo(TestEnum.FIRST));
        assertThat(standardLeafValueHandler.convert(of(boolean.class), Boolean.TRUE), equalTo(true));
        assertThat(standardLeafValueHandler.convert(of(long.class), 34), equalTo(34L));
        assertThat(standardLeafValueHandler.convert(of(String.class), 47.765), equalTo("47.765"));
        assertThat(standardLeafValueHandler.convert(of(BigInteger.class), "47198"), equalTo(BigInteger.valueOf(47198)));
    }

    @Test
    void shouldNotMapUnknownValue() {
        // given / when / then
        assertThat(standardLeafValueHandler.convert(of(TestEnum.class), 45), nullValue());
        assertThat(standardLeafValueHandler.convert(of(Map.class), Collections.emptyMap()), nullValue());
        assertThat(standardLeafValueHandler.convert(of(Integer[].class), 3.8), nullValue());
        assertThat(standardLeafValueHandler.convert(of(String.class), Optional.of("3")), nullValue());
    }

    @Test
    void shouldExportValues() {
        // given / when / then
        assertExportValueSameAsInput(standardLeafValueHandler, 45);
        assertExportValueSameAsInput(standardLeafValueHandler, Boolean.FALSE);
        assertExportValueSameAsInput(standardLeafValueHandler, -8.45);
        assertExportValueSameAsInput(standardLeafValueHandler, (short) -2);
        assertExportValueSameAsInput(standardLeafValueHandler, "test string");
        assertThat(standardLeafValueHandler.toExportValue(TestEnum.THIRD), equalTo(TestEnum.THIRD.name()));
        assertThat(standardLeafValueHandler.toExportValue(new BigDecimal("3.14159")), equalTo("3.14159"));
    }

    @Test
    void shouldNotExportOtherValues() {
        // given / when / then
        assertThat(standardLeafValueHandler.toExportValue(Optional.of(3253)), nullValue());
        assertThat(standardLeafValueHandler.toExportValue(new CommandConfig()), nullValue());
        assertThat(standardLeafValueHandler.toExportValue(String.class), nullValue());
        assertThat(standardLeafValueHandler.toExportValue(Arrays.asList("", 5)), nullValue());
    }

    private static void assertExportValueSameAsInput(LeafValueHandler transformer, Object input) {
        assertThat(transformer.toExportValue(input), sameInstance(input));
    }

    private static TypeInformation of(Type type) {
        return new TypeInformation(type);
    }
}
