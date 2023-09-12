package ch.jalu.configme.properties.types;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.IntFunction;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Test for {@link ArrayPropertyType}.
 */
class ArrayPropertyTypeTest {

    @Test
    void shouldConvertProperly() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();
        ArrayPropertyType<Float> type = new ArrayPropertyType<>(NumberType.FLOAT, Float[]::new);
        Object value = Arrays.asList(3, "4.5");

        // when
        Float[] result = type.convert(value, errorRecorder);

        // then
        assertThat(result, arrayContaining(3f, 4.5f));
        assertThat(errorRecorder.isFullyValid(), equalTo(true));
    }

    @Test
    void shouldConvertAndIgnoreInvalidEntries() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();
        ArrayPropertyType<TimeUnit> type = new ArrayPropertyType<>(EnumPropertyType.of(TimeUnit.class), TimeUnit[]::new);
        Set<Object> value = new HashSet<>();
        value.add(true);
        value.add("HOURS");
        value.add(1334024888);

        // when
        TimeUnit[] result = type.convert(value, errorRecorder);

        // then
        assertThat(result, arrayContaining(TimeUnit.HOURS));
        assertThat(errorRecorder.isFullyValid(), equalTo(false));
    }

    @Test
    void shouldReturnExportValue() {
        // given
        ArrayPropertyType<Boolean> type = new ArrayPropertyType<>(BooleanType.BOOLEAN, Boolean[]::new);
        Boolean[] values = new Boolean[]{false, false, true};

        // when
        List<?> exportValue = type.toExportValue(values);

        // then
        assertThat(exportValue, contains(false, false, true));
    }

    @Test
    void shouldReturnNullForInvalidObjectToConvertFrom() {
        // given
        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);
        ArrayPropertyType<Float> type = new ArrayPropertyType<>(NumberType.FLOAT, Float[]::new);
        Object value = new char[]{ 'a', 'f' };

        // when
        Float[] result = type.convert(value, errorRecorder);

        // then
        assertThat(result, nullValue());
        verifyNoInteractions(errorRecorder);
    }

    @Test
    void shouldReturnEntryType() {
        // given
        ArrayPropertyType<String> type = new ArrayPropertyType<>(StringType.STRING_LOWER_CASE, String[]::new);

        // when
        PropertyType<String> entryType = type.getEntryType();

        // then
        assertThat(entryType, sameInstance(StringType.STRING_LOWER_CASE));
    }

    @Test
    void shouldReturnArrayProducer() {
        // given
        IntFunction<String[]> arrayProducer = String[]::new;
        ArrayPropertyType<String> type = new ArrayPropertyType<>(StringType.STRING, arrayProducer);

        // when
        IntFunction<String[]> result = type.getArrayProducer();

        // then
        assertThat(result, sameInstance(arrayProducer));
    }
}
