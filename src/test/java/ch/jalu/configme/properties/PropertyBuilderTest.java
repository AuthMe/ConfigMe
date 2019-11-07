package ch.jalu.configme.properties;

import ch.jalu.configme.properties.inlinearray.StandardInlineArrayConverters;
import ch.jalu.configme.properties.types.EnumPropertyType;
import ch.jalu.configme.properties.types.PrimitivePropertyType;
import ch.jalu.configme.samples.TestEnum;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for {@link PropertyBuilder}.
 */
class PropertyBuilderTest {

    @Test
    void shouldCreateCommonProperty() {
        // given / when
        Property<Integer> result = new PropertyBuilder.TypeBasedPropertyBuilder<>(PrimitivePropertyType.INTEGER)
            .path("my.path.test")
            .defaultValue(3)
            .build();

        // then
        assertThat(result, instanceOf(TypeBasedProperty.class));
        assertThat(result.getPath(), equalTo("my.path.test"));
        assertThat(result.getDefaultValue(), equalTo(3));
    }

    @Test
    void shouldCreateCommonPropertyWithCustomFunction() {
        // given / when
        Property<String> result = new PropertyBuilder.TypeBasedPropertyBuilder<>(PrimitivePropertyType.STRING)
            .path("my.path")
            .defaultValue("seven")
            .createFunction((path, val, type) -> new StringProperty(path, val))
            .build();

        // then
        assertThat(result, instanceOf(StringProperty.class));
    }

    @Test
    void shouldThrowForMissingPathInCommonPropertyBuilder() {
        // given / when
        NullPointerException e = assertThrows(NullPointerException.class,
            () -> new PropertyBuilder.TypeBasedPropertyBuilder<>(PrimitivePropertyType.STRING)
                .defaultValue("Hello")
                .build());

        // then
        assertThat(e.getMessage(), equalTo("path"));
    }

    @Test
    void shouldThrowForMissingDefaultValueInCommonPropertyBuilder() {
        // given / when
        NullPointerException e = assertThrows(NullPointerException.class,
            () -> new PropertyBuilder.TypeBasedPropertyBuilder<>(PrimitivePropertyType.STRING)
                .path("a.path")
                .createFunction((path, val, type) -> new StringProperty(path, val))
                .build());

        // then
        assertThat(e.getMessage(), equalTo("defaultValue"));
    }

    @Test
    void shouldThrowForMissingPropertyTypeInCommonPropertyBuilder() {
        // given / when
        NullPointerException e = assertThrows(NullPointerException.class,
            () -> new PropertyBuilder.TypeBasedPropertyBuilder<>(null)
                .path("random.path")
                .defaultValue("Hello")
                .build());

        // then
        assertThat(e.getMessage(), equalTo("type"));
    }

    @Test
    void shouldCreateMapProperty() {
        // given / when
        MapProperty<Double> result = new PropertyBuilder.MapPropertyBuilder<>(PrimitivePropertyType.DOUBLE)
            .path("the.path")
            .defaultEntry("leet", 1337.0)
            .defaultEntry("all", 411.411)
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
        MapProperty<Double> result = new PropertyBuilder.MapPropertyBuilder<>(PrimitivePropertyType.DOUBLE)
            .path("some.path")
            .build();

        // then
        assertThat(result.getDefaultValue(), anEmptyMap());
    }

    @Test
    void shouldThrowForMissingPathInMapBuilder() {
        // given / when
        NullPointerException e = assertThrows(NullPointerException.class,
            () -> new PropertyBuilder.MapPropertyBuilder<>(PrimitivePropertyType.DOUBLE).build());

        // then
        assertThat(e.getMessage(), equalTo("path"));
    }

    @Test
    void shouldCreateArrayProperty() {
        // given / when
        Property<Long[]> property = new PropertyBuilder.ArrayPropertyBuilder<>(PrimitivePropertyType.LONG, Long[]::new)
            .path("given.path")
            .defaultValue(5L, 11L, 23L)
            .build();

        // then
        assertThat(property, instanceOf(ArrayProperty.class));
        assertThat(property.getPath(), equalTo("given.path"));
        assertThat(property.getDefaultValue(), arrayContaining(5L, 11L, 23L));
    }

    @Test
    void shouldCreateInlineArrayProperty() {
        // given / when
        Property<Float[]> property = new PropertyBuilder.InlineArrayPropertyBuilder<>(StandardInlineArrayConverters.FLOAT)
            .path("one.path")
            .defaultValue(-1.23f)
            .build();

        // then
        assertThat(property, instanceOf(InlineArrayProperty.class));
        assertThat(property.getPath(), equalTo("one.path"));
        assertThat(property.getDefaultValue(), arrayContaining(-1.23f));
    }

    @Test
    void shouldCreateListProperty() {
        // given / when
        Property<List<TestEnum>> property = new PropertyBuilder.ListPropertyBuilder<>(EnumPropertyType.of(TestEnum.class))
            .path("enum.path")
            .defaultValue(TestEnum.FOURTH, TestEnum.SECOND)
            .build();

        // then
        assertThat(property, instanceOf(ListProperty.class));
        assertThat(property.getPath(), equalTo("enum.path"));
        assertThat(property.getDefaultValue(), contains(TestEnum.FOURTH, TestEnum.SECOND));
    }

    @Test
    void shouldThrowForMissingDefaultValue() {
        // given / when / then
        assertThrows(NullPointerException.class,
            () -> new PropertyBuilder.ListPropertyBuilder<>(PrimitivePropertyType.DOUBLE)
                .path("defined.path")
                .build());
    }
}
