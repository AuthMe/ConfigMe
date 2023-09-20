package ch.jalu.configme.properties.types;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.samples.TestEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Test for {@link EnumSetPropertyType}.
 */
@ExtendWith(MockitoExtension.class)
class EnumSetPropertyTypeTest {

    @Test
    void shouldConvertProperly() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();
        EnumSetPropertyType<TimeUnit> type = new EnumSetPropertyType<>(TimeUnit.class);
        Object value = Arrays.asList("HOURS", TimeUnit.SECONDS);

        // when
        EnumSet<TimeUnit> result = type.convert(value, errorRecorder);

        // then
        assertThat(result, contains(TimeUnit.SECONDS, TimeUnit.HOURS));
        assertThat(errorRecorder.isFullyValid(), equalTo(true));
    }

    @Test
    void shouldConvertAndIgnoreInvalidEntries() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();
        EnumSetPropertyType<TimeUnit> type = new EnumSetPropertyType<>(EnumPropertyType.of(TimeUnit.class));
        Set<Object> value = new HashSet<>();
        value.add(true);
        value.add("HOURS");
        value.add(1334024888);

        // when
        EnumSet<TimeUnit> result = type.convert(value, errorRecorder);

        // then
        assertThat(result, contains(TimeUnit.HOURS));
        assertThat(errorRecorder.isFullyValid(), equalTo(false));
    }

    @Test
    void shouldReturnExportValue() {
        // given
        EnumSetPropertyType<TestEnum> type = new EnumSetPropertyType<>(TestEnum.class);
        EnumSet<TestEnum> values = EnumSet.noneOf(TestEnum.class);
        values.add(TestEnum.SECOND);
        values.add(TestEnum.THIRD);

        // when
        List<?> exportValue = type.toExportValue(values);

        // then
        assertThat(exportValue, contains("SECOND", "THIRD"));
    }

    @Test
    void shouldReturnNullForInvalidObjectToConvertFrom() {
        // given
        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);
        EnumSetPropertyType<TestEnum> type = new EnumSetPropertyType<>(EnumPropertyType.of(TestEnum.class));
        Object value = 1.41421;

        // when
        EnumSet<TestEnum> result = type.convert(value, errorRecorder);

        // then
        assertThat(result, nullValue());
        verifyNoInteractions(errorRecorder);
    }

    @Test
    void shouldReturnEntryType() {
        // given
        EnumPropertyType<TestEnum> enumType = EnumPropertyType.of(TestEnum.class);
        EnumSetPropertyType<TestEnum> type = new EnumSetPropertyType<>(enumType);

        // when
        EnumPropertyType<TestEnum> entryType = type.getEntryType();
        Class<TestEnum> enumClass = type.getEnumClass();

        // then
        assertThat(entryType, equalTo(enumType));
        assertThat(enumClass, equalTo(TestEnum.class));
    }
}
