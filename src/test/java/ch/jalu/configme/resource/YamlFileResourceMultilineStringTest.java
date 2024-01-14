package ch.jalu.configme.resource;

import ch.jalu.configme.TestUtils;
import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.PropertyInitializer;
import ch.jalu.configme.properties.StringProperty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Tests that multi-line string values are properly read and exported.
 *
 * @see <a href="https://github.com/AuthMe/ConfigMe/issues/405">Issue #405</a>
 */
class YamlFileResourceMultilineStringTest {

    @TempDir
    public Path temporaryFolder;

    @Test
    void shouldReadAndExportMultipleLines() throws IOException {
        // given
        Path configFile = TestUtils.copyFileFromResources("/multiple_lines.yml", temporaryFolder);
        PropertyResource resource = new YamlFileResource(configFile);

        Property<String> linesProperty = PropertyInitializer.newProperty("lines", "");
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(Collections.singletonList(linesProperty));
        configurationData.initializeValues(resource.createReader());

        // when (read)
        String linesValue = configurationData.getValue(linesProperty);

        // then
        assertThat(linesValue, equalTo("First row\n\nSecond row\nThird row\n"));

        // when (write)
        resource.exportProperties(configurationData);

        // then
        String lines = linesProperty.determineValue(resource.createReader()).getValue();
        assertThat(lines, equalTo("First row\n\nSecond row\nThird row\n"));

        byte[] fileBytes = Files.readAllBytes(configFile);
        String fileContent = new String(fileBytes, StandardCharsets.UTF_8);
        assertThat(fileContent,
            equalTo(
                "lines: |"
                + "\n    First row"
                + "\n"
                + "\n    Second row"
                + "\n    Third row"
                + "\n"));
    }

    @Test
    void shouldWriteMultipleLines() throws IOException {
        // given
        Path configFile = TestUtils.copyFileFromResources("/empty_file.yml", temporaryFolder);
        PropertyResource resource = new YamlFileResource(configFile);

        Property<String> l1Property = PropertyInitializer.newProperty("l1", "");
        Property<String> l2Property = PropertyInitializer.newProperty("l2", "");
        Property<String> l3Property = PropertyInitializer.newProperty("l3", "");
        Property<String> l4Property = PropertyInitializer.newProperty("l4", "");

        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(
            Arrays.asList(l1Property, l2Property, l3Property, l4Property));
        configurationData.initializeValues(resource.createReader());

        // set multiple-line strings
        configurationData.setValue(l1Property, "First row\nSecond row");
        configurationData.setValue(l2Property, "First row\r\nSecond row");
        configurationData.setValue(l3Property, "First text Second text");
        configurationData.setValue(l4Property, "[{\r\n\"enabled\" : true \r\n}]");

        // when
        resource.exportProperties(configurationData);

        // then
        PropertyReader propertyReader = resource.createReader();
        String l1 = l1Property.determineValue(propertyReader).getValue();
        assertThat(l1, equalTo("First row\nSecond row"));

        String l2 = l2Property.determineValue(propertyReader).getValue();
        assertThat(l2, equalTo("First row\r\nSecond row"));

        String l3 = l3Property.determineValue(propertyReader).getValue();
        assertThat(l3, equalTo("First text Second text"));

        String l4 = l4Property.determineValue(propertyReader).getValue();
        assertThat(l4, equalTo("[{\r\n\"enabled\" : true \r\n}]"));

        byte[] fileBytes = Files.readAllBytes(configFile);
        String fileContent = new String(fileBytes, StandardCharsets.UTF_8);

        assertThat(fileContent,
            equalTo(
                "l1: |-"
                    + "\n    First row"
                    + "\n    Second row"
                    + "\nl2: \"First row\\r\\nSecond row\""
                    + "\nl3: First text Second text"
                    + "\nl4: \"[{\\r\\n\\\"enabled\\\" : true \\r\\n}]\""
                    + "\n"));
    }

    @ParameterizedTest
    @MethodSource("getStringsToExport")
    void shouldExportIdenticalValue(String value) {
        // given
        Property<String> property = new StringProperty("test.string", "#Default");
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(Collections.singletonList(property));
        configurationData.setValue(property, value);

        Path file = temporaryFolder.resolve("test.yml");
        YamlFileResource resource = new YamlFileResource(file);

        // when
        resource.exportProperties(configurationData);
        configurationData.setValue(property, "#Overwritten");
        configurationData.initializeValues(resource.createReader());

        // then
        assertThat(configurationData.getValue(property), equalTo(value));
    }

    /*
     * Some of these texts aren't exported as scalar blocks despite having a newline in them; SnakeYAML checks the
     * contents and decides whether the literal scalar style can be applied. The purpose of these test cases is to
     * ensure that the strings are exported and re-read to the IDENTICAL value (including any and all whitespace),
     * and more distantly, to ensure that no values break the export (though that is the responsibility of SnakeYAML).
     */
    private static List<String> getStringsToExport() {
        List<String> cases = new ArrayList<>();

        // Whitespace cases
        cases.add(" Text with\ninitial whitespace");
        cases.add("Ending spaces\nin this text  ");
        cases.add("Text with\nending whitespace\n\n");
        cases.add("\nStart with a new line\nEnd with no new line");
        cases.add("\tFirst line\nhas a tab");
        cases.add("Indent the end\nwith a tab\t");

        // Carriage returns
        cases.add("\rCR lines\rCR lines");
        cases.add("More CR lines\r");
        cases.add("Here are\r\nWindows lines");
        cases.add("Another\r\nWin line text\r\n");

        // Other characters (typically chars that have a special meaning in YAML)
        cases.add("Second line\n# looks like a comment");
        cases.add("test: true\nhmm");
        cases.add("??? hi\n--- hello");
        cases.add("!!int 3\n!!string");
        cases.add("|MD table|Col_2|\n|---|---|\n|row1|rowA|\n|row2|rowB|");
        cases.add("\\ testing\n\\|some combinations\\n\n: test");

        return cases;
    }
}
