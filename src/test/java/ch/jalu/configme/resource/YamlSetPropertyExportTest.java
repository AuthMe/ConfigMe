package ch.jalu.configme.resource;

import ch.jalu.configme.TestUtils;
import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.properties.EnumSetProperty;
import ch.jalu.configme.samples.TestEnum;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

/**
 * Tests that {@link YamlFileResource} exports Sets in a nice way (cf. issue #27).
 */
class YamlSetPropertyExportTest {

    private static final String SAMPLE_FILE = "/empty_file.yml";

    @TempDir
    public Path temporaryFolder;

    private Path configFile;

    @BeforeEach
    void copyConfigFile() {
        configFile = TestUtils.copyFileFromResources(SAMPLE_FILE, temporaryFolder);
    }

    @Test
    void shouldLoadAndExportProperly() throws IOException {
        // given
        PropertyResource resource = new YamlFileResource(configFile);
        EnumSetProperty<TestEnum> setProperty = new ExportToSetProperty("sample.ratio.fields");
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(singletonList(setProperty));
        configurationData.setValue(setProperty, EnumSet.of(TestEnum.FIRST, TestEnum.SECOND, TestEnum.THIRD));

        // when
        resource.exportProperties(configurationData);

        // then
        assertThat(setProperty.determineValue(resource.createReader()).getValue(),
            contains(TestEnum.FIRST, TestEnum.SECOND, TestEnum.THIRD));

        assertThat(Files.readAllLines(configFile), contains(
            "sample:",
            "    ratio:",
            "        fields:",
            "        - FIRST",
            "        - SECOND",
            "        - THIRD"));
    }

    private static final class ExportToSetProperty extends EnumSetProperty<TestEnum> {

        ExportToSetProperty(String path) {
            super(path, TestEnum.class, EnumSet.noneOf(TestEnum.class));
        }

        @Override
        public Set<String> toExportValue(@NotNull EnumSet<TestEnum> value) {
            return value.stream()
                .map(Enum::name)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        }
    }
}
