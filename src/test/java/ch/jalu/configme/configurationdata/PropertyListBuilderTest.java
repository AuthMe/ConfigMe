package ch.jalu.configme.configurationdata;

import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.StringProperty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static ch.jalu.configme.TestUtils.transform;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

        // when
        ConfigMeException ex = assertThrows(ConfigMeException.class,
            () -> properties.add(createPropertyWithPath("test.version")));

        // then
        assertThat(ex.getMessage(), equalTo("Path at 'test.version' already exists"));
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

        // when
        ConfigMeException ex = assertThrows(ConfigMeException.class,
            () -> properties.add(createPropertyWithPath("test.version.major")));

        // then
        assertThat(ex.getMessage(), equalTo("Unexpected entry found at path 'version'"));
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
        Object unknownObject = new Object();
        ((Map<String, Object>) internalMap.get("test")).put("version", unknownObject);

        // when
        ConfigMeException ex = assertThrows(ConfigMeException.class,
            () -> properties.add(createPropertyWithPath("test.version.minor")));

        // then
        assertThat(ex.getMessage(), equalTo("Value of unknown type found at 'version': " + unknownObject));
        assertThat(properties.create(), hasSize(1));
    }

    @Test
    void shouldSupportRootProperty() {
        // given
        PropertyListBuilder listBuilder = new PropertyListBuilder();
        Property<?> rootProperty = createPropertyWithPath("");
        listBuilder.add(rootProperty);

        // when
        List<Property<?>> properties = listBuilder.create();

        // then
        assertThat(properties, contains(rootProperty));
    }

    @Test
    void shouldThrowForRootPathAndOtherProperty() {
        // given
        PropertyListBuilder properties = new PropertyListBuilder();
        properties.add(createPropertyWithPath(""));
        properties.add(createPropertyWithPath("enabled"));

        // when
        ConfigMeException ex = assertThrows(ConfigMeException.class, properties::create);

        // then
        assertThat(ex.getMessage(),
            equalTo("A property at the root path (\"\") cannot be defined alongside other properties as the paths would conflict"));
    }

    @ParameterizedTest
    @MethodSource("malformedPropertyPaths")
    void shouldThrowForMalformedPropertyPath(String path) {
        // given
        PropertyListBuilder properties = new PropertyListBuilder();

        // when
        ConfigMeException ex = assertThrows(ConfigMeException.class,
            () -> properties.add(createPropertyWithPath(path)));

        // then
        assertThat(ex.getMessage(), equalTo("The path at '" + path + "' is malformed: dots may not be at "
            + "the beginning or end of a path, and dots may not appear multiple times successively."));
    }

    static Stream<String> malformedPropertyPaths() {
        return Stream.of(".", "..", ".security", "security.", "alf..beta", "security.hash..version.minor");
    }

    private static Property<?> createPropertyWithPath(String path) {
        return new StringProperty(path, "");
    }
}
