package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.PropertyType;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class MapPropertyTest {

    @Mock
    private PropertyReader reader;

    @Test
    public void shouldReturnValueFromResource() {
        MapProperty<String> property = new MapProperty<>("map", new HashMap<>(), PropertyType.stringType());

        given(reader.getObject("map")).willReturn(new HashMap<String, String>() {{
            put("test", "keks");
        }});

        assertThat(property.determineValue(reader), equalTo(new HashMap<String, String>() {{
            put("test", "keks");
        }}));
    }

    @Test
    public void shouldReturnDefaultValue() {
        MapProperty<String> property = new MapProperty<>("map", new HashMap<String, String>() {{
            put("test", "keks");
        }}, PropertyType.stringType());

        given(reader.getObject("map")).willReturn(null);

        assertThat(property.determineValue(reader), equalTo(new HashMap<String, String>() {{
            put("test", "keks");
        }}));
    }

    @Test
    public void shouldReturnValueAsExportValue() {
        MapProperty<String> property = new MapProperty<>("map", new HashMap<>(), PropertyType.stringType());

        Map<String, String> given = new HashMap<String, String>() {{
            put("test", "keks");
        }};

        assertThat(property.toExportValue(given), equalTo(given));
    }

}
