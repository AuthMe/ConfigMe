package ch.jalu.configme.resource;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.TestUtils;
import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.properties.MapProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.convertresult.PropertyValue;
import ch.jalu.configme.properties.types.BeanPropertyType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

class YamlFileResourceRootMapPropertyTest {

    @TempDir
    public Path temporaryFolder;

    @Test
    void shouldWriteAndReadFile() {
        //given
        Path tempFolder = TestUtils.createTemporaryFile(temporaryFolder);
        YamlFileResource resource = new YamlFileResource(tempFolder);
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(SampleConfig.class);

        Map<String, InnerProperties> mapProperty = new HashMap<>();
        mapProperty.put("props", new InnerProperties());
        mapProperty.get("props").setProps(Arrays.asList("foo", "bar"));

        configurationData.setValue(SampleConfig.MAP_PROPERTY, mapProperty);
        resource.exportProperties(configurationData);

        //when
        PropertyValue<Map<String, InnerProperties>> actualPropertyValue =
            SampleConfig.MAP_PROPERTY.determineValue(resource.createReader());

        //then
        assertThat(actualPropertyValue.isValidInResource(), equalTo(true));
        assertThat(actualPropertyValue.getValue().keySet(), contains("props"));
        assertThat(actualPropertyValue.getValue().get("props").getProps(), contains("foo", "bar"));
    }

    public static final class SampleConfig implements SettingsHolder {

        public static final Property<Map<String, InnerProperties>> MAP_PROPERTY = new MapProperty<>(
            "",
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
