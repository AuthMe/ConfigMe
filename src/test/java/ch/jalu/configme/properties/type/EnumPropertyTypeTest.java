package ch.jalu.configme.properties.type;

import ch.jalu.configme.properties.types.EnumPropertyType;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link EnumPropertyType}.
 */
public class EnumPropertyTypeTest {

    @Test
    public void shouldReturnUnknown() {
        // given
        EnumPropertyType<TimeUnit> propertyType = new EnumPropertyType<>(TimeUnit.class);

        // when
        TimeUnit result = propertyType.convert("unknown");

        // then
        assertThat(result, nullValue());
    }

    @Test
    public void shouldReturnNull() {
        // given
        EnumPropertyType<TimeUnit> propertyType = new EnumPropertyType<>(TimeUnit.class);

        // when
        TimeUnit result = propertyType.convert(new Object());

        // then
        assertThat(result, nullValue());
    }

    @Test
    public void shouldHandleNull() {
        // given
        EnumPropertyType<TimeUnit> propertyType = new EnumPropertyType<>(TimeUnit.class);

        // when
        TimeUnit result = propertyType.convert(null);

        // then
        assertThat(result, nullValue());
    }

    @Test
    public void shouldReturnHimself() {
        // given
        EnumPropertyType<TimeUnit> propertyType = new EnumPropertyType<>(TimeUnit.class);

        // when
        TimeUnit result = propertyType.convert(TimeUnit.SECONDS);

        // then
        assertThat(result, equalTo(TimeUnit.SECONDS));
    }

    @Test
    public void shouldReturnConvertedValue() {
        // given
        EnumPropertyType<TimeUnit> propertyType = new EnumPropertyType<>(TimeUnit.class);

        // when
        TimeUnit result = propertyType.convert("SECONDS");

        // then
        assertThat(result, equalTo(TimeUnit.SECONDS));
    }

    @Test
    public void shouldReturnValueAsExportValue() {
        // given
        EnumPropertyType<TimeUnit> propertyType = new EnumPropertyType<>(TimeUnit.class);

        // when
        Object result = propertyType.toExportValue(TimeUnit.DAYS);

        // then
        assertThat(result, equalTo("DAYS"));
    }

}
