package com.github.authme.configme.knownproperties;

import com.github.authme.configme.exception.ConfigMeException;
import com.github.authme.configme.properties.Property;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link PropertyListBuilder}.
 */
public class PropertyListBuilderTest {

    @Test
    public void shouldKeepEntriesByInsertionAndGroup() {
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
        List<String> resultPaths = knownProperties.stream().map(Property::getPath).collect(Collectors.toList());

        assertThat(knownProperties, hasSize(paths.size()));
        assertThat(knownProperties, hasSize(resultPaths.size()));
        assertThat(resultPaths, contains("japan.toyota", "japan.tokyo.sumida", "japan.tokyo.taito", "japan.nagoya",
            "indonesia.jakarta.koja", "indonesia.jakarta.tugu", "china.shanghai", "china.shenzhen", "china.tianjin",
            "egypt.cairo", "egypt.luxor"));
    }

    @Test
    public void shouldThrowForSamePropertyAdded() {
        // given
        PropertyListBuilder properties = new PropertyListBuilder();
        properties.add(createPropertyWithPath("test.version"));
        properties.add(createPropertyWithPath("test.name"));

        // when / then
        try {
            properties.add(createPropertyWithPath("test.version"));
            fail("Expected exception to be thrown");
        } catch (ConfigMeException e) {
            assertThat(e.getMessage(), containsString("already exists"));
        }

        assertThat(properties.create(), hasSize(2));
    }

    /**
     * Checks that if we have a property like {@code test.version} we can't add a child to it,
     * e.g. {@code test.version.major}.
     */
    @Test
    public void shouldThrowForPropertyHavingIllegalChild() {
        // given
        PropertyListBuilder properties = new PropertyListBuilder();
        properties.add(createPropertyWithPath("test.version"));

        // when / then
        try {
            properties.add(createPropertyWithPath("test.version.major"));
            fail("Expected exception to be thrown");
        } catch (ConfigMeException e) {
            assertThat(e.getMessage(), containsString("Unexpected entry found"));
        }

        assertThat(properties.create(), hasSize(1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldThrowForUnknownInternalEntry() throws ReflectiveOperationException {
        // given
        PropertyListBuilder properties = new PropertyListBuilder();
        properties.add(createPropertyWithPath("test.name"));

        Map<String, Object> internalMap = getInternalMap(properties);
        // Put an unknown object in test.version
        ((Map<String, Object>) internalMap.get("test")).put("version", new Object());

        // when
        try {
            properties.add(createPropertyWithPath("test.version.minor"));
            fail("Expected exception to be thrown");
        } catch (ConfigMeException e) {
            assertThat(e.getMessage(), containsString("Value of unknown type found"));
        }
    }

    private static Property<?> createPropertyWithPath(String path) {
        Property<?> property = mock(Property.class);
        when(property.getPath()).thenReturn(path);
        return property;
    }

    private static Map<String, Object> getInternalMap(PropertyListBuilder properties)
                                                      throws ReflectiveOperationException {
        Field field = PropertyListBuilder.class.getDeclaredField("rootEntries");
        field.setAccessible(true);
        return (Map<String, Object>) field.get(properties);
    }
}
