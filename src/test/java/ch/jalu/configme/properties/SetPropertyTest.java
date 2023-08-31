package ch.jalu.configme.properties;

import ch.jalu.configme.properties.convertresult.PropertyValue;
import ch.jalu.configme.properties.types.NumberType;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link SetProperty}.
 */
class SetPropertyTest {

    @Test
    void shouldReturnValueFromSource() {
        // given
        SetProperty<Double> property = new SetProperty<>("error.codes", NumberType.DOUBLE,
            1.414, 1.732, 2.0);
        PropertyReader reader = mock(PropertyReader.class);
        List list = Arrays.asList(3.6, 6.9, 10.2);
        given(reader.getList("error.codes")).willReturn(list);

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
        given(reader.getList("error.codes")).willReturn(null);

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
}
