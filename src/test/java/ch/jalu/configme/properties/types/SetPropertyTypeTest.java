package ch.jalu.configme.properties.types;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorderImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
 * Test for {@link SetPropertyType}.
 */
@ExtendWith(MockitoExtension.class)
class SetPropertyTypeTest {

    @Test
    void shouldConvertProperly() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorderImpl();
        SetPropertyType<Float> type = new SetPropertyType<>(NumberType.FLOAT);
        Object value = Arrays.asList(3, "4.5");

        // when
        Set<Float> result = type.convert(value, errorRecorder);

        // then
        assertThat(result, instanceOf(LinkedHashSet.class));
        assertThat(result, contains(3f, 4.5f));
        assertThat(errorRecorder.isFullyValid(), equalTo(true));
    }

    @Test
    void shouldConvertAndIgnoreInvalidEntries() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorderImpl();
        SetPropertyType<TimeUnit> type = new SetPropertyType<>(EnumPropertyType.of(TimeUnit.class));
        Set<Object> value = new HashSet<>();
        value.add(true);
        value.add("HOURS");
        value.add(1334024888);

        // when
        Set<TimeUnit> result = type.convert(value, errorRecorder);

        // then
        assertThat(result, instanceOf(LinkedHashSet.class));
        assertThat(result, contains(TimeUnit.HOURS));
        assertThat(errorRecorder.isFullyValid(), equalTo(false));
    }

    @Test
    void shouldReturnExportValue() {
        // given
        SetPropertyType<String> type = new SetPropertyType<>(StringType.STRING);
        Set<String> values = new LinkedHashSet<>(Arrays.asList("f", "r", "o", "g"));

        // when
        List<?> exportValue = type.toExportValue(values);

        // then
        assertThat(exportValue, contains("f", "r", "o", "g")); // ribbit
    }

    @Test
    void shouldReturnNullForInvalidObjectToConvertFrom() {
        // given
        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);
        SetPropertyType<Float> type = new SetPropertyType<>(NumberType.FLOAT);
        Object value = TimeUnit.MILLISECONDS;

        // when
        Set<Float> result = type.convert(value, errorRecorder);

        // then
        assertThat(result, nullValue());
        verifyNoInteractions(errorRecorder);
    }
}
