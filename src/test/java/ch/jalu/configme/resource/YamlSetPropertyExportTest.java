package ch.jalu.configme.resource;

import ch.jalu.configme.TestUtils;
import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.StringListProperty;
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
        resource.setValue("sample.ratio.fields", Arrays.asList(TestEnum.FIRST, TestEnum.SECOND, TestEnum.THIRD));
        Property<Set<TestEnum>> setProperty = new EnumSetProperty("sample.ratio.fields", Collections.emptySet());

        // when
        resource.exportProperties(new ConfigurationData(singletonList(setProperty)));
        resource.reload();

        // then
        assertThat(setProperty.getValue(resource), contains(TestEnum.FIRST, TestEnum.SECOND, TestEnum.THIRD));
        // Check that export can be read with StringListProperty too
        assertThat(new StringListProperty("sample.ratio.fields").getValue(resource),
            contains(TestEnum.FIRST.name(), TestEnum.SECOND.name(), TestEnum.THIRD.name()));

        assertThat(Files.readAllLines(configFile.toPath()), contains(
            "",
            "sample:",
            "    ratio:",
            "        fields: ",
            "        - 'FIRST'",
            "        - 'SECOND'",
            "        - 'THIRD'"));
    }

    private static final class EnumSetProperty extends Property<Set<TestEnum>> {

        EnumSetProperty(String path, Set<TestEnum> defaultValue) {
            super(path, defaultValue);
        }

        @Override
        protected Set<TestEnum> getFromResource(PropertyResource resource) {
            List<?> list = resource.getList(getPath());
            if (list == null) {
                return null;
            }
            return new LinkedHashSet<>(
                transform(list, v -> TestEnum.valueOf(v.toString())));
        }
    }
}
