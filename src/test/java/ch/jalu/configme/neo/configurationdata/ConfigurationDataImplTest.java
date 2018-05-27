package ch.jalu.configme.neo.configurationdata;

import ch.jalu.configme.neo.properties.Property;
import ch.jalu.configme.neo.properties.StringProperty;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ch.jalu.configme.TestUtils.containsAll;
import static ch.jalu.configme.TestUtils.verifyException;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link ConfigurationDataImpl}.
 */
public class ConfigurationDataImplTest {

    @Test
    public void shouldAcceptListWithTypedProperty() {
        // given
        List<Property<?>> properties = Arrays.asList(
            new StringProperty("test", "Test"),
            new StringProperty("taste", "Taste"),
            new StringProperty("toast", "Toaster"));

        // when
        ConfigurationData configData = new ConfigurationDataImpl(properties, Collections.emptyMap());

        // then
        assertThat(configData.getProperties(), containsAll(properties));
    }

    @Test
    public void shouldHaveImmutablePropertyList() {
        // given
        List<Property<?>> properties = Collections.singletonList(new StringProperty("test", ""));
        ConfigurationData configData = new ConfigurationDataImpl(properties, Collections.emptyMap());

        // when / then
        verifyException(() -> configData.getProperties().remove(0), UnsupportedOperationException.class);
    }
}
