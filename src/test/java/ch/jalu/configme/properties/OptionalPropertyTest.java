package ch.jalu.configme.properties;

import ch.jalu.configme.configurationdata.PropertyValue;
import ch.jalu.configme.resource.PropertyReader;
import ch.jalu.configme.samples.TestEnum;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static ch.jalu.configme.TestUtils.isErrorValueOf;
import static ch.jalu.configme.TestUtils.isValidValueOf;
import static java.util.Optional.of;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for {@link OptionalProperty}.
 */
public class OptionalPropertyTest {

    private PropertyReader reader;

    @Before
    public void setUpResource() {
        reader = mock(PropertyReader.class);
        when(reader.getObject("bool.path.test")).thenReturn(true);
        when(reader.getObject("bool.path.wrong")).thenReturn(null);
        when(reader.getObject("int.path.test")).thenReturn(27);
        when(reader.getObject("int.path.wrong")).thenReturn(null);
        when(reader.getObject("enum.path.test")).thenReturn(TestEnum.FOURTH.name());
        when(reader.getObject("enum.path.wrong")).thenReturn(null);
    }

    @Test
    public void shouldReturnPresentValues() {
        // given
        OptionalProperty<Boolean> booleanProp = new OptionalProperty<>(new BooleanProperty("bool.path.test", false));
        OptionalProperty<Integer> intProp = new OptionalProperty<>(new IntegerProperty("int.path.test", 0));
        OptionalProperty<TestEnum> enumProp = new OptionalProperty<>(new EnumProperty<>(TestEnum.class, "enum.path.test", TestEnum.SECOND));

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
    public void shouldReturnEmptyOptional() {
        // given
        OptionalProperty<Boolean> booleanProp = new OptionalProperty<>(new BooleanProperty("bool.path.wrong", false));
        OptionalProperty<Integer> intProp = new OptionalProperty<>(new IntegerProperty("int.path.wrong", 0));
        OptionalProperty<TestEnum> enumProp = new OptionalProperty<>(new EnumProperty<>(TestEnum.class, "enum.path.wrong", TestEnum.SECOND));

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
    public void shouldAllowToDefineDefaultValue() {
        // given
        OptionalProperty<Integer> integerProp = new OptionalProperty<>(new IntegerProperty("path", 0), 42);

        // when
        Optional<Integer> defaultValue = integerProp.getDefaultValue();

        // then
        assertThat(defaultValue, equalTo(Optional.of(42)));
    }

    @Test
    public void shouldReturnValueWithInvalidFlagIfReturnedFromReader() {
        // given
        StringProperty baseProperty = spy(new StringProperty("the.path", "DEFAULT"));
        doReturn(PropertyValue.withValueRequiringRewrite("this should be discarded")).when(baseProperty).determineValue(reader);
        given(reader.contains("the.path")).willReturn(true);
        OptionalProperty<String> optionalProperty = new OptionalProperty<>(baseProperty);

        // when
        PropertyValue<Optional<String>> value = optionalProperty.determineValue(reader);

        // then
        assertThat(value, isErrorValueOf(Optional.empty()));
    }

    @Test
    public void shouldDelegateToBasePropertyAndHaveEmptyOptionalAsDefault() {
        // given
        StringProperty baseProperty = new StringProperty("some.path", "Def");
        OptionalProperty<String> property = new OptionalProperty<>(baseProperty);

        // when
        Optional<String> defaultValue = property.getDefaultValue();
        String path = property.getPath();

        // then
        assertThat(defaultValue, equalTo(Optional.empty()));
        assertThat(path, equalTo("some.path"));
    }

    @Test
    public void shouldValidateWithBasePropertyNullSafe() {
        // given
        StringProperty baseProperty = spy(new StringProperty("some.path", "Def"));
        OptionalProperty<String> property = new OptionalProperty<>(baseProperty);

        // when
        boolean isEmptyValid = property.isValidValue(Optional.empty());
        boolean isValueValid = property.isValidValue(Optional.of("foo"));
        boolean isNullValid = property.isValidValue(null);

        // then
        assertThat(isEmptyValid, equalTo(true));
        assertThat(isValueValid, equalTo(true));
        assertThat(isNullValid, equalTo(false));
        verify(baseProperty, only()).isValidValue("foo");
    }
}
