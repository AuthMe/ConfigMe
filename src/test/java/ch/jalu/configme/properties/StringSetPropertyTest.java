package ch.jalu.configme.properties;

import ch.jalu.configme.properties.convertresult.PropertyValue;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static ch.jalu.configme.TestUtils.isErrorValueOf;
import static ch.jalu.configme.TestUtils.isValidValueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link StringSetProperty}.
 */
@ExtendWith(MockitoExtension.class)
class StringSetPropertyTest {

    private static PropertyReader reader;

    @BeforeAll
    @SuppressWarnings("unchecked")
    static void setUpConfiguration() {
        reader = mock(PropertyReader.class);
        // need to have the List objects unchecked so we satisfy the List<?> signature
        List stringList = Arrays.asList("test1", "Test2", "3rd test", "Test2");
        when(reader.getList("list.path.test")).thenReturn(stringList);
        when(reader.getList("list.path.wrong")).thenReturn(null);
        List mixedList = Arrays.asList("test1", false, "toast", 1);
        when(reader.getList("list.path.mixed")).thenReturn(mixedList);
    }

    @Test
    void shouldGetValueFromReader() {
        // given
        Property<Set<String>> property = new StringSetProperty("list.path.test", "1", "b");

        // when
        PropertyValue<Set<String>> result = property.determineValue(reader);

        // then
        assertThat(result, isValidValueOf(newLinkedHashSet("test1", "Test2", "3rd test")));
    }

    @Test
    void shouldGetStringListDefault() {
        // given
        Property<Set<String>> property = new StringSetProperty("list.path.wrong", "default", "list", "elements");

        // when
        PropertyValue<Set<String>> result = property.determineValue(reader);

        // then
        assertThat(result, isErrorValueOf(newLinkedHashSet("default", "list", "elements")));
    }

    @Test
    void shouldGetStringListDefaultForMixedListFromResource() {
        // given
        Property<Set<String>> property = new StringSetProperty("list.path.mixed", "My", "default", "values");

        // when
        PropertyValue<Set<String>> result = property.determineValue(reader);

        // then
        assertThat(result, isValidValueOf(newLinkedHashSet("test1", "false", "toast", "1")));
    }

    @Test
    void shouldReturnValueAsExportValue() {
        // given
        Property<Set<String>> property = new StringSetProperty("test.path");
        Set<String> value = newLinkedHashSet("one", "two");

        // when
        Object exportValue = property.toExportValue(value);

        // then
        assertThat(exportValue, sameInstance(value));
    }

    @Test
    void shouldHaveImmutableListAsDefaultValue() {
        // given
        Set<String> set = new LinkedHashSet<>();
        set.add("Two");
        set.add("Three");
        StringSetProperty propertyCreatedWithList = new StringSetProperty("test.path", set);
        StringSetProperty propertyCreatedWithVarargs = new StringSetProperty("test.path", "One", "Two", "One");

        // when
        Set<String> default1 = propertyCreatedWithList.getDefaultValue();
        Set<String> default2 = propertyCreatedWithVarargs.getDefaultValue();

        // then
        assertThat(default1, contains("Two", "Three"));
        assertThat(default1.getClass().getName(), equalTo("java.util.Collections$UnmodifiableSet"));
        assertThat(default2, contains("One", "Two"));
        assertThat(default2.getClass().getName(), equalTo("java.util.Collections$UnmodifiableSet"));
    }

    private static Set<String> newLinkedHashSet(String... values) {
        return new LinkedHashSet<>(Arrays.asList(values));
    }
}
