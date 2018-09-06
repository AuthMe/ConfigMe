package ch.jalu.configme.properties.type;

import ch.jalu.configme.properties.types.EnumPropertyType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class EnumPropertyTypeTest {

    @Test
    public void shouldReturnConvertedValue() {
        EnumPropertyType<TimeUnit> propertyType = new EnumPropertyType<>(TimeUnit.class);

        assertThat(propertyType.convert("SECONDS"), equalTo(TimeUnit.SECONDS));
    }

    @Test
    public void shouldReturnValueAsExportValue() {
        EnumPropertyType<TimeUnit> propertyType = new EnumPropertyType<>(TimeUnit.class);

        assertThat(propertyType.toExportValue(TimeUnit.DAYS), equalTo("DAYS"));
    }

}
