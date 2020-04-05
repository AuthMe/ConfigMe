package ch.jalu.configme.resource;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.TestUtils;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.samples.TestEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static ch.jalu.configme.TestUtils.isValidValueOf;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests that paths which have a number in them can be read properly.
 *
 * @see <a href="https://github.com/AuthMe/ConfigMe/issues/77">Issue #77</a>
 */
class YamlFileReaderPathsWithNumberTest {

    @TempDir
    public Path temporaryFolder;

    @Test
    void shouldReadPropertiesFromFile() {
        // given
        Path file = TestUtils.copyFileFromResources("/configurationfiles/config-with-number-paths.yml", temporaryFolder);
        YamlFileResource yamlFileResource = new YamlFileResource(file);
        PropertyReader reader = yamlFileResource.createReader();

        // when / then
        assertThat(PathsWithNumbers.ONE_LEVEL.determineValue(reader), isValidValueOf(1));
        assertThat(PathsWithNumbers.TWO_LEVEL.determineValue(reader), isValidValueOf(2));
        assertThat(PathsWithNumbers.ROLE_ZERO_NAME.determineValue(reader), isValidValueOf("Initial role"));
        assertThat(PathsWithNumbers.ROLE_ZERO_CHANGE_HOME.determineValue(reader), isValidValueOf(false));
        assertThat(PathsWithNumbers.ROLE_ONE_NAME.determineValue(reader), isValidValueOf("Expert role"));
        assertThat(PathsWithNumbers.ROLE_ONE_CHANGE_HOME.determineValue(reader), isValidValueOf(true));
        assertThat(PathsWithNumbers.ROLE_FALSE_ENUM.determineValue(reader), isValidValueOf(TestEnum.SECOND));
    }

    /** Test configuration. */
    static final class PathsWithNumbers implements SettingsHolder {

        static final Property<Integer> ONE_LEVEL =
            newProperty("tiers.list.1.level", 999);

        static final Property<Integer> TWO_LEVEL =
            newProperty("tiers.list.2.level", 999);

        static final Property<String> ROLE_ZERO_NAME =
            newProperty("roles.0.name", "");

        static final Property<Boolean> ROLE_ZERO_CHANGE_HOME =
            newProperty("roles.0.change-home", false);

        static final Property<String> ROLE_ONE_NAME =
            newProperty("roles.1.name", "");

        static final Property<Boolean> ROLE_ONE_CHANGE_HOME =
            newProperty("roles.1.change-home", false);

        static final Property<TestEnum> ROLE_FALSE_ENUM =
            newProperty(TestEnum.class, "roles.false.-5", TestEnum.FOURTH);

        private PathsWithNumbers() {
        }
    }
}
