package ch.jalu.configme.properties;

import ch.jalu.configme.properties.convertresult.PropertyValue;
import ch.jalu.configme.properties.types.NumberType;
import ch.jalu.configme.properties.types.SetPropertyType;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.singleton;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link SetProperty}.
 */
@ExtendWith(MockitoExtension.class)
class SetPropertyTest {

    @Test
    void shouldReturnValueFromSource() {
        // given
        SetProperty<Double> property = new SetProperty<>("error.codes", NumberType.DOUBLE,
            1.414, 1.732, 2.0);
        PropertyReader reader = mock(PropertyReader.class);
        List<Double> list = Arrays.asList(3.6, 6.9, 10.2);
        given(reader.getObject("error.codes")).willReturn(list);

        // when
        PropertyValue<Set<Double>> result = property.determineValue(reader);

        // then
        assertThat(result.isValidInResource(), equalTo(true));
        assertThat(result.getValue(), contains(3.6, 6.9, 10.2));
    }

    @Test
    void shouldReturnDefaultValue() {
        // given
        SetProperty<Integer> property = new SetProperty<>("error.codes", NumberType.INTEGER,
            -27, -8);
        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getObject("error.codes")).willReturn(null);

        // when
        PropertyValue<Set<Integer>> result = property.determineValue(reader);

        // then
        assertThat(result.isValidInResource(), equalTo(false));
        assertThat(result.getValue(), contains(-27, -8)); // default value
    }

    @Test
    void shouldCreateExportValue() {
        // given
        SetProperty<Double> property = new SetProperty<>("error.codes", NumberType.DOUBLE,
            1.414, 1.732, 2.0);
        Set<Double> value = new LinkedHashSet<>(Arrays.asList(2.14, 3.28, 5.56));

        // when
        Object exportValue = property.toExportValue(value);

        // then
        assertThat(exportValue, equalTo(Arrays.asList(2.14, 3.28, 5.56)));
    }

    @Test
    void shouldHaveUnmodifiableDefaultValue() {
        // given
        SetPropertyType<BigDecimal> setPropertyType = new SetPropertyType<>(NumberType.BIG_DECIMAL);

        // when
        SetProperty<BigDecimal> property1 = new SetProperty<>("path", NumberType.BIG_DECIMAL, BigDecimal.TEN);
        SetProperty<BigDecimal> property2 = new SetProperty<>("path", NumberType.BIG_DECIMAL, singleton(BigDecimal.TEN));
        SetProperty<BigDecimal> property3 = new SetProperty<>("path", setPropertyType, singleton(BigDecimal.TEN));

        // then
        Stream.of(property1, property2, property3).forEach(property -> {
            assertThat(property.getDefaultValue(), contains(BigDecimal.TEN));
            assertThat(property.getDefaultValue().getClass().getName(), equalTo("java.util.Collections$UnmodifiableSet"));
        });
    }
}
