package ch.jalu.configme.utils;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.migration.MigrationService;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.resource.PropertyReader;
import ch.jalu.configme.resource.PropertyResource;
import ch.jalu.configme.samples.settingsholders.FullyValidSettingsHolder1;
import ch.jalu.configme.samples.settingsholders.FullyValidSettingsHolder2;
import ch.jalu.configme.samples.settingsholders.MissingCommentsHolder;
import ch.jalu.configme.samples.settingsholders.SettingsHolderWithEnumPropertyComments;
import ch.jalu.configme.samples.settingsholders.SettingsHolderWithInvalidConstants;
import ch.jalu.configme.samples.settingsholders.SettingsHolderWithVariousCommentLengths;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

/**
 * Test for {@link SettingsHolderClassValidator}.
 */
class SettingsHolderClassValidatorTest {

    private SettingsHolderClassValidator validator = new SettingsHolderClassValidator();

    @Test
    void shouldValidateSuccessfully() {
        // given
        SettingsHolderClassValidator validatorSpy = Mockito.spy(validator);

        // when
        validatorSpy.validate(FullyValidSettingsHolder1.class, FullyValidSettingsHolder2.class);

        // then - no exception
        Matcher<Iterable<Class<? extends SettingsHolder>>> matcher = (Matcher) contains(FullyValidSettingsHolder1.class, FullyValidSettingsHolder2.class);
        verify(validatorSpy).validateAllPropertiesAreConstants(argThat(matcher));
        verify(validatorSpy).validateSettingsHolderClassesFinal(argThat(matcher));
        verify(validatorSpy).validateClassesHaveHiddenNoArgConstructor(argThat(matcher));
        verify(validatorSpy).validateHasCommentOnEveryProperty(any(ConfigurationData.class), isNull());
        verify(validatorSpy).validateCommentLengthsAreWithinBounds(any(ConfigurationData.class), isNull(), eq(90));
        verify(validatorSpy).validateHasAllEnumEntriesInComment(any(ConfigurationData.class), isNull());
    }

    @Test
    void shouldThrowForNonConstantPropertyDeclarations() {
        // given
        List<Class<? extends SettingsHolder>> classes = Arrays.asList(FullyValidSettingsHolder2.class, SettingsHolderWithInvalidConstants.class);

        // when
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> validator.validateAllPropertiesAreConstants(classes));

