package ch.jalu.configme.properties;

import ch.jalu.configme.beanmapper.worldgroup.WorldGroupConfig;
import ch.jalu.configme.properties.builder.ArrayPropertyBuilder;
import ch.jalu.configme.properties.builder.CollectionPropertyBuilder;
import ch.jalu.configme.properties.builder.MapPropertyBuilder;
import ch.jalu.configme.properties.types.BooleanType;
import ch.jalu.configme.properties.types.InlineArrayPropertyType;
import ch.jalu.configme.properties.types.NumberType;
import ch.jalu.configme.properties.types.StringType;
import ch.jalu.configme.samples.TestEnum;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.regex.Pattern;

import static ch.jalu.configme.properties.PropertyInitializer.arrayProperty;
import static ch.jalu.configme.properties.PropertyInitializer.inlineArrayProperty;
import static ch.jalu.configme.properties.PropertyInitializer.listProperty;
import static ch.jalu.configme.properties.PropertyInitializer.mapProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newBeanProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newLowercaseStringSetProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newRegexProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newSetProperty;
import static ch.jalu.configme.properties.PropertyInitializer.optionalBooleanProperty;
import static ch.jalu.configme.properties.PropertyInitializer.optionalDoubleProperty;
import static ch.jalu.configme.properties.PropertyInitializer.optionalEnumProperty;
import static ch.jalu.configme.properties.PropertyInitializer.optionalFloatProperty;
import static ch.jalu.configme.properties.PropertyInitializer.optionalIntegerProperty;
import static ch.jalu.configme.properties.PropertyInitializer.optionalListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.optionalLongProperty;
import static ch.jalu.configme.properties.PropertyInitializer.optionalRegexProperty;
import static ch.jalu.configme.properties.PropertyInitializer.optionalSetProperty;
import static ch.jalu.configme.properties.PropertyInitializer.optionalShortProperty;
import static ch.jalu.configme.properties.PropertyInitializer.optionalStringProperty;
import static ch.jalu.configme.properties.PropertyInitializer.setProperty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

/**
 * Test for {@link PropertyInitializer}.
 */
class PropertyInitializerTest {

    @Test
    void shouldInstantiateProperties() {
        assertThat(newProperty("my.path", true), instanceOf(BooleanProperty.class));
        assertThat(newProperty("my.path", (short) 5), instanceOf(ShortProperty.class));
        assertThat(newProperty("my.path", 12), instanceOf(IntegerProperty.class));
        assertThat(newProperty("my.path", 10L), instanceOf(LongProperty.class));
        assertThat(newProperty("my.path", 3.5f), instanceOf(FloatProperty.class));
        assertThat(newProperty("my.path", -8.4d), instanceOf(DoubleProperty.class));
        assertThat(newProperty("my.path", "default"), instanceOf(StringProperty.class));
        assertThat(newProperty(TestEnum.class, "my.path", TestEnum.FIRST), instanceOf(EnumProperty.class));
        assertThat(newRegexProperty("reg.path", "abc?"), instanceOf(RegexProperty.class));
        assertThat(newRegexProperty("reg.path", Pattern.compile("w[0-9]*")), instanceOf(RegexProperty.class));
        assertThat(newListProperty("path", "default", "entries"), instanceOf(StringListProperty.class));
        assertThat(newListProperty("path", Arrays.asList("a1", "a2", "a3")), instanceOf(StringListProperty.class));
        assertThat(newSetProperty("path", "some", "values"), instanceOf(StringSetProperty.class));
        assertThat(newSetProperty("path", newLinkedHashSet("ah", "hmm", "oh")), instanceOf(StringSetProperty.class));
        assertThat(newLowercaseStringSetProperty("path", "a", "b", "c"), instanceOf(LowercaseStringSetProperty.class));
        assertThat(newLowercaseStringSetProperty("path", Arrays.asList("5", "7")), instanceOf(LowercaseStringSetProperty.class));
        assertThat(newBeanProperty(WorldGroupConfig.class, "worlds", new WorldGroupConfig()), instanceOf(BeanProperty.class));

        assertThat(optionalBooleanProperty("path"), instanceOf(OptionalProperty.class));
        assertThat(optionalShortProperty("path"), instanceOf(OptionalProperty.class));
        assertThat(optionalIntegerProperty("path"), instanceOf(OptionalProperty.class));
        assertThat(optionalLongProperty("path"), instanceOf(OptionalProperty.class));
        assertThat(optionalFloatProperty("path"), instanceOf(OptionalProperty.class));
        assertThat(optionalDoubleProperty("path"), instanceOf(OptionalProperty.class));
        assertThat(optionalStringProperty("path"), instanceOf(OptionalProperty.class));
        assertThat(optionalEnumProperty(TestEnum.class, "path"), instanceOf(OptionalProperty.class));
        assertThat(optionalRegexProperty("path"), instanceOf(OptionalProperty.class));
        assertThat(optionalListProperty("path"), instanceOf(OptionalProperty.class));
        assertThat(optionalSetProperty("path"), instanceOf(OptionalProperty.class));
    }

    @Test
    void shouldInstantiateBuilders() {
        assertThat(listProperty(NumberType.INTEGER), instanceOf(CollectionPropertyBuilder.class));
        assertThat(setProperty(NumberType.FLOAT), instanceOf(CollectionPropertyBuilder.class));
        assertThat(mapProperty(NumberType.DOUBLE), instanceOf(MapPropertyBuilder.class));
        assertThat(arrayProperty(BooleanType.BOOLEAN, Boolean[]::new), instanceOf(ArrayPropertyBuilder.class));
        assertThat(arrayProperty(StringType.STRING.arrayType()), instanceOf(ArrayPropertyBuilder.class));
        assertThat(inlineArrayProperty(InlineArrayPropertyType.FLOAT), instanceOf(ArrayPropertyBuilder.class));
    }

    @Test
    void shouldHaveAccessibleNoArgsConstructorForExtensions() {
        // given / when
        new PropertyInitializer() { };

        // then - no exception
    }

    @SafeVarargs
    private static <T> LinkedHashSet<T> newLinkedHashSet(T... args) {
        return new LinkedHashSet<>(Arrays.asList(args));
    }
}
