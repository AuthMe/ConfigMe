package ch.jalu.configme.properties.type;

import ch.jalu.configme.beanmapper.DefaultMapper;
import ch.jalu.configme.beanmapper.Mapper;
import ch.jalu.configme.demo.beans.Location;
import ch.jalu.configme.properties.types.BeanPropertyType;
import ch.jalu.configme.utils.TypeInformation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class BeanPropertyTypeTest {

    @Test
    public void shouldReturnConvertedValue() {
        // given
        TypeInformation typeInformation = new TypeInformation(Location.class);
        Mapper mapper = mock(Mapper.class);
        Location location = new Location();
        location.setLatitude(360F);
        location.setLongitude(123F);
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
        location.setLatitude(360F);
        location.setLongitude(123F);
        BeanPropertyType<Location> beanPropertyType = new BeanPropertyType<>(typeInformation, DefaultMapper.getInstance());

        // when
        Object result = beanPropertyType.toExportValue(location);

        // then
        assertThat(result, equalTo(location));
    }

}
