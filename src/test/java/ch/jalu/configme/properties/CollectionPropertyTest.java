package ch.jalu.configme.properties;

import ch.jalu.configme.properties.convertresult.PropertyValue;
import ch.jalu.configme.properties.types.CollectionPropertyType;
import ch.jalu.configme.properties.types.NumberType;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;
import java.util.stream.Collectors;

import static ch.jalu.configme.TestUtils.isErrorValueOf;
import static ch.jalu.configme.TestUtils.isValidValueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.BDDMockito.given;

/**
 * Test for {@link CollectionProperty}.
 */
@ExtendWith(MockitoExtension.class)
class CollectionPropertyTest {

    @Mock
    private PropertyReader reader;

    @Test
    void shouldUseGivenPropertyType() {
        // given
        CollectionPropertyType<Double, TreeSet<Double>> treeSetType =
            CollectionPropertyType.of(NumberType.DOUBLE, Collectors.toCollection(TreeSet::new));
        Property<TreeSet<Double>> sortedValuesProperty =
            new CollectionProperty<>("values", treeSetType, new TreeSet<>());
        given(reader.getObject("values")).willReturn(Arrays.asList(16, 9, 4));

        // when
        PropertyValue<TreeSet<Double>> value = sortedValuesProperty.determineValue(reader);

        // then
        assertThat(value.isValidInResource(), equalTo(true));
        assertThat(value.getValue(), instanceOf(TreeSet.class));
        assertThat(value.getValue(), contains(4.0, 9.0, 16.0));
    }

    @Test
    void shouldCreatePropertyWithEntryTypeAndCollector() {
        // given
        Property<Vector<Double>> valuesProperty = CollectionProperty.of(
            "values",
            NumberType.DOUBLE,
            Collectors.toCollection(Vector::new),
            new Vector<>(Arrays.asList(3.0, 4.0)));
        given(reader.getObject("values")).willReturn(Arrays.asList(1.0, 2.5, 4.0));

        // when
        PropertyValue<Vector<Double>> value = valuesProperty.determineValue(reader);

        // then
        assertThat(valuesProperty.getPath(), equalTo("values"));
        assertThat(valuesProperty.getDefaultValue(), instanceOf(Vector.class));
        assertThat(valuesProperty.getDefaultValue(), contains(3.0, 4.0));

        assertThat(value.isValidInResource(), equalTo(true));
        assertThat(value.getValue(), instanceOf(Vector.class));
        assertThat(value.getValue(), contains(1.0, 2.5, 4.0));
    }

    @Test
    void shouldCreatePropertyWithEntryTypeAndCollectorAndVarargsDefaultValue() {
        // given
        Property<List<Long>> property1 = CollectionProperty.of("p1", NumberType.LONG, Collectors.toList());
        Property<List<Long>> property2 = CollectionProperty.of("p2", NumberType.LONG, Collectors.toList(), 4L, 6L);
        given(reader.getObject("p1")).willReturn("invalid");
        given(reader.getObject("p2")).willReturn(Arrays.asList("1", "2"));

        // when
        PropertyValue<List<Long>> value1 = property1.determineValue(reader);
        PropertyValue<List<Long>> value2 = property2.determineValue(reader);

        // then
        assertThat(property1.getDefaultValue(), empty());
        assertThat(property2.getDefaultValue(), contains(4L, 6L));
        assertThat(value1, isErrorValueOf(Collections.emptyList())); // default value
        assertThat(value2, isValidValueOf(Arrays.asList(1L, 2L)));
    }
}