        // then
        assertThat(e.getMessage(), equalTo("The following fields were found not to be public static final:"
            + "\n- SettingsHolderWithInvalidConstants#DBL_PROP"
            + "\n- SettingsHolderWithInvalidConstants#STRLIST"
            + "\n- SettingsHolderWithInvalidConstants#TIME_UNIT"));
    }

    @Test
    void shouldCheckParentClassesForPropertyFields() {
        // given
        List<Class<? extends SettingsHolder>> classes = Arrays.asList(FullyValidSettingsHolder2.class, SettingsHolderWithInvalidConstants.Child.class);

        // when
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> validator.validateAllPropertiesAreConstants(classes));

        // then
        assertThat(e.getMessage(), equalTo("The following fields were found not to be public static final:"
            + "\n- Child#strProp"
            + "\n- SettingsHolderWithInvalidConstants#DBL_PROP"
            + "\n- SettingsHolderWithInvalidConstants#STRLIST"
            + "\n- SettingsHolderWithInvalidConstants#TIME_UNIT"));
    }

    @Test
    void shouldThrowForHolderMissingComment() {
        // given
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(
            FullyValidSettingsHolder1.class, MissingCommentsHolder.class);

        // when
        IllegalStateException e = assertThrows(IllegalStateException.class,
            () -> validator.validateHasCommentOnEveryProperty(configurationData, null));

        // then
        assertThat(e.getMessage(), equalTo("The following properties do not have a comment:\n- Property 'lorem.ipsum'\n- Property 'lorem.dolor'"));
    }

    @Test
    void shouldCheckForPropertiesWithoutCommentsAndRespectPropertyFilter() {
        // given
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(
            FullyValidSettingsHolder1.class, MissingCommentsHolder.class);
        Predicate<Property<?>> filter = p -> !"lorem.dolor".equals(p.getPath());

        // when
        IllegalStateException e = assertThrows(IllegalStateException.class,
            () -> validator.validateHasCommentOnEveryProperty(configurationData, filter));

        // then
        assertThat(e.getMessage(), equalTo("The following properties do not have a comment:\n- Property 'lorem.ipsum'"));
    }

    @Test
    void shouldValidateCommentLengthWithGivenBounds() {
        // given
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(SettingsHolderWithVariousCommentLengths.class);

        // when
        IllegalStateException e1 = assertThrows(IllegalStateException.class,
            () -> validator.validateCommentLengthsAreWithinBounds(configurationData, null, 25));
        IllegalStateException e2 = assertThrows(IllegalStateException.class,
            () -> validator.validateCommentLengthsAreWithinBounds(configurationData, 20, null));
        IllegalStateException e3 = assertThrows(IllegalStateException.class,
            () -> validator.validateCommentLengthsAreWithinBounds(configurationData, 15, 30));

        // then
        assertThat(e1.getMessage(), equalTo("The comments for the following paths are not within the bounds: "
            + "max length of 25 characters:\n- Path 'comment'\n- Path 'comment.40'"));
        assertThat(e2.getMessage(), equalTo("The comments for the following paths are not within the bounds: "
            + "min length of 20 characters:\n- Path ''\n- Path 'comment.5'"));
        assertThat(e3.getMessage(), equalTo("The comments for the following paths are not within the bounds: "
            + "min length of 15, max length of 30 characters:\n- Path ''\n- Path 'comment.5'\n- Path 'comment.40'"));
    }

    @Test
    void shouldThrowIfBothLengthConstraintsAreNull() {
        // given
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(SettingsHolderWithVariousCommentLengths.class);

        // when
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
            () -> validator.validateCommentLengthsAreWithinBounds(configurationData, null, null));

        // then
        assertThat(e.getMessage(), equalTo("min length or max length must be not null"));
    }

    @Test
    void shouldVerifyAllEnumEntriesInComment() {
        // given
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(
            FullyValidSettingsHolder1.class, SettingsHolderWithEnumPropertyComments.class);

        // when
        IllegalStateException e1 = assertThrows(IllegalStateException.class,
            () -> validator.validateHasAllEnumEntriesInComment(configurationData, null));
        IllegalStateException e2 = assertThrows(IllegalStateException.class,
            () -> validator.validateHasAllEnumEntriesInComment(configurationData, p -> !p.getPath().endsWith(".timeUnit")));

        // then
        assertThat(e1.getMessage(), equalTo("The following enum properties do not list all enum values:"
            + "\n- For Property 'sample.timeUnit': missing NANOSECONDS, MICROSECONDS, MILLISECONDS, DAYS"
            + "\n- For Property 'sample.gameMode': missing CREATIVE, SURVIVAL"));
        assertThat(e2.getMessage(), equalTo("The following enum properties do not list all enum values:"
            + "\n- For Property 'sample.gameMode': missing CREATIVE, SURVIVAL"));
    }

    @Test
    void shouldThrowForNonFinalClasses() {
        // given
        List<Class<? extends SettingsHolder>> classes = Arrays.asList(
            SettingsHolderWithEnumPropertyComments.class, FullyValidSettingsHolder1.class, SettingsHolderWithInvalidConstants.class);

        // when
        IllegalStateException e = assertThrows(IllegalStateException.class,
            () -> validator.validateSettingsHolderClassesFinal(classes));

        // then
        assertThat(e.getMessage(), equalTo("The following classes are not final:"
            + "\n- ch.jalu.configme.samples.settingsholders.SettingsHolderWithEnumPropertyComments"
            + "\n- ch.jalu.configme.samples.settingsholders.SettingsHolderWithInvalidConstants"));
    }

    @Test
    void shouldThrowForClassWithoutPrivateConstructor() {
        // given
        List<Class<? extends SettingsHolder>> classes = Arrays.asList(
            SettingsHolderWithEnumPropertyComments.class, FullyValidSettingsHolder1.class, SettingsHolderWithInvalidConstants.class);

        // when
        IllegalStateException e = assertThrows(IllegalStateException.class,
            () -> validator.validateClassesHaveHiddenNoArgConstructor(classes));

        // then
        assertThat(e.getMessage(), equalTo("The following classes do not have a single no-args private constructor:"
            + "\n- ch.jalu.configme.samples.settingsholders.SettingsHolderWithEnumPropertyComments"
            + "\n- ch.jalu.configme.samples.settingsholders.SettingsHolderWithInvalidConstants"));
    }

    @Test
    void shouldThrowForMigrationServiceRequiringMigration() {
        // given
        MigrationService migrationService = mock(MigrationService.class);
        ConfigurationData configurationData = mock(ConfigurationData.class);
        PropertyResource resource = mock(PropertyResource.class);
        PropertyReader reader = mock(PropertyReader.class);
        given(resource.createReader()).willReturn(reader);
        given(migrationService.checkAndMigrate(reader, configurationData)).willReturn(true);

        // when
        IllegalStateException e = assertThrows(IllegalStateException.class,
            () -> validator.validateConfigurationDataValidForMigrationService(configurationData, resource, migrationService));

        // then
        assertThat(e.getMessage(), equalTo("Migration service unexpectedly returned that a migration is required"));
        verify(resource).exportProperties(configurationData);
    }

    @Test
    void shouldPassValidationForMigrationServiceNotRequiringMigration() {
        // given
        MigrationService migrationService = mock(MigrationService.class);
        ConfigurationData configurationData = mock(ConfigurationData.class);
        PropertyResource resource = mock(PropertyResource.class);
        PropertyReader reader = mock(PropertyReader.class);
        given(resource.createReader()).willReturn(reader);
        given(migrationService.checkAndMigrate(reader, configurationData)).willReturn(false);

        // when
        validator.validateConfigurationDataValidForMigrationService(configurationData, resource, migrationService);

        // then - no exception
        verify(resource).exportProperties(configurationData);
    }
}
