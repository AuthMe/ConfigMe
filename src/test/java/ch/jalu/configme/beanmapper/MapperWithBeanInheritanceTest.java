package ch.jalu.configme.beanmapper;

import ch.jalu.configme.TestUtils;
import ch.jalu.configme.properties.BeanProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.convertresult.PropertyValue;
import ch.jalu.configme.resource.PropertyReader;
import ch.jalu.configme.resource.YamlFileReader;
import ch.jalu.configme.samples.inheritance.Child;
import ch.jalu.configme.samples.inheritance.ChildWithFieldOverrides;
import ch.jalu.configme.samples.inheritance.Middle;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;

/**
 * Mapper integration test with various bean types that inherit from each other.
 */
class MapperWithBeanInheritanceTest {

    @TempDir
    Path tempFolder;

    @Nested
    class TestMiddle {

        private final Property<Middle> BEAN_PROPERTY =
            new BeanProperty<>("bean", Middle.class, new Middle());

        @Test
        void shouldLoadProperly() throws IOException {
            // given
            String yaml = "bean:"
                + "\n  id: 7"
                + "\n  temporary: true"
                + "\n  name: 'Foo'"
                + "\n  ratio: 0.25";
            Path file = TestUtils.createTemporaryFile(tempFolder);
            Files.write(file, yaml.getBytes());
            PropertyReader reader = new YamlFileReader(file);

            // when
            PropertyValue<Middle> value = BEAN_PROPERTY.determineValue(reader);

            // then
            assertThat(value.isValidInResource(), equalTo(true));
            assertThat(value.getValue().readId(), equalTo(7L));
            assertThat(value.getValue().readTemporary(), equalTo(false));
            assertThat(value.getValue().readName(), equalTo("Foo"));
            assertThat(value.getValue().readRatio(), equalTo(0.25f));
        }

        @Test
        void shouldExportProperly() {
            // given
            Middle bean = new Middle();
            bean.writeId(7L);
            bean.writeTemporary(true);
            bean.writeName("test");
            bean.writeRatio(0.45f);

            // when
            Object value = BEAN_PROPERTY.toExportValue(bean);

            // then
            assertThat(value, instanceOf(Map.class));
            Map<String, Object> exportValue = (Map<String, Object>) value;
            assertThat(exportValue.keySet(), contains("id", "name", "ratio"));
            assertThat(exportValue.get("id"), equalTo(7L));
            assertThat(exportValue.get("name"), equalTo("test"));
            assertThat(exportValue.get("ratio"), equalTo(0.45f));
        }
    }

    @Nested
    class TestChild {

        private final Property<Child> BEAN_PROPERTY =
            new BeanProperty<>("child", Child.class, new Child());

        @Test
        void shouldLoadProperly() throws IOException {
            // given
            String yaml = "child:"
                + "\n  id: 7"
                + "\n  temporary: true"
                + "\n  name: 'Foo'"
                + "\n  ratio: 0.25"
                + "\n  importance: 9";
            Path file = TestUtils.createTemporaryFile(tempFolder);
            Files.write(file, yaml.getBytes());
            PropertyReader reader = new YamlFileReader(file);

            // when
            PropertyValue<Child> value = BEAN_PROPERTY.determineValue(reader);

            // then
            assertThat(value.isValidInResource(), equalTo(true));
            assertThat(value.getValue().readId(), equalTo(7L));
            assertThat(value.getValue().readTemporary(), equalTo(false));
            assertThat(value.getValue().readName(), equalTo("Foo"));
            assertThat(value.getValue().readRatio(), equalTo(0.25f));
            assertThat(value.getValue().readImportance(), equalTo(9));
            assertThat(value.getValue().readChildTemporary(), equalTo(true));
        }

        @Test
        void shouldExportProperly() {
            // given
            Child bean = new Child();
            bean.writeId(7L);
            bean.writeTemporary(false);
            bean.writeName("Mike");
            bean.writeRatio(0.33f);
            bean.writeImportance(8);
            bean.writeChildTemporary(true);

            // when
            Object value = BEAN_PROPERTY.toExportValue(bean);

            // then
            assertThat(value, instanceOf(Map.class));
            Map<String, Object> exportValue = (Map<String, Object>) value;
            assertThat(exportValue.keySet(), contains("id", "temporary", "name", "ratio", "importance"));
            assertThat(exportValue.get("id"), equalTo(7L));
            assertThat(exportValue.get("temporary"), equalTo(true));
            assertThat(exportValue.get("name"), equalTo("Mike"));
            assertThat(exportValue.get("ratio"), equalTo(0.33f));
            assertThat(exportValue.get("importance"), equalTo(8));
        }
    }

    @Nested
    class TestChildWithFieldOverrides {

        private final Property<ChildWithFieldOverrides> BEAN_PROPERTY =
            new BeanProperty<>("bean", ChildWithFieldOverrides.class, new ChildWithFieldOverrides());

        @Test
        void shouldLoadProperly() throws IOException {
            // given
            String yaml = "bean:"
                + "\n  id: 7"
                + "\n  temporary: true"
                + "\n  name: 'Foo'"
                + "\n  ratio: 99999.9"
                + "\n  o_ratio: 0.25";
            Path file = TestUtils.createTemporaryFile(tempFolder);
            Files.write(file, yaml.getBytes());
            PropertyReader reader = new YamlFileReader(file);

            // when
            PropertyValue<ChildWithFieldOverrides> value = BEAN_PROPERTY.determineValue(reader);

            // then
            assertThat(value.isValidInResource(), equalTo(true));
            assertThat(value.getValue().readId(), equalTo(7L));
            assertThat(value.getValue().readTemporary(), equalTo(false));
            assertThat(value.getValue().readName(), nullValue());
            assertThat(value.getValue().readRatio(), equalTo(0f));
            assertThat(value.getValue().readChildName(), nullValue());
            assertThat(value.getValue().readChildRatio(), equalTo(0.25f));
        }

        @Test
        void shouldExportProperly() {
            // given
            ChildWithFieldOverrides bean = new ChildWithFieldOverrides();
            bean.writeId(7L);
            bean.writeTemporary(true);
            bean.writeName("name (ignored)");
            bean.writeRatio(999);
            bean.writeChildName("child name (ignored)");
            bean.writeChildRatio(6.5f);

            // when
            Object value = BEAN_PROPERTY.toExportValue(bean);

            // then
            assertThat(value, instanceOf(Map.class));
            Map<String, Object> exportValue = (Map<String, Object>) value;
            assertThat(exportValue.keySet(), contains("id", "o_ratio"));
            assertThat(exportValue.get("id"), equalTo(7L));
            assertThat(exportValue.get("o_ratio"), equalTo(6.5f));
        }
    }
}
