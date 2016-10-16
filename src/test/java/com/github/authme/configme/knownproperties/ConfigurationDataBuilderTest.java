package com.github.authme.configme.knownproperties;

import com.github.authme.configme.TestUtils;
import com.github.authme.configme.exception.ConfigMeException;
import com.github.authme.configme.knownproperties.samples.AdditionalTestConfiguration;
import com.github.authme.configme.knownproperties.samples.SectionCommentsFailClasses;
import com.github.authme.configme.properties.Property;
import com.github.authme.configme.samples.TestConfiguration;
import org.junit.Test;

import static com.github.authme.configme.TestUtils.verifyException;
import static org.hamcrest.Matchers.arrayContaining;
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
        ConfigurationData configurationData = ConfigurationDataBuilder.collectData(
            TestConfiguration.class, AdditionalTestConfiguration.class);

        // then
        assertThat(configurationData.getCommentsForSection("additional"), arrayContaining("Section comment for 'additional'"));
        assertThat(configurationData.getCommentsForSection("bogus"), arrayContaining("This section does not exist anywhere"));
        assertThat(configurationData.getCommentsForSection("other.section"), emptyArray());
        assertThat(configurationData.getCommentsForSection("notDefinedAnywhere"), emptyArray());

        // 3 properties in AdditionalTestConfiguration, 10 properties in TestConfiguration
        assertThat(configurationData.getProperties(), hasSize(13));
        // Take some samples, check for presence & expected comments
        assertHasPropertyWithComments(configurationData, TestConfiguration.SKIP_BORING_FEATURES, "Skip boring features?");
        assertHasPropertyWithComments(configurationData, TestConfiguration.DUST_LEVEL);
        assertHasPropertyWithComments(configurationData, TestConfiguration.VERSION_NUMBER,
            "The version number", "This is just a random number");
        assertHasPropertyWithComments(configurationData, AdditionalTestConfiguration.NAME, "Additional name");
        assertHasPropertyWithComments(configurationData, AdditionalTestConfiguration.SLEEP, "Seconds to sleep");
    }

    @Test
    public void shouldHavePrivateConstructor() {
        TestUtils.validateHasOnlyPrivateEmptyConstructor(ConfigurationDataBuilder.class);
    }

    @Test
    public void shouldHandleMalformedSectionCommentClasses() {
        // Wrong return type
        verifyException(
            () -> ConfigurationDataBuilder.collectData(SectionCommentsFailClasses.WrongReturnType.class),
            ConfigMeException.class,
            "Return value must be Map<String, String>");

        // Non-static method
        verifyException(
            () -> ConfigurationDataBuilder.collectData(SectionCommentsFailClasses.NonStaticMethod.class),
            ConfigMeException.class,
            "must be static");

        // Method with parameters
        verifyException(
            () -> ConfigurationDataBuilder.collectData(SectionCommentsFailClasses.MethodWithParameters.class),
            ConfigMeException.class,
            "may not have any parameters");

        // Throwing method
        verifyException(
            () -> ConfigurationDataBuilder.collectData(SectionCommentsFailClasses.ThrowingMethod.class),
            ConfigMeException.class,
            "Could not get section comments");
    }

    private static void assertHasPropertyWithComments(ConfigurationData configurationData, Property<?> property,
                                                      String... comments) {
        for (Property<?> knownProperty : configurationData.getProperties()) {
            if (knownProperty.equals(property)) {
                assertThat(configurationData.getCommentsForSection(property.getPath()), equalTo(comments));
                return;
            }
        }
        fail("Not found in property map: " + property);
    }

}
