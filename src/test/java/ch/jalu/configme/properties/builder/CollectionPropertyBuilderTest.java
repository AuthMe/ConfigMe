package ch.jalu.configme.properties.builder;

import ch.jalu.configme.properties.ListProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.SetProperty;
import ch.jalu.configme.properties.types.BooleanType;
import ch.jalu.configme.properties.types.EnumPropertyType;
import ch.jalu.configme.properties.types.NumberType;
import ch.jalu.configme.properties.types.StringType;
import ch.jalu.configme.samples.TestEnum;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for {@link CollectionPropertyBuilder}.
 */
class CollectionPropertyBuilderTest {

    @Test
    void shouldCreateListProperty() {
        // given / when
        Property<List<TestEnum>> property = CollectionPropertyBuilder.listBuilder(EnumPropertyType.of(TestEnum.class))
            .path("enum.path")
            .defaultValue(TestEnum.FOURTH, TestEnum.SECOND)
            .build();

        // then
        assertThat(property, instanceOf(ListProperty.class));
        assertThat(property.getPath(), equalTo("enum.path"));
        assertThat(property.getDefaultValue(), contains(TestEnum.FOURTH, TestEnum.SECOND));
    }

    @Test
    void shouldCreateSetProperty() {
        // given / when
        Property<Set<Integer>> property = CollectionPropertyBuilder.setBuilder(NumberType.INTEGER)
            .path("path.to.set")
            .defaultValue(1, 4, 2, 5)
            .build();

        // then
        assertThat(property, instanceOf(SetProperty.class));
        assertThat(property.getPath(), equalTo("path.to.set"));
        assertThat(property.getDefaultValue(), contains(1, 4, 2, 5));
    }

    @Test
    void shouldAllowEmptyDefaultValue() {
        // given / when
        ListProperty<Double> property = CollectionPropertyBuilder.listBuilder(NumberType.DOUBLE)
            .path("defined.path")
            .build();

        // then
        assertThat(property.getDefaultValue(), empty());
    }

    @Test
    void shouldThrowIfNonEmptyDefaultValueIsOverwritten() {
        // given
        CollectionPropertyBuilder<String, ?, SetProperty<String>> builder = CollectionPropertyBuilder.setBuilder(StringType.STRING_LOWER_CASE)
            .addToDefaultValue("name");

        // when
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> builder.defaultValue("test"));

        // then
        assertThat(ex.getMessage(), equalTo("Default values have already been defined! Use addToDefaultValue to add entries individually"));
    }

    @Test
    void shouldThrowExceptionForMissingPath() {
        // given / when
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> CollectionPropertyBuilder.listBuilder(BooleanType.BOOLEAN)
            .defaultValue(true, false)
            .build());

        // then
        assertThat(ex.getMessage(), equalTo("The path of the property must be defined"));
    }

    @Test
    void shouldCreateListPropertyWithTheGivenDefaultValue() {
        // given
        List<Integer> defaultWeights = Arrays.asList(7, 11, 2);

        // when
        SetProperty<Integer> property = CollectionPropertyBuilder.setBuilder(NumberType.INTEGER)
            .defaultValue(defaultWeights)
            .path("weights")
            .build();

        // then
        assertThat(property.getPath(), equalTo("weights"));
        assertThat(property.getDefaultValue(), contains(7, 11, 2)); // Set property uses a LinkedHashSet, so check concrete order
    }

    /** Ensures that this builder has a method {@code addToDefaultValue}, which is referenced in an exception. */
    @Test
    void shouldHaveAddToDefaultValueMethod() {
        // given / when
        boolean hasMethod = Arrays.stream(CollectionPropertyBuilder.class.getDeclaredMethods())
            .anyMatch(m -> PropertyBuilderUtils.ADD_TO_DEFAULT_VALUE_METHOD.equals(m.getName()));

        // then
        assertThat(hasMethod, equalTo(true));
    }
}
