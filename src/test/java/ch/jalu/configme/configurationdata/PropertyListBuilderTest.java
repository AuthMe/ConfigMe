package ch.jalu.configme.configurationdata;

import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.StringProperty;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static ch.jalu.configme.TestUtils.transform;
import static ch.jalu.configme.TestUtils.verifyException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

/**
 * Test for {@link PropertyListBuilder}.
 */
class PropertyListBuilderTest {

    @Test
    void shouldKeepEntriesByInsertionAndGroup() {
        // given
        List<String> paths = Arrays.asList("japan.toyota", "indonesia.jakarta.koja", "japan.tokyo.sumida",
            "china.shanghai", "egypt.cairo", "china.shenzhen", "china.tianjin", "indonesia.jakarta.tugu",
            "egypt.luxor", "japan.nagoya", "japan.tokyo.taito");
        PropertyListBuilder builder = new PropertyListBuilder();

        // when
        for (String path : paths) {
            Property<?> property = createPropertyWithPath(path);
            builder.add(property);
        }

        // then
        List<Property<?>> knownProperties = builder.create();
        List<String> resultPaths = transform(knownProperties, Property::getPath);

        assertThat(knownProperties, hasSize(paths.size()));
        assertThat(knownProperties, hasSize(resultPaths.size()));
        assertThat(resultPaths, contains("japan.toyota", "japan.tokyo.sumida", "japan.tokyo.taito", "japan.nagoya",
            "indonesia.jakarta.koja", "indonesia.jakarta.tugu", "china.shanghai", "china.shenzhen", "china.tianjin",
            "egypt.cairo", "egypt.luxor"));
    }

    @Test
    void shouldThrowForSamePropertyAdded() {
        // given
        PropertyListBuilder properties = new PropertyListBuilder();
        properties.add(createPropertyWithPath("test.version"));
        properties.add(createPropertyWithPath("test.name"));

        // when / then
        verifyException(() -> properties.add(createPropertyWithPath("test.version")),
            ConfigMeException.class, "already exists");
        assertThat(properties.create(), hasSize(2));
    }

    /**
     * Checks that if we have a property like {@code test.version} we can't add a child to it,
     * e.g. {@code test.version.major}.
     */
    @Test
    void shouldThrowForPropertyHavingIllegalChild() {
        // given
        PropertyListBuilder properties = new PropertyListBuilder();
        properties.add(createPropertyWithPath("test.version"));

        // when / then
        verifyException(() -> properties.add(createPropertyWithPath("test.version.major")),
            ConfigMeException.class, "Unexpected entry found");
        assertThat(properties.create(), hasSize(1));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldThrowForUnknownInternalEntry() {
        // given
        PropertyListBuilder properties = new PropertyListBuilder();
        properties.add(createPropertyWithPath("test.name"));

        Map<String, Object> internalMap = properties.getRootEntries();
        // Put an unknown object in test.version
        ((Map<String, Object>) internalMap.get("test")).put("version", new Object());

        // when / then
        verifyException(() -> properties.add(createPropertyWithPath("test.version.minor")),
            ConfigMeException.class, "Value of unknown type found");
    }

    private static Property<?> createPropertyWithPath(String path) {
        return new StringProperty(path, "");
    }
}
