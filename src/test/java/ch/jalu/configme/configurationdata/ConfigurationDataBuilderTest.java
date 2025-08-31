package ch.jalu.configme.configurationdata;

import ch.jalu.configme.configurationdata.samples.AdditionalTestConfiguration;
import ch.jalu.configme.configurationdata.samples.IllegalSettingsHolderConstructorClasses;
import ch.jalu.configme.configurationdata.samples.inheritance.ChildInheritanceSettingsHolder;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.samples.ClassWithPrivatePropertyField;
import ch.jalu.configme.samples.TestConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static ch.jalu.configme.TestUtils.transform;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link ConfigurationDataBuilder}.
 */
@ExtendWith(MockitoExtension.class)
class ConfigurationDataBuilderTest {

    @Test
    void shouldGetAllProperties() {
        // given / when
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(
            TestConfiguration.class, AdditionalTestConfiguration.class);

        // then
        assertThat(configurationData.getCommentsForSection("additional"), contains("Section comment for 'additional'"));
        assertThat(configurationData.getCommentsForSection("bogus"), contains("This section does not exist anywhere"));
        assertThat(configurationData.getCommentsForSection("other.section"), empty());
        assertThat(configurationData.getCommentsForSection("notDefinedAnywhere"), empty());

        // 3 properties in AdditionalTestConfiguration, 11 properties in TestConfiguration
        assertThat(configurationData.getProperties(), hasSize(14));
        // Take some samples, check for presence & expected comments
        assertHasPropertyWithComments(configurationData, TestConfiguration.SKIP_BORING_FEATURES, "Skip boring features?");
        assertHasPropertyWithComments(configurationData, TestConfiguration.DUST_LEVEL);
        assertHasPropertyWithComments(configurationData, TestConfiguration.VERSION_NUMBER,
            "The version number", "This is just a random number");
        assertHasPropertyWithComments(configurationData, AdditionalTestConfiguration.NAME, "Additional name");
        assertHasPropertyWithComments(configurationData, AdditionalTestConfiguration.SLEEP, "Seconds to sleep");
    }

    @Test
    void shouldHandleSettingsHolderConstructorIssues() {
        ConfigMeException ex;

        // Missing no-arg constructor
        ex = assertThrows(ConfigMeException.class,
            () -> ConfigurationDataBuilder.createConfiguration(IllegalSettingsHolderConstructorClasses.MissingNoArgConstructor.class));
        assertThat(ex.getMessage(), startsWith("Expected no-arg constructor to be available for class "));

        // Constructor throws exception
        ex = assertThrows(ConfigMeException.class,
            () -> ConfigurationDataBuilder.createConfiguration(IllegalSettingsHolderConstructorClasses.ThrowingConstructor.class));
        assertThat(ex.getMessage(), startsWith("Could not create instance of class "));

        // Class is abstract
        ex = assertThrows(ConfigMeException.class,
            () -> ConfigurationDataBuilder.createConfiguration(IllegalSettingsHolderConstructorClasses.AbstractClass.class));
        assertThat(ex.getMessage(), startsWith("Could not create instance of class "));

        // Class is interface
        ex = assertThrows(ConfigMeException.class,
            () -> ConfigurationDataBuilder.createConfiguration(IllegalSettingsHolderConstructorClasses.InterfaceSettingsHolder.class));
        assertThat(ex.getMessage(), startsWith("Expected no-arg constructor to be available for interface "));
    }

    @Test
    void shouldAccessPrivateField() throws NoSuchFieldException {
        // given
        ConfigurationDataBuilder configurationDataBuilder = new ConfigurationDataBuilder();
        Field privateProperty = ClassWithPrivatePropertyField.class.getDeclaredField("PRIVATE_INT_PROPERTY");

        // when
        Property<?> result = configurationDataBuilder.getPropertyField(privateProperty);

        // then
        assertThat(result, sameInstance(ClassWithPrivatePropertyField.getPrivatePropertyValue()));
    }

    @Test
    void shouldCreateConfigDataWithPropertiesList() {
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
    void shouldCreateConfigDataWithPropertiesListAndCommentsMap() {
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

    @Test
    void shouldCollectPropertiesAlsoFromParentClasses() {
        // given / when
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(ChildInheritanceSettingsHolder.class);

        // then
        List<String> propertyPaths = transform(configurationData.getProperties(), Property::getPath);
        assertThat(propertyPaths, contains("top.string", "middle.version", "sample.name", "sample.subtitle", "child.double"));
        assertThat(configurationData.getCommentsForSection("middle"), contains("Comes from the holder in the middle"));
    }

    @Test
    void shouldReturnFields() {
        // given
        PropertyListBuilder propertyListBuilder = mock(PropertyListBuilder.class);
        CommentsConfiguration commentsConfiguration = mock(CommentsConfiguration.class);
        ConfigurationDataBuilder confDataBuilder = new ConfigurationDataBuilder(propertyListBuilder, commentsConfiguration);

        // when
        PropertyListBuilder returnedListBuilder = confDataBuilder.getPropertyListBuilder();
        CommentsConfiguration returnedCommentsConf = confDataBuilder.getCommentsConfiguration();

        // then
        assertThat(returnedListBuilder, sameInstance(propertyListBuilder));
        assertThat(returnedCommentsConf, sameInstance(commentsConfiguration));
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
