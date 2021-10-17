package ch.jalu.configme.properties;

import ch.jalu.configme.beanmapper.worldgroup.WorldGroupConfig;
import ch.jalu.configme.properties.inlinearray.StandardInlineArrayConverters;
import ch.jalu.configme.properties.types.PrimitivePropertyType;
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
import static ch.jalu.configme.properties.PropertyInitializer.optionalEnumProperty;
import static ch.jalu.configme.properties.PropertyInitializer.optionalIntegerProperty;
import static ch.jalu.configme.properties.PropertyInitializer.optionalStringProperty;
import static ch.jalu.configme.properties.PropertyInitializer.setProperty;
import static ch.jalu.configme.properties.PropertyInitializer.typeBasedProperty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

/**
 * Test for {@link PropertyInitializer}.
 */
class PropertyInitializerTest {

    @Test
    void shouldInstantiateProperties() {
        assertThat(newProperty("my.path", true), instanceOf(BooleanProperty.class));
        assertThat(newProperty("my.path", 12), instanceOf(IntegerProperty.class));
        assertThat(newProperty("my.path", -8.4), instanceOf(DoubleProperty.class));
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
        assertThat(optionalIntegerProperty("path"), instanceOf(OptionalProperty.class));
        assertThat(optionalStringProperty("path"), instanceOf(OptionalProperty.class));
        assertThat(optionalEnumProperty(TestEnum.class, "path"), instanceOf(OptionalProperty.class));
    }

    @Test
    void shouldInstantiateBuilders() {
        assertThat(typeBasedProperty(PrimitivePropertyType.STRING), instanceOf(PropertyBuilder.TypeBasedPropertyBuilder.class));
        assertThat(listProperty(PrimitivePropertyType.INTEGER), instanceOf(PropertyBuilder.ListPropertyBuilder.class));
        assertThat(setProperty(PrimitivePropertyType.FLOAT), instanceOf(PropertyBuilder.SetPropertyBuilder.class));
        assertThat(mapProperty(PrimitivePropertyType.DOUBLE), instanceOf(PropertyBuilder.MapPropertyBuilder.class));
        assertThat(arrayProperty(PrimitivePropertyType.BOOLEAN, Boolean[]::new), instanceOf(PropertyBuilder.ArrayPropertyBuilder.class));
        assertThat(inlineArrayProperty(StandardInlineArrayConverters.FLOAT), instanceOf(PropertyBuilder.InlineArrayPropertyBuilder.class));
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
