package ch.jalu.configme.properties;

import ch.jalu.configme.TestUtils;
import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.resource.PropertyReader;
import ch.jalu.configme.resource.PropertyResource;
import ch.jalu.configme.resource.YamlFileResource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link StringProperty}.
 */
@ExtendWith(MockitoExtension.class)
class StringPropertyTest {

    @TempDir
    private Path temporaryFolder;

    private static PropertyReader reader;

    @BeforeAll
    static void setUpConfiguration() {
        reader = mock(PropertyReader.class);
        when(reader.getObject("str.path.test")).thenReturn("Test value");
        when(reader.getObject("str.path.wrong")).thenReturn(null);
    }

    @Test
    void shouldGetStringValue() {
        // given
        Property<String> property = new StringProperty("str.path.test", "unused default");

        // when
        String result = property.determineValue(reader).getValue();

        // then
        assertThat(result, equalTo("Test value"));
    }

    @Test
    void shouldGetStringDefault() {
        // given
        Property<String> property = new StringProperty("str.path.wrong", "given default value");

        // when
        String result = property.determineValue(reader).getValue();

        // then
        assertThat(result, equalTo("given default value"));
    }

    @Test
    void shouldDefineExportValue() {
        // given
        Property<String> property = new StringProperty("path", "def. value");

        // when
        Object exportValue = property.toExportValue("some value");

        // then
        assertThat(exportValue, equalTo("some value"));
    }

    @Test
    void shouldReturnStringForNumber() {
        // given
        Property<String> property1 = new StringProperty("one", "");
        Property<String> property2 = new StringProperty("two", "");
        given(reader.getObject(property1.getPath())).willReturn(1);
        given(reader.getObject(property2.getPath())).willReturn(-5.328);

        // when
        String value1 = property1.determineValue(reader).getValue();
        String value2 = property2.determineValue(reader).getValue();

        // then
        assertThat(value1, equalTo("1"));
        assertThat(value2, equalTo("-5.328"));
    }

    @Test
    void shouldReturnStringFromBoolean() {
        // given
        Property<String> property = new StringProperty("test", "");
        given(reader.getObject(property.getPath())).willReturn(false);

        // when
        String value = property.determineValue(reader).getValue();

        // then
        assertThat(value, equalTo("false"));
    }

    @Test
    void shouldReadMultipleLines() throws IOException {
        // given
        Path configFile = TestUtils.copyFileFromResources("/multiple_lines.yml", temporaryFolder);
        PropertyResource resource = new YamlFileResource(configFile);

        Property<String> linesProperty = PropertyInitializer.newProperty("lines", "");
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(Collections.singletonList(linesProperty));
        configurationData.initializeValues(resource.createReader());

        // when
        resource.exportProperties(configurationData);

        // then
        String lines = linesProperty.determineValue(resource.createReader()).getValue();
        assertThat(lines, equalTo("First row\n\nSecond row\nThird row\n"));

        byte[] fileBytes = Files.readAllBytes(configFile);
        String fileContent = new String(fileBytes);

        assertThat(
            fileContent,
            equalTo(
                "lines: |\n" +
                    "    First row\n\n" +
                    "    Second row\n" +
                    "    Third row\n"));
    }

    @Test
    void shouldWriteMultipleLines() throws IOException {
        // given
        Path configFile = TestUtils.copyFileFromResources("/empty_file.yml", temporaryFolder);
        PropertyResource resource = new YamlFileResource(configFile);

        Property<String> linesProperty = PropertyInitializer.newProperty("lines", "");
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(Collections.singletonList(linesProperty));
        configurationData.initializeValues(resource.createReader());

        // set a multiple-line string
        configurationData.setValue(linesProperty, "First row\n\nSecond row\nThird row\n");

        // when
        resource.exportProperties(configurationData);

        // then
        String lines = linesProperty.determineValue(resource.createReader()).getValue();
        assertThat(lines, equalTo("First row\n\nSecond row\nThird row\n"));

        byte[] fileBytes = Files.readAllBytes(configFile);
        String fileContent = new String(fileBytes);

        assertThat(
            fileContent,
            equalTo(
                "lines: |\n" +
                    "    First row\n\n" +
                    "    Second row\n" +
                    "    Third row\n"));
    }
}
