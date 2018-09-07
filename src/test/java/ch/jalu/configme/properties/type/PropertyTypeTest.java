package ch.jalu.configme.properties.type;

import ch.jalu.configme.beanmapper.DefaultMapper;
import ch.jalu.configme.properties.types.BeanPropertyType;
import ch.jalu.configme.properties.types.EnumPropertyType;
import ch.jalu.configme.properties.types.PrimitivePropertyType;
import ch.jalu.configme.properties.types.PropertyType;
import ch.jalu.configme.utils.TypeInformation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

// Pointless tests, because we know, that return all methods in PropertyType class.
// But we want to up coverage percent c:
@RunWith(MockitoJUnitRunner.class)
public class PropertyTypeTest {

    @Test
    public void shouldReturnBeanType() {
        assertThat(
            PropertyType.beanType(Object.class),
            equalTo(new BeanPropertyType<>(new TypeInformation(Object.class), DefaultMapper.getInstance()))
        );
    }

    @Test
    public void shouldReturnBeanTypeWithMapper() {
        assertThat(
            PropertyType.beanType(Object.class, DefaultMapper.getInstance()),
            equalTo(new BeanPropertyType<>(new TypeInformation(Object.class), DefaultMapper.getInstance()))
        );
    }

    @Test
    public void shouldReturnEnumType() {
        assertThat(
            PropertyType.enumType(TimeUnit.class),
            equalTo(new EnumPropertyType<>(TimeUnit.class))
        );
    }

    @Test
    public void shouldReturnLowercaseStringType() {
        assertThat(
            PropertyType.lowerCaseStringType(),
            equalTo(PrimitivePropertyType.LOWERCASE_STRING)
        );
    }

    @Test
    public void shouldReturnBooleanType() {
        assertThat(
            PropertyType.booleanType(),
            equalTo(PrimitivePropertyType.BOOLEAN)
        );
    }

    @Test
    public void shouldReturnDoubleType() {
        assertThat(
            PropertyType.doubleType(),
            equalTo(PrimitivePropertyType.DOUBLE)
        );
    }

    @Test
    public void shouldReturnFloatType() {
        assertThat(
            PropertyType.floatType(),
            equalTo(PrimitivePropertyType.FLOAT)
        );
    }

    @Test
    public void shouldReturnLongType() {
        assertThat(
            PropertyType.longType(),
            equalTo(PrimitivePropertyType.LONG)
        );
    }

    @Test
    public void shouldReturnIntegerType() {
        assertThat(
            PropertyType.integerType(),
            equalTo(PrimitivePropertyType.INTEGER)
        );
    }

    @Test
    public void shouldReturnShortType() {
        assertThat(
            PropertyType.shortType(),
            equalTo(PrimitivePropertyType.SHORT)
        );
    }

    @Test
    public void shouldReturnByteType() {
        assertThat(
            PropertyType.byteType(),
            equalTo(PrimitivePropertyType.BYTE)
        );
    }

}
