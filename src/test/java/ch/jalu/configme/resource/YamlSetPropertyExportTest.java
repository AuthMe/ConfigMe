package ch.jalu.configme.resource;

import ch.jalu.configme.TestUtils;
import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.properties.BaseProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.samples.TestEnum;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static ch.jalu.configme.TestUtils.transform;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

/**
 * Tests that {@link YamlFileResource} exports Sets in a nice way (cf. issue #27).
 */
public class YamlSetPropertyExportTest {

    private static final String SAMPLE_FILE = "/empty_file.yml";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File configFile;

    @Before
    public void copyConfigFile() {
        configFile = TestUtils.copyFileFromResources(SAMPLE_FILE, temporaryFolder);
    }

    @Test
    public void shouldLoadAndExportProperly() throws IOException {
        // given
        PropertyResource resource = new YamlFileResource(configFile);
        Property<Set<TestEnum>> setProperty = new EnumSetProperty("sample.ratio.fields", Collections.emptySet());
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(singletonList(setProperty));
        configurationData.setValue(setProperty, new LinkedHashSet<>(Arrays.asList(TestEnum.FIRST, TestEnum.SECOND, TestEnum.THIRD)));

        // when
        resource.exportProperties(configurationData);

        // then
        assertThat(setProperty.determineValue(resource.createReader()), contains(TestEnum.FIRST, TestEnum.SECOND, TestEnum.THIRD));

        assertThat(Files.readAllLines(configFile.toPath()), contains(
            "",
            "sample:",
            "    ratio:",
            "        fields: ",
            "        - FIRST",
            "        - SECOND",
            "        - THIRD"));
    }

    private static final class EnumSetProperty extends BaseProperty<Set<TestEnum>> {

        EnumSetProperty(String path, Set<TestEnum> defaultValue) {
            super(path, defaultValue);
        }

        @Override
        protected Set<TestEnum> getFromResource(PropertyReader reader) {
            List<?> list = reader.getList(getPath());
            if (list == null) {
                return null;
            }
            return new LinkedHashSet<>(
                transform(list, v -> TestEnum.valueOf(v.toString())));
        }

        @Override
        public List<String> toExportValue(Set<TestEnum> value) {
            return transform(value, Enum::name);
        }
    }
}
