package com.github.authme.configme.knownproperties;

import com.github.authme.configme.TestUtils;
import com.github.authme.configme.exception.ConfigMeException;
import com.github.authme.configme.knownproperties.samples.AdditionalTestConfiguration;
import com.github.authme.configme.knownproperties.samples.SectionCommentsFailClasses;
import com.github.authme.configme.properties.Property;
import com.github.authme.configme.samples.TestConfiguration;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Test for {@link ConfigurationDataBuilder}.
 */
public class ConfigurationDataBuilderTest {

    @Test
    public void shouldGetAllProperties() {
        // given / when
        ConfigurationData configurationData = ConfigurationDataBuilder.getAllProperties(
            TestConfiguration.class, AdditionalTestConfiguration.class);

        // then
        assertThat(configurationData.getCommentsForSection("additional"), arrayContaining("Section comment for 'additional'"));
        assertThat(configurationData.getCommentsForSection("bogus"), arrayContaining("This section does not exist anywhere"));
        assertThat(configurationData.getCommentsForSection("other.section"), emptyArray());
        assertThat(configurationData.getCommentsForSection("notDefinedAnywhere"), emptyArray());

        List<PropertyEntry> knownProperties = configurationData.getPropertyEntries();
        // 3 properties in AdditionalTestConfiguration, 10 properties in TestConfiguration
        assertThat(knownProperties, hasSize(13));
        // Take some samples, check for presence & expected comments
        assertHasPropertyWithComments(knownProperties, TestConfiguration.SKIP_BORING_FEATURES, "Skip boring features?");
        assertHasPropertyWithComments(knownProperties, TestConfiguration.DUST_LEVEL);
        assertHasPropertyWithComments(knownProperties, TestConfiguration.VERSION_NUMBER,
            "The version number", "This is just a random number");
        assertHasPropertyWithComments(knownProperties, AdditionalTestConfiguration.NAME, "Additional name");
        assertHasPropertyWithComments(knownProperties, AdditionalTestConfiguration.SLEEP, "Seconds to sleep");
    }

    @Test
    public void shouldHavePrivateConstructor() {
        TestUtils.validateHasOnlyPrivateEmptyConstructor(ConfigurationDataBuilder.class);
    }

    @Test
    public void shouldHandleMalformedSectionCommentClasses() {
        // Wrong return type
        assertHasException(
            () -> ConfigurationDataBuilder.getAllProperties(SectionCommentsFailClasses.WrongReturnType.class),
            "Return value must be Map<String, String>");

        // Non-static method
        assertHasException(
            () -> ConfigurationDataBuilder.getAllProperties(SectionCommentsFailClasses.NonStaticMethod.class),
            "must be static");

        // Method with parameters
        assertHasException(
            () -> ConfigurationDataBuilder.getAllProperties(SectionCommentsFailClasses.MethodWithParameters.class),
            "may not have any parameters");

        // Throwing method
        assertHasException(
            () -> ConfigurationDataBuilder.getAllProperties(SectionCommentsFailClasses.ThrowingMethod.class),
            "Could not get section comments");
    }

    private void assertHasException(Runnable runnable, String messageExcerpt) {
        try {
            runnable.run();
            fail("Expected exception to be thrown");
        } catch (ConfigMeException e) {
            assertThat(e.getMessage(), containsString(messageExcerpt));
        }
    }

    private static void assertHasPropertyWithComments(List<PropertyEntry> knownProperties, Property<?> property,
                                                      String... comments) {
        for (PropertyEntry entry : knownProperties) {
            if (entry.getProperty().equals(property)) {
                assertThat(entry.getComments(), equalTo(comments));
                return;
            }
        }
        fail("Not found in property map: " + property);
    }

}
