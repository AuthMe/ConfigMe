package ch.jalu.configme.properties.builder;

import ch.jalu.configme.properties.ArrayProperty;
import ch.jalu.configme.properties.InlineArrayProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.types.BooleanType;
import ch.jalu.configme.properties.types.InlineArrayPropertyType;
import ch.jalu.configme.properties.types.NumberType;
import ch.jalu.configme.properties.types.StringType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ch.jalu.configme.properties.builder.ArrayPropertyBuilder.arrayBuilder;
import static ch.jalu.configme.properties.builder.ArrayPropertyBuilder.inlineArrayBuilder;

/**
 * Test for {@link ArrayPropertyBuilder}.
 */
class ArrayPropertyBuilderTest {

    @Test
    void shouldCreateInlineArrayProperty() {
        // given / when
        Property<Float[]> property = ArrayPropertyBuilder.inlineArrayBuilder(InlineArrayPropertyType.FLOAT)
            .path("one.path")
            .defaultValue(-1.23f)
            .build();

        // then
        assertThat(property, instanceOf(InlineArrayProperty.class));
        assertThat(property.getPath(), equalTo("one.path"));
        assertThat(property.getDefaultValue(), arrayContaining(-1.23f));
    }

    @Test
    void shouldCreateArrayProperty() {
        // given / when
        Property<Long[]> property = ArrayPropertyBuilder.arrayBuilder(NumberType.LONG, Long[]::new)
            .path("given.path")
            .defaultValue(5L, 11L, 23L)
            .build();

        // then
        assertThat(property, instanceOf(ArrayProperty.class));
        assertThat(property.getPath(), equalTo("given.path"));
        assertThat(property.getDefaultValue(), arrayContaining(5L, 11L, 23L));
    }

    @Test
    void shouldCreateArrayWithDefaultValueSuccessivelyAddedTo() {
        // given / when
        ArrayProperty<Long> property = ArrayPropertyBuilder.arrayBuilder(NumberType.LONG.arrayType())
            .path("the.path")
            .addToDefaultValue(3L)
            .addToDefaultValue(7L)
            .addToDefaultValue(13L)
            .build();

        // then
        assertThat(property.getDefaultValue(), arrayContaining(3L, 7L, 13L));
        assertThat(property.getPath(), equalTo("the.path"));
    }

    @Test
    void shouldThrowIfNonEmptyDefaultValueIsOverridden() {
        // given
        ArrayPropertyBuilder<Double, ArrayProperty<Double>> builder = ArrayPropertyBuilder.arrayBuilder(NumberType.DOUBLE.arrayType())
            .addToDefaultValue(3.0)
            .path("soap");

        // when
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> builder.defaultValue(4.0));

        // then
        assertThat(ex.getMessage(), equalTo("Default values have already been defined! Use addToDefaultValue to add entries individually"));
    }

    @Test
    void shouldCreateArrayPropertyWithEmptyDefault() {
        // given / when
        ArrayProperty<Boolean> property = ArrayPropertyBuilder.arrayBuilder(BooleanType.BOOLEAN.arrayType())
            .path("filters")
            .build();

        // then
        assertThat(property.getDefaultValue(), emptyArray());
    }

    @Test
    void shouldThrowForMissingPath() {
        // given / when
        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> ArrayPropertyBuilder.inlineArrayBuilder(StringType.STRING.inlineArrayType(";")).build());

        // then
        assertThat(ex.getMessage(), equalTo("The path of the property must be defined"));
    }

    /** Ensures that this builder has a method {@code addToDefaultValue}, which is referenced in an exception. */
    @Test
    void shouldHaveAddToDefaultValueMethod() {
        // given / when
        boolean hasMethod = Arrays.stream(ArrayPropertyBuilder.class.getDeclaredMethods())
            .anyMatch(m -> PropertyBuilderUtils.ADD_TO_DEFAULT_VALUE_METHOD.equals(m.getName()));

        // then
        assertThat(hasMethod, equalTo(true));
    }

    @Nested
    class JavadocExamples {

        public /*static*/ final Property<Integer[]> WEIGHTS = arrayBuilder(NumberType.INTEGER.arrayType())
            .path("calculation.weights")
            .defaultValue(3, 10, 2)
            .build();

        public /*static*/ final Property<String[]> WELCOME_TEXT =
            inlineArrayBuilder(InlineArrayPropertyType.STRING)
                .path("texts.welcome")
                .addToDefaultValue("Welcome!")
                .addToDefaultValue("Please read /rules")
                .addToDefaultValue("For help, see /help")
                .build();

        @Test
        void shouldHaveValidExampleForArrayProperty() {
            // given / when -> field init

            // then
            assertThat(WEIGHTS.getPath(), equalTo("calculation.weights"));
            assertThat(WEIGHTS.getDefaultValue(), arrayContaining(3, 10, 2));
        }

        @Test
        void shouldHaveValidExampleForInlineArrayProperty() {
            // given / when -> field init

            // then
            assertThat(WELCOME_TEXT.getPath(), equalTo("texts.welcome"));
            assertThat(WELCOME_TEXT.getDefaultValue(),
                arrayContaining("Welcome!", "Please read /rules", "For help, see /help"));
        }
    }
}
