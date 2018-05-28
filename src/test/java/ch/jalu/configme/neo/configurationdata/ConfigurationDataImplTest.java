package ch.jalu.configme.neo.configurationdata;

import ch.jalu.configme.neo.properties.Property;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ch.jalu.configme.TestUtils.containsAll;
import static ch.jalu.configme.TestUtils.verifyException;
import static ch.jalu.configme.neo.properties.PropertyInitializer.newProperty;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link ConfigurationDataImpl}.
 */
public class ConfigurationDataImplTest {

    @Test
    public void shouldAcceptListWithTypedProperty() {
        // given
        List<Property<?>> properties = Arrays.asList(
            newProperty("test", "Test"),
            newProperty("taste", "Taste"),
            newProperty("toast", "Toaster"));

        // when
        ConfigurationData configData = new ConfigurationDataImpl(properties, Collections.emptyMap());

        // then
        assertThat(configData.getProperties(), containsAll(properties));
    }

    @Test
    public void shouldHaveImmutablePropertyList() {
        // given
        List<Property<?>> properties = Collections.singletonList(newProperty("test", ""));
        ConfigurationData configData = new ConfigurationDataImpl(properties, Collections.emptyMap());

        // when / then
        verifyException(() -> configData.getProperties().remove(0), UnsupportedOperationException.class);
    }
}
