package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.PrimitivePropertyType;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Test for {@link ListProperty}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ListPropertyTest {

    @Mock
    private PropertyReader reader;

    @Test
    public void shouldReturnValueFromResource() {
        // given
        Property<List<Integer>> property = new ListProperty<>("list", PrimitivePropertyType.INTEGER);
        given(reader.getList("list")).willReturn((List) Arrays.asList(3, 5, 7.0));

        // when
        List<Integer> result = property.determineValue(reader);

        // then
        assertThat(result, contains(3, 5, 7));
    }

    @Test
    public void shouldReturnDefaultValue() {
        // given
        Property<List<Integer>> property = new ListProperty<>("list", PrimitivePropertyType.INTEGER, 8, 9, 10);
        given(reader.getList("list")).willReturn(null);

        // when
        List<Integer> result = property.determineValue(reader);

        // then
        assertThat(result, contains(8, 9, 10));
    }

    @Test
    public void shouldReturnValueAsExportValue() {
        // given
        Property<List<Integer>> property = new ListProperty<>("list", PrimitivePropertyType.INTEGER, Arrays.asList(-2, 16));

        // when
        Object result = property.toExportValue(Arrays.asList(128, -256, 512));

        // then
        assertThat(result, equalTo(Arrays.asList(128, -256, 512)));
    }
}
