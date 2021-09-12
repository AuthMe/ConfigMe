package ch.jalu.configme.resource;

import ch.jalu.configme.TestUtils;
import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.properties.MapProperty;
import ch.jalu.configme.properties.convertresult.PropertyValue;
import ch.jalu.configme.properties.types.EnumPropertyType;
import ch.jalu.configme.properties.types.PrimitivePropertyType;
import ch.jalu.configme.samples.TestEnum;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static ch.jalu.configme.properties.PropertyInitializer.mapProperty;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

/**
 * Verifies that YAML paths with '.' are not split into nested paths when not configured.
 * <p>
 * @see <a href="https://github.com/AuthMe/ConfigMe/issues/214">Issue #214</a>
 */
class YamlFileResourceNoSplitPathsTest {

    @TempDir
    public Path tempFolder;

    @Test
    void shouldLoadFilesWithDotsSuccessfully() throws IOException {
        // given
        Path file = TestUtils.createTemporaryFile(tempFolder);
        String yaml = "groups:"
            + "\n  com.example.basic: 2"
            + "\n  com.example.advanced: 5"
            + "\n  com.example.premium: 10"
            + "\n  com.example.vip: 20";
        Files.write(file, yaml.getBytes(StandardCharsets.UTF_8));

        MapProperty<Integer> groupsProperty = mapProperty(PrimitivePropertyType.INTEGER)
            .path("groups")
            .build();
        YamlFileResourceOptions fileResourceOptions = YamlFileResourceOptions.builder()
            .splitDotPaths(false)
            .build();
        YamlFileResource resource = new YamlFileResource(file, fileResourceOptions);

        // when
        PropertyValue<Map<String, Integer>> readGroups = groupsProperty.determineValue(resource.createReader());

        // then
        assertThat(readGroups.isValidInResource(), equalTo(true));
        Map<String, Integer> expectedValue = new HashMap<>();
        expectedValue.put("com.example.basic", 2);
        expectedValue.put("com.example.advanced", 5);
        expectedValue.put("com.example.premium", 10);
        expectedValue.put("com.example.vip", 20);
        assertThat(readGroups.getValue(), equalTo(expectedValue));
    }

    @Test
    @Disabled
    void shouldExportMapWithDotsWithoutSplittingPaths() throws IOException {
        // given
        Path file = TestUtils.createTemporaryFile(tempFolder);
        MapProperty<TestEnum> sectionsProperty = mapProperty(EnumPropertyType.of(TestEnum.class))
            .path("sections")
            .build();
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(singletonList(sectionsProperty));

        Map<String, TestEnum> newSections = new LinkedHashMap<>();
        newSections.put("org.example.one", TestEnum.FOURTH);
        newSections.put("org.example.second.a", TestEnum.FIRST);
        newSections.put("org.example.second.b", TestEnum.SECOND);
        newSections.put("org.example.third", TestEnum.THIRD);
        newSections.put("abc", TestEnum.FOURTH);
        configurationData.setValue(sectionsProperty, newSections);

        YamlFileResourceOptions fileResourceOptions = YamlFileResourceOptions.builder()
            .splitDotPaths(false)
            .build();
        YamlFileResource resource = new YamlFileResource(file, fileResourceOptions);

        // when
        resource.exportProperties(configurationData);

        // then
        assertThat(Files.readAllLines(file), contains(
            "sections:",
            "    org.example.one: 'FOURTH'",
            "    org.example.second.a: 'FIRST'",
            "    org.example.second.b: 'SECOND'",
            "    org.example.third: 'THIRD'",
            "    abc: 'FOURTH'"
        ));
    }
}
