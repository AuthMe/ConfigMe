package ch.jalu.configme.properties.types;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorderImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Test for {@link ListPropertyType}.
 */
@ExtendWith(MockitoExtension.class)
class ListPropertyTypeTest {

    @Test
    void shouldConvertProperly() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorderImpl();
        ListPropertyType<Float> type = new ListPropertyType<>(NumberType.FLOAT);
        Object value = Arrays.asList(3, "4.5");

        // when
        List<Float> result = type.convert(value, errorRecorder);

        // then
        assertThat(result, instanceOf(ArrayList.class));
        assertThat(result, contains(3f, 4.5f));
        assertThat(errorRecorder.isFullyValid(), equalTo(true));
    }

    @Test
    void shouldConvertAndIgnoreInvalidEntries() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorderImpl();
        ListPropertyType<TimeUnit> type = new ListPropertyType<>(EnumPropertyType.of(TimeUnit.class));
        Set<Object> value = new HashSet<>();
        value.add(true);
        value.add("HOURS");
        value.add(1334024888);

        // when
        List<TimeUnit> result = type.convert(value, errorRecorder);

        // then
        assertThat(result, instanceOf(ArrayList.class));
        assertThat(result, contains(TimeUnit.HOURS));
        assertThat(errorRecorder.isFullyValid(), equalTo(false));
    }

    @Test
    void shouldReturnExportValue() {
        // given
        ListPropertyType<Boolean> type = new ListPropertyType<>(BooleanType.BOOLEAN);
        List<Boolean> values = Arrays.asList(false, false, true);

        // when
        List<?> exportValue = type.toExportValue(values);

        // then
        assertThat(exportValue, contains(false, false, true));
    }

    @Test
    void shouldReturnNullForInvalidObjectToConvertFrom() {
        // given
        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);
        ListPropertyType<Float> type = new ListPropertyType<>(NumberType.FLOAT);
        Object value = new char[]{ 'a', 'f' };

        // when
        List<Float> result = type.convert(value, errorRecorder);

        // then
        assertThat(result, nullValue());
        verifyNoInteractions(errorRecorder);
    }
}
