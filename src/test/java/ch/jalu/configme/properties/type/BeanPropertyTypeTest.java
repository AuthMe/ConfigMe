package ch.jalu.configme.properties.type;

import ch.jalu.configme.beanmapper.DefaultMapper;
import ch.jalu.configme.beanmapper.Mapper;
import ch.jalu.configme.beanmapper.command.Command;
import ch.jalu.configme.demo.beans.CoordinateSystem;
import ch.jalu.configme.demo.beans.Location;
import ch.jalu.configme.properties.types.BeanPropertyType;
import ch.jalu.configme.utils.TypeInformation;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link BeanPropertyType}.
 */
public class BeanPropertyTypeTest {

    @Test
    public void shouldReturnConvertedValue() {
        // given
        TypeInformation typeInformation = new TypeInformation(Location.class);
        Mapper mapper = mock(Mapper.class);
        Location location = new Location();
        location.setLatitude(360f);
        location.setLongitude(123f);
        BeanPropertyType<Location> beanPropertyType = new BeanPropertyType<>(typeInformation, mapper);
        given(mapper.convertToBean("test_bean", typeInformation)).willReturn(location);

        // when
        Object result = beanPropertyType.convert("test_bean");

        // then
        assertThat(result, equalTo(location));
    }

    @Test
    public void shouldReturnValueAsExportValue() {
        // given
        TypeInformation typeInformation = new TypeInformation(Location.class);
        Location location = new Location();
        location.setLatitude(360f);
        location.setLongitude(123f);
        location.setCoordinateType(CoordinateSystem.NAD);
        BeanPropertyType<Location> beanPropertyType = new BeanPropertyType<>(typeInformation, DefaultMapper.getInstance());

        // when
        Object result = beanPropertyType.toExportValue(location);

        // then
        assertThat(result, instanceOf(Map.class));
        Map<String, Object> map = (Map) result;
        assertThat(map.keySet(), containsInAnyOrder("latitude", "longitude", "coordinateType"));
        assertThat(map.get("latitude"), equalTo(360f));
        assertThat(map.get("longitude"), equalTo(123f));
        assertThat(map.get("coordinateType"), equalTo("NAD"));
    }

    @Test
    public void shouldInitializeWithDefaultMapper() throws NoSuchFieldException, IllegalAccessException {
        // given / when
        BeanPropertyType<Command> type = BeanPropertyType.of(Command.class);

        // then
        Field field = BeanPropertyType.class.getDeclaredField("mapper");
        field.setAccessible(true);
        Object mapperValue = field.get(type);
        assertThat(mapperValue, sameInstance(DefaultMapper.getInstance()));
    }
}
