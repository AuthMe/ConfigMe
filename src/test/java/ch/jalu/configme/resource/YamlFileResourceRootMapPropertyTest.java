package ch.jalu.configme.resource;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.TestUtils;
import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.properties.MapProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.types.BeanPropertyType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class YamlFileResourceRootMapPropertyTest {

    @TempDir
    public Path temporaryFolder;

    @Test
    void shouldWriteAndReadFile() {
        Path yamlFile = TestUtils.createTemporaryFile(temporaryFolder);

        YamlFileResource resource = new YamlFileResource(yamlFile);
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(SampleConfig.class);

        configurationData.setValue(SampleConfig.SHAPE, singletonList("foo"));

        Map<String, InnerProperties> mapProperty = new HashMap<>();
        mapProperty.put("props", new InnerProperties());
        mapProperty.get("props").setProps(Arrays.asList("bar", "baz"));

        configurationData.setValue(SampleConfig.MAP_PROPERTY, mapProperty);

        resource.exportProperties(configurationData);

        List<String> actualShapeProperty = configurationData.getValue(SampleConfig.SHAPE);
        Map<String, InnerProperties> actualMapProperty = configurationData.getValue(SampleConfig.MAP_PROPERTY);

        assertThat(actualShapeProperty, equalTo(singletonList("foo")));
        assertThat(actualMapProperty, equalTo(mapProperty));
    }

    public static final class SampleConfig implements SettingsHolder {

        public static final Property<List<String>> SHAPE = newListProperty("shape");

        public static final Property<Map<String, InnerProperties>> MAP_PROPERTY = new MapProperty<>(
            "map",
            BeanPropertyType.of(InnerProperties.class)
        );

        private SampleConfig() {
        }
    }

    public static class InnerProperties {
        private List<String> props = new ArrayList<>();

        public List<String> getProps() {
            return props;
        }

        public void setProps(List<String> props) {
            this.props = props;
        }
    }
}
