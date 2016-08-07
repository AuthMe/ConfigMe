package com.github.authme.configme.propertymap;

import com.github.authme.configme.properties.Property;
import com.github.authme.configme.samples.TestConfiguration;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Test for {@link SettingsFieldRetriever}.
 */
public class SettingsFieldRetrieverTest {

    @Test
    public void shouldGetAllProperties() {
        // given
        SettingsFieldRetriever retriever =
            new SettingsFieldRetriever(TestConfiguration.class, AdditionalTestConfiguration.class);

        // when
        PropertyMap propertyMap = retriever.getAllPropertyFields();

        // then
        // 3 properties in AdditionalTestConfiguration, 10 properties in TestConfiguration
        assertThat(propertyMap.getEntries(), hasSize(13));
        // Take some samples, check for presence & expected comments
        assertHasPropertyWithComments(propertyMap, TestConfiguration.SKIP_BORING_FEATURES, "Skip boring features?");
        assertHasPropertyWithComments(propertyMap, TestConfiguration.DUST_LEVEL);
        assertHasPropertyWithComments(propertyMap, TestConfiguration.VERSION_NUMBER,
            "The version number", "This is just a random number");
        assertHasPropertyWithComments(propertyMap, AdditionalTestConfiguration.NAME, "Additional name");
        assertHasPropertyWithComments(propertyMap, AdditionalTestConfiguration.SLEEP, "Seconds to sleep");
    }

    private static void assertHasPropertyWithComments(PropertyMap propertyMap, Property<?> property,
                                                      String... comments) {
        for (PropertyEntry entry : propertyMap.getEntries()) {
            if (entry.getProperty().equals(property)) {
                assertThat(entry.getComments(), equalTo(comments));
                return;
            }
        }
        fail("Not found in property map: " + property);
    }

}
