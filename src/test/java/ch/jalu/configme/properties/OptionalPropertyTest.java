package ch.jalu.configme.properties;

import ch.jalu.configme.properties.convertresult.PropertyValue;
import ch.jalu.configme.properties.types.BooleanType;
import ch.jalu.configme.properties.types.EnumPropertyType;
import ch.jalu.configme.properties.types.NumberType;
import ch.jalu.configme.properties.types.PropertyType;
import ch.jalu.configme.properties.types.StringType;
import ch.jalu.configme.resource.PropertyReader;
import ch.jalu.configme.samples.TestEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static ch.jalu.configme.TestUtils.isErrorValueOf;
import static ch.jalu.configme.TestUtils.isValidValueOf;
import static java.util.Optional.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link OptionalProperty}.
 */
@ExtendWith(MockitoExtension.class)
class OptionalPropertyTest {

    @Mock
    private PropertyReader reader;

    @Test
    void shouldReturnPresentValues() {
        // given
        OptionalProperty<Boolean> booleanProp = new OptionalProperty<>("bool.path.test", BooleanType.BOOLEAN);
        OptionalProperty<Integer> intProp = new OptionalProperty<>("int.path.test", NumberType.INTEGER);
        OptionalProperty<TestEnum> enumProp = new OptionalProperty<>("enum.path.test", new EnumPropertyType<>(TestEnum.class));

        given(reader.getObject("bool.path.test")).willReturn(true);
        given(reader.getObject("int.path.test")).willReturn(27);
        given(reader.getObject("enum.path.test")).willReturn(TestEnum.FOURTH.name());

        // when
        PropertyValue<Optional<Boolean>> boolResult = booleanProp.determineValue(reader);
        PropertyValue<Optional<Integer>> intResult = intProp.determineValue(reader);
        PropertyValue<Optional<TestEnum>> enumResult = enumProp.determineValue(reader);

        // then
        assertThat(boolResult, isValidValueOf(of(true)));
        assertThat(intResult, isValidValueOf(of(27)));
        assertThat(enumResult, isValidValueOf(of(TestEnum.FOURTH)));
    }

    @Test
    void shouldReturnEmptyOptional() {
        // given
        OptionalProperty<Boolean> booleanProp = new OptionalProperty<>("bool.path.wrong", BooleanType.BOOLEAN);
        OptionalProperty<Integer> intProp = new OptionalProperty<>("int.path.wrong", NumberType.INTEGER);
        OptionalProperty<TestEnum> enumProp = new OptionalProperty<>("enum.path.wrong", new EnumPropertyType<>(TestEnum.class));

        // when
        PropertyValue<Optional<Boolean>> boolResult = booleanProp.determineValue(reader);
        PropertyValue<Optional<Integer>> intResult = intProp.determineValue(reader);
        PropertyValue<Optional<TestEnum>> enumResult = enumProp.determineValue(reader);

        // then
        assertThat(boolResult, isValidValueOf(Optional.empty()));
        assertThat(intResult, isValidValueOf(Optional.empty()));
        assertThat(enumResult, isValidValueOf(Optional.empty()));
    }

    @Test
    void shouldAllowToDefineDefaultValue() {
        // given
        OptionalProperty<Integer> integerProp = new OptionalProperty<>("int.path.wrong", NumberType.INTEGER, 42);

        // when
        Optional<Integer> defaultValue = integerProp.getDefaultValue();

        // then
        assertThat(defaultValue, equalTo(Optional.of(42)));
    }

    @Test
    void shouldReturnValueWithInvalidFlagIfReturnedFromReader() {
        // given
        given(reader.getObject("the.path")).willReturn(400);
        OptionalProperty<Byte> optionalProperty = new OptionalProperty<>("the.path", NumberType.BYTE);

        // when
        PropertyValue<Optional<Byte>> value = optionalProperty.determineValue(reader);

        // then
        assertThat(value, isErrorValueOf(Optional.of(Byte.MAX_VALUE)));
    }

    @Test
    void shouldValidateWithBasePropertyNullSafe() {
        // given
        OptionalProperty<String> property = new OptionalProperty<>("path", StringType.STRING);

        // when
        boolean isEmptyValid = property.isValidValue(Optional.empty());
        boolean isValueValid = property.isValidValue(Optional.of("foo"));
        boolean isNullValid = property.isValidValue(null);

        // then
        assertThat(isEmptyValid, equalTo(true));
        assertThat(isValueValid, equalTo(true));
        assertThat(isNullValid, equalTo(false));
    }

    @Test
    void shouldReturnNullAsExportValue() {
        // given
        OptionalProperty<Integer> property = new OptionalProperty<>("int.path", NumberType.INTEGER);

        // when
        Object exportValue = property.toExportValue(Optional.empty());

        // then
        assertThat(exportValue, nullValue());
    }

    @Test
    void shouldReturnNullIfValuePropertyTypeReturnsNull() {
        // given
        PropertyType<String> valuePropertyType = mock(PropertyType.class);
        given(valuePropertyType.toExportValue("demo")).willReturn(null);
        OptionalProperty<String> optionalProperty = new OptionalProperty<>("int.path", valuePropertyType);

        // when
        Object exportValue = optionalProperty.toExportValue(Optional.of("demo"));

        // then
        assertThat(exportValue, nullValue());
    }

    @Test
    void shouldConstructExportValue() {
        // given
        OptionalProperty<TimeUnit> optionalProperty = new OptionalProperty<>("duration.unit", EnumPropertyType.of(TimeUnit.class));

        // when
        Object exportValue = optionalProperty.toExportValue(Optional.of(TimeUnit.HOURS));

        // then
        assertThat(exportValue, equalTo("HOURS"));
    }
}
