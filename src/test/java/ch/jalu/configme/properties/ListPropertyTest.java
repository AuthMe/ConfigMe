package ch.jalu.configme.properties;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.properties.convertresult.PropertyValue;
import ch.jalu.configme.properties.types.ListPropertyType;
import ch.jalu.configme.properties.types.NumberType;
import ch.jalu.configme.properties.types.PropertyType;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static ch.jalu.configme.TestUtils.isErrorValueOf;
import static ch.jalu.configme.TestUtils.isValidValueOf;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link ListProperty}.
 */
@ExtendWith(MockitoExtension.class)
class ListPropertyTest {

    @Mock
    private PropertyReader reader;

    @Test
    void shouldReturnValueFromResource() {
        // given
        Property<List<Integer>> property = new ListProperty<>("list", NumberType.INTEGER);
        given(reader.getObject("list")).willReturn(Arrays.asList(3, 5, 7.0));

        // when
        PropertyValue<List<Integer>> result = property.determineValue(reader);

        // then
        assertThat(result, isValidValueOf(Arrays.asList(3, 5, 7)));
    }

    @Test
    void shouldReturnDefaultValue() {
        // given
        Property<List<Integer>> property = new ListProperty<>("list", NumberType.INTEGER, 8, 9, 10);
        given(reader.getObject("list")).willReturn(null);

        // when
        PropertyValue<List<Integer>> result = property.determineValue(reader);

        // then
        assertThat(result, isErrorValueOf(Arrays.asList(8, 9, 10)));
    }

    @Test
    void shouldReturnValueAsExportValue() {
        // given
        Property<List<Integer>> property = new ListProperty<>("list", NumberType.INTEGER, Arrays.asList(-2, 16));

        // when
        Object result = property.toExportValue(Arrays.asList(128, -256, 512));

        // then
        assertThat(result, equalTo(Arrays.asList(128, -256, 512)));
    }

    @Test
    void shouldHaveUnmodifiableDefaultValue() {
        // given
        PropertyType<List<BigDecimal>> listPropertyType = new ListPropertyType<>(NumberType.BIG_DECIMAL);

        // when
        ListProperty<BigDecimal> property1 = new ListProperty<>("path", NumberType.BIG_DECIMAL, BigDecimal.TEN);
        ListProperty<BigDecimal> property2 = new ListProperty<>("path", NumberType.BIG_DECIMAL, singletonList(BigDecimal.TEN));
        ListProperty<BigDecimal> property3 = ListProperty.withListType("path", listPropertyType, singletonList(BigDecimal.TEN));
        ListProperty<BigDecimal> property4 = ListProperty.withListType("path", listPropertyType, BigDecimal.TEN);

        // then
        Stream.of(property1, property2, property3, property4).forEach(property -> {
            assertThat(property.getDefaultValue(), contains(BigDecimal.TEN));
            assertThat(property.getDefaultValue().getClass().getName(), equalTo("java.util.Collections$UnmodifiableRandomAccessList"));
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldCreatePropertyWithCustomListType() {
        // given
        String path = "duration.units";
        PropertyType<List<TimeUnit>> timeUnitListType = mock(PropertyType.class);

        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();
        String value = "s,h";
        given(timeUnitListType.convert(value, errorRecorder)).willReturn(Arrays.asList(TimeUnit.SECONDS, TimeUnit.HOURS));
        given(reader.getObject(path)).willReturn(value);

        // when
        ListProperty<TimeUnit> property = ListProperty.withListType(path, timeUnitListType, singletonList(TimeUnit.DAYS));

        // then
        assertThat(property.getPath(), equalTo(path));
        assertThat(property.getDefaultValue(), contains(TimeUnit.DAYS));
        assertThat(property.getFromReader(reader, errorRecorder), contains(TimeUnit.SECONDS, TimeUnit.HOURS));
    }
}
