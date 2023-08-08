package ch.jalu.configme.migration.version;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.properties.IntegerProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.resource.PropertyReader;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Test for {@link VersionMigrationService}.
 */
@ExtendWith(MockitoExtension.class)
class VersionMigrationServiceTest {

    @Test
    void shouldNotSaveIfVersionIsUpToDate() {
        // given
        Property<Integer> versionProperty = createVersionProperty(3);
        TestVersionMigrationImpl migration1To2 = new TestVersionMigrationImpl(1, 2);
        TestVersionMigrationImpl migration2To3 = new TestVersionMigrationImpl(2, 3);

        VersionMigrationService migrationService = new VersionMigrationService(versionProperty, migration1To2, migration2To3);

        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getObject(versionProperty.getPath())).willReturn(3);
        ConfigurationData configurationData = mock(ConfigurationData.class);

        // when
        boolean needsSave = migrationService.performMigrations(reader, configurationData);

        // then
        assertThat(needsSave, equalTo(false));
        verifyNoInteractions(configurationData);
        assertThat(migration1To2.timesCalled, equalTo(0));
        assertThat(migration2To3.timesCalled, equalTo(0));
    }

    @Test
    void shouldTriggerSaveForCurrentVersionButInvalidValueInConfig() {
        // given
        Property<Integer> versionProperty = createVersionProperty(3);
        TestVersionMigrationImpl migration1To2 = new TestVersionMigrationImpl(1, 2);
        TestVersionMigrationImpl migration2To3 = new TestVersionMigrationImpl(2, 3);

        VersionMigrationService migrationService = new VersionMigrationService(versionProperty, migration1To2, migration2To3);

        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getObject(versionProperty.getPath())).willReturn(3);
        ConfigurationData configurationData = mock(ConfigurationData.class);
        given(configurationData.areAllValuesValidInResource()).willReturn(false); // <-- Invalid value; resave expected.

        // when
        boolean needsSave = migrationService.checkAndMigrate(reader, configurationData);

        // then
        assertThat(needsSave, equalTo(true));
        verify(configurationData, only()).areAllValuesValidInResource();
        assertThat(migration1To2.timesCalled, equalTo(0));
        assertThat(migration2To3.timesCalled, equalTo(0));
    }

    @Test
    void shouldMigrateFromOlderVersionToNewerWithOneMigration() {
        // given
        Property<Integer> versionProperty = createVersionProperty(3);
        TestVersionMigrationImpl migration1To3 = new TestVersionMigrationImpl(1, 3);
        TestVersionMigrationImpl migration2To3 = new TestVersionMigrationImpl(2, 3);

        VersionMigrationService migrationService = new VersionMigrationService(versionProperty, migration1To3, migration2To3);

        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getObject(versionProperty.getPath())).willReturn(1);
        ConfigurationData configurationData = mock(ConfigurationData.class);

        // when
        boolean needsSave = migrationService.checkAndMigrate(reader, configurationData);

        // then
        assertThat(needsSave, equalTo(true));
        verify(configurationData, only()).setValue(versionProperty, 3);
        assertThat(migration1To3.timesCalled, equalTo(1));
        assertThat(migration2To3.timesCalled, equalTo(0));
    }

    @Test
    void shouldMigrateFromOlderVersionToNewerWithSuccessiveMigrations() {
        // given
        Property<Integer> versionProperty = createVersionProperty(5);
        TestVersionMigrationImpl migration1To2 = new TestVersionMigrationImpl(1, 2);
        TestVersionMigrationImpl migration2To3 = new TestVersionMigrationImpl(2, 3);
        TestVersionMigrationImpl migration3To5 = new TestVersionMigrationImpl(3, 5);
        TestVersionMigrationImpl migration4To5 = new TestVersionMigrationImpl(4, 5);

        VersionMigrationService migrationService = new VersionMigrationService(versionProperty,
            migration1To2, migration2To3, migration3To5, migration4To5);

        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getObject(versionProperty.getPath())).willReturn(1);
        ConfigurationData configurationData = mock(ConfigurationData.class);

        // when
        boolean needsSave = migrationService.checkAndMigrate(reader, configurationData);

        // then
        assertThat(needsSave, equalTo(true));
        verify(configurationData, only()).setValue(versionProperty, 5);
        assertThat(migration1To2.timesCalled, equalTo(1));
        assertThat(migration2To3.timesCalled, equalTo(1));
        assertThat(migration3To5.timesCalled, equalTo(1));
        assertThat(migration4To5.timesCalled, equalTo(0));
    }

    @Test
    void shouldNotRunMigrationsAndSetCurrentValueIfNoMigrationsApply() {
        // given
        Property<Integer> versionProperty = createVersionProperty(4);
        TestVersionMigrationImpl migration2To3 = new TestVersionMigrationImpl(2, 3);
        TestVersionMigrationImpl migration3To4 = new TestVersionMigrationImpl(3, 4);

        VersionMigrationService migrationService = new VersionMigrationService(versionProperty, migration2To3, migration3To4);

        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getObject(versionProperty.getPath())).willReturn(0);
        ConfigurationData configurationData = mock(ConfigurationData.class);

        // when
        boolean needsSave = migrationService.performMigrations(reader, configurationData);

        // then
        assertThat(needsSave, equalTo(true));
        verify(configurationData, only()).setValue(versionProperty, 4);
        assertThat(migration2To3.timesCalled, equalTo(0));
        assertThat(migration3To4.timesCalled, equalTo(0));
    }

    @Test
    void shouldThrowForMultipleMigrationsWithSameStartVersion() {
        // given
        Property<Integer> versionProperty = createVersionProperty(3);
        List<VersionMigration> migrations = Arrays.asList(
            new TestVersionMigrationImpl(1, 2),
            new TestVersionMigrationImpl(2, 3),
            new TestVersionMigrationImpl(1, 3));

        // when
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> new VersionMigrationService(versionProperty, migrations));

        // then
        assertThat(ex.getMessage(), equalTo("Multiple migrations were provided for start version 1"));
    }

    @Test
    void shouldThrowForMigrationLargerThanVersionProperty() {
        // given
        Property<Integer> versionProperty = createVersionProperty(3);
        List<VersionMigration> migrations = Arrays.asList(
            new TestVersionMigrationImpl(1, 2),
            new TestVersionMigrationImpl(2, 4));

        // when
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> new VersionMigrationService(versionProperty, migrations));

        // then
        assertThat(ex.getMessage(), equalTo("The migration from version 2 to version 4 has an invalid target version. Current configuration version is: 3"));
    }

    @Test
    void shouldThrowForMigrationWithTargetVersionEqualsStartVersion() {
        // given
        Property<Integer> versionProperty = createVersionProperty(3);
        List<VersionMigration> migrations = Arrays.asList(
            new TestVersionMigrationImpl(1, 2),
            new TestVersionMigrationImpl(2, 2));

        // when
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> new VersionMigrationService(versionProperty, migrations));

        // then
        assertThat(ex.getMessage(), equalTo("A migration from version 2 to version 2 was supplied, but it is expected that the target version be larger than the start version"));
    }

    @Test
    void shouldReturnPropertyAndMigrationsForExtendingClasses() {
        // given
        Property<Integer> versionProperty = createVersionProperty(3);
        TestVersionMigrationImpl migration1To2 = new TestVersionMigrationImpl(1, 2);
        TestVersionMigrationImpl migration2To3 = new TestVersionMigrationImpl(2, 3);
        VersionMigrationService migrationService = new VersionMigrationService(versionProperty, migration1To2, migration2To3);

        // when
        Property<Integer> returnedVersionProp = migrationService.getVersionProperty();
        Map<Integer, VersionMigration> returnedMigrations = migrationService.getMigrationsByStartVersion();

        // then
        assertThat(returnedVersionProp, sameInstance(versionProperty));
        assertThat(returnedMigrations, aMapWithSize(2));
        assertThat(returnedMigrations.get(1), sameInstance(migration1To2));
        assertThat(returnedMigrations.get(2), sameInstance(migration2To3));
    }

    /**
     * Tests the return value of {@link VersionMigrationService#runApplicableMigrations}, which may be useful for
     * extending the class (the current implementation does not use it).
     */
    @ParameterizedTest
    @MethodSource("createMigrationVersionArgs")
    void shouldRunMigrationsAndReturnLastTargetVersion(int readConfigVersion, int expectedVersionAfterMigration) {
        // given
        Property<Integer> versionProperty = createVersionProperty(10);

        TestVersionMigrationImpl migration1To2 = new TestVersionMigrationImpl(1, 2);
        TestVersionMigrationImpl migration2To3 = new TestVersionMigrationImpl(2, 3);
        TestVersionMigrationImpl migration3To5 = new TestVersionMigrationImpl(3, 5);
        TestVersionMigrationImpl migration4To7 = new TestVersionMigrationImpl(4, 7);
        TestVersionMigrationImpl migration7To8 = new TestVersionMigrationImpl(7, 8);
        VersionMigrationService migrationService = new VersionMigrationService(versionProperty,
            migration1To2, migration2To3, migration3To5, migration4To7, migration7To8);

        PropertyReader reader = mock(PropertyReader.class);
        ConfigurationData configData = mock(ConfigurationData.class);

        // when
        int versionAfterMigration = migrationService.runApplicableMigrations(readConfigVersion, reader, configData);

        // then
        assertThat(versionAfterMigration, equalTo(expectedVersionAfterMigration));
    }

    private static List<Arguments> createMigrationVersionArgs() {
        // Args of read config value -> expected return value
        return Arrays.asList(
            Arguments.of(1, 5), // Migrations: 1->2, 2->3, 3->5
            Arguments.of(3, 5), // Migrations: 3->5
            Arguments.of(4, 8), // Migrations: 4->7, 7->8
            Arguments.of(7, 8), // Migrations: 7->8

            // Rest below has no associated migration
            Arguments.of(8, 8),
            Arguments.of(0, 0),
            Arguments.of(5, 5),
            Arguments.of(200, 200));
    }

    private static Property<Integer> createVersionProperty(int defaultValue) {
        return new IntegerProperty("config.version", defaultValue);
    }

    private static final class TestVersionMigrationImpl implements VersionMigration {

        private final int fromVersion;
        private final int targetVersion;
        private int timesCalled;

        TestVersionMigrationImpl(int fromVersion, int targetVersion) {
            this.fromVersion = fromVersion;
            this.targetVersion = targetVersion;
        }

        @Override
        public int fromVersion() {
            return fromVersion;
        }

        @Override
        public int targetVersion() {
            return targetVersion;
        }

        @Override
        public void migrate(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
            ++timesCalled;
        }
    }
}
