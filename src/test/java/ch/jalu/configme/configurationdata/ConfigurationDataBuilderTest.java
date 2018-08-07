package ch.jalu.configme.configurationdata;

import ch.jalu.configme.TestUtils;
import ch.jalu.configme.configurationdata.samples.AdditionalTestConfiguration;
import ch.jalu.configme.configurationdata.samples.IllegalSettingsHolderConstructorClasses;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.samples.ClassWithPrivatePropertyField;
import ch.jalu.configme.samples.TestConfiguration;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static ch.jalu.configme.TestUtils.verifyException;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
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
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(
            TestConfiguration.class, AdditionalTestConfiguration.class);

        // then
        assertThat(configurationData.getCommentsForSection("additional"), contains("Section comment for 'additional'"));
        assertThat(configurationData.getCommentsForSection("bogus"), contains("This section does not exist anywhere"));
        assertThat(configurationData.getCommentsForSection("other.section"), empty());
        assertThat(configurationData.getCommentsForSection("notDefinedAnywhere"), empty());

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
        TestUtils.validateHasOnlyProtectedEmptyConstructor(ConfigurationDataBuilder.class);
    }

    @Test
    public void shouldHandleSettingsHolderConstructorIssues() {
        // Missing no-args constructor
        verifyException(
            () -> ConfigurationDataBuilder.createConfiguration(IllegalSettingsHolderConstructorClasses.MissingNoArgsConstructor.class),
            ConfigMeException.class,
            "Expected no-args constructor to be available");

        // Constructor throws exception
        verifyException(
            () -> ConfigurationDataBuilder.createConfiguration(IllegalSettingsHolderConstructorClasses.ThrowingConstructor.class),
            ConfigMeException.class,
            "Could not create instance");

        // Class is abstract
        verifyException(
            () -> ConfigurationDataBuilder.createConfiguration(IllegalSettingsHolderConstructorClasses.AbstractClass.class),
            ConfigMeException.class,
            "Could not create instance");

        // Class is interface
        verifyException(
            () -> ConfigurationDataBuilder.createConfiguration(IllegalSettingsHolderConstructorClasses.InterfaceSettingsHolder.class),
            ConfigMeException.class,
            "Expected no-args constructor to be available");
    }

    @Test
    public void shouldWrapIllegalAccessExceptionIntoConfigMeException() throws NoSuchFieldException {
        // given
        ConfigurationDataBuilder configurationDataBuilder = new ConfigurationDataBuilder();
        Field privateProperty = ClassWithPrivatePropertyField.class.getDeclaredField("PRIVATE_INT_PROPERTY");

        // when / then
        verifyException(
            () -> configurationDataBuilder.getPropertyField(privateProperty),
            ConfigMeException.class,
            "Is it maybe not public?");
    }

    @Test
    public void shouldCreateConfigDataWithPropertiesList() {
        // given
        List<Property<?>> properties = Arrays.asList(
            newProperty("test", "t"),
            newProperty("test.test", "oo"),
            newProperty("test.int", 4));

        // when
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(properties);

        // then
        assertThat(configurationData.getProperties(), equalTo(properties));
        assertThat(configurationData.getCommentsForSection("test.test"), empty());
    }

    @Test
    public void shouldCreateConfigDataWithPropertiesListAndCommentsMap() {
        // given
        List<Property<?>> properties = Arrays.asList(
            newProperty("test", "t"),
            newProperty("test.test", "oo"),
            newProperty("test.int", 4));
        CommentsConfiguration commentsConfiguration = new CommentsConfiguration();
        commentsConfiguration.setComment("test.test", "Comment for 'test.test'", "Two lines here");

        // when
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(properties, commentsConfiguration);

        // then
        assertThat(configurationData.getProperties(), equalTo(properties));
        assertThat(configurationData.getCommentsForSection("test.test"), contains("Comment for 'test.test'", "Two lines here"));
        assertThat(configurationData.getCommentsForSection("test.int"), empty());
    }

    private static void assertHasPropertyWithComments(ConfigurationData configurationData, Property<?> property,
                                                      String... comments) {
        for (Property<?> knownProperty : configurationData.getProperties()) {
            if (knownProperty.equals(property)) {
                if (comments.length == 0) {
                    assertThat(configurationData.getCommentsForSection(property.getPath()), empty());
                } else {
                    assertThat(configurationData.getCommentsForSection(property.getPath()), contains(comments));
                }
                return;
            }
        }
        fail("Not found in property map: " + property);
    }
}
