package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.PropertyType;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ListPropertyTest {

    @Mock
    private PropertyReader reader;

    @Test
    public void shouldReturnValueFromResource() {
        Property<List<String>> property = new ListProperty<>("list", Collections.emptyList(), PropertyType.stringType());

        given(reader.getObject("list")).willReturn(Arrays.asList("hello", "it is list"));

        assertThat(property.determineValue(reader), equalTo(Arrays.asList("hello", "it is list")));
    }

    @Test
    public void shouldReturnDefaultValue() {
        Property<List<String>> property = new ListProperty<>("list", Arrays.asList("default list", "you are pidor c:"), PropertyType.stringType());

        given(reader.getObject("list")).willReturn(null);

        assertThat(property.determineValue(reader), equalTo(Arrays.asList("default list", "you are pidor c:")));
    }

}
