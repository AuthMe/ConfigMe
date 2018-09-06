package ch.jalu.configme.properties.type;

import ch.jalu.configme.properties.types.PrimitivePropertyType;
import ch.jalu.configme.properties.types.PropertyType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class PrimitivePropertyTypeTest {

    @Test
    public void shouldReturnConvertedValue() {
        PropertyType<Double> propertyType = new PrimitivePropertyType<>(
            Double.class,
            object -> object instanceof Number ? ((Number) object).doubleValue() : null
        );

        assertThat(propertyType.convert(new Double(3.1423)), equalTo(3.1423));
    }

    @Test
    public void shouldReturnValueAsExportValue() {
        PropertyType<Double> propertyType = new PrimitivePropertyType<>(
            Double.class,
            object -> object instanceof Number ? ((Number) object).doubleValue() : null
        );

        assertThat(propertyType.toExportValue(new Double(123.99923)), equalTo(123.99923));
    }

}
