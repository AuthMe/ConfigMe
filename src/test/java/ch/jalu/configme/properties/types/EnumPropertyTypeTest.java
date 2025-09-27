package ch.jalu.configme.properties.types;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorderImpl;
import org.junit.jupiter.api.Test;

import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

/**
 * Test for {@link EnumPropertyType}.
 */
class EnumPropertyTypeTest {

    @Test
    void shouldReturnUnknown() {
        // given
        EnumPropertyType<TimeUnit> propertyType = new EnumPropertyType<>(TimeUnit.class);

        // when
        TimeUnit result = propertyType.convert("unknown", new ConvertErrorRecorderImpl());

        // then
        assertThat(result, nullValue());
    }

    @Test
    void shouldReturnNull() {
        // given
        EnumPropertyType<TimeUnit> propertyType = new EnumPropertyType<>(TimeUnit.class);

        // when
        TimeUnit result = propertyType.convert(new Object(), new ConvertErrorRecorderImpl());

        // then
        assertThat(result, nullValue());
    }

    @Test
    void shouldHandleNull() {
        // given
        EnumPropertyType<TimeUnit> propertyType = new EnumPropertyType<>(TimeUnit.class);

        // when
        TimeUnit result = propertyType.convert(null, new ConvertErrorRecorderImpl());

        // then
        assertThat(result, nullValue());
    }

    @Test
    void shouldReturnHimself() {
        // given
        EnumPropertyType<TimeUnit> propertyType = new EnumPropertyType<>(TimeUnit.class);

        // when
        TimeUnit result = propertyType.convert(TimeUnit.SECONDS, new ConvertErrorRecorderImpl());

        // then
        assertThat(result, equalTo(TimeUnit.SECONDS));
    }

    @Test
    void shouldReturnConvertedValue() {
        // given
        EnumPropertyType<TimeUnit> propertyType = new EnumPropertyType<>(TimeUnit.class);

        // when
        TimeUnit result = propertyType.convert("SECONDS", new ConvertErrorRecorderImpl());

        // then
        assertThat(result, equalTo(TimeUnit.SECONDS));
    }

    @Test
    void shouldReturnValueAsExportValue() {
        // given
        EnumPropertyType<TimeUnit> propertyType = new EnumPropertyType<>(TimeUnit.class);

        // when
        Object result = propertyType.toExportValue(TimeUnit.DAYS);

        // then
        assertThat(result, equalTo("DAYS"));
    }

    @Test
    void shouldReturnEnumClass() {
        // given
        EnumPropertyType<StandardOpenOption> propertyType = new EnumPropertyType<>(StandardOpenOption.class);

        // when / then
        assertThat(propertyType.getEnumClass(), equalTo(StandardOpenOption.class));
    }

    @Test
    void shouldCreateArrayType() {
        // given / when
        ArrayPropertyType<StandardOpenOption> arrayType = EnumPropertyType.of(StandardOpenOption.class).arrayType();

        // then
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorderImpl();
        assertThat(arrayType.convert(Arrays.asList("READ", "CREATE"), errorRecorder),
            arrayContaining(StandardOpenOption.READ, StandardOpenOption.CREATE));
    }

    @Test
    void shouldCreateInlineArrayType() {
        // given / when
        InlineArrayPropertyType<StandardOpenOption> inlineArrayType =
            EnumPropertyType.of(StandardOpenOption.class).inlineArrayType(";;");

        // then
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorderImpl();
        assertThat(inlineArrayType.convert("READ;;CREATE;;", errorRecorder),
            arrayContaining(StandardOpenOption.READ, StandardOpenOption.CREATE));
    }
}
