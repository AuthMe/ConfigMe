package ch.jalu.configme.properties.types;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Test for {@link CollectionPropertyType}.
 */
@ExtendWith(MockitoExtension.class)
class CollectionPropertyTypeTest {

    @Test
    void shouldConvertProperly() {
        // given
        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);
        CollectionPropertyType<Integer, Vector<Integer>> type = newVectorType(NumberType.INTEGER);
        List<String> value = Arrays.asList("3", "14", "15");

        // when
        Vector<Integer> result = type.convert(value, errorRecorder);

        // then
        assertThat(result, contains(3, 14, 15));
        verifyNoInteractions(errorRecorder);
    }

    @Test
    void shouldConvertAndIgnoreInvalidValues() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();
        CollectionPropertyType<Long, Vector<Long>> type = newVectorType(NumberType.LONG);
        List<String> value = Arrays.asList("3", "a", "15,77");

        // when
        Vector<Long> result = type.convert(value, errorRecorder);

        // then
        assertThat(result, contains(3L));
        assertThat(errorRecorder.isFullyValid(), equalTo(false));
    }

    @Test
    void shouldReturnExportValue() {
        // given
        CollectionPropertyType<TimeUnit, Vector<TimeUnit>> type = newVectorType(EnumPropertyType.of(TimeUnit.class));
        Vector<TimeUnit> value = new Vector<>(Arrays.asList(TimeUnit.SECONDS, TimeUnit.HOURS, TimeUnit.DAYS));

        // when
        List<?> exportValue = type.toExportValue(value);

        // then
        assertThat(exportValue, contains("SECONDS", "HOURS", "DAYS"));
    }

    @Test
    void shouldReturnEntryPropertyType() {
        // given
        CollectionPropertyType<String, Vector<String>> type = newVectorType(StringType.STRING_LOWER_CASE);

        // when
        PropertyType<String> entryType = type.getEntryType();

        // then
        assertThat(entryType, sameInstance(StringType.STRING_LOWER_CASE));
    }

    private static <E> CollectionPropertyType<E, Vector<E>> newVectorType(PropertyType<E> propertyType) {
        return CollectionPropertyType.of(propertyType,
            Collectors.toCollection(Vector::new));
    }
}
