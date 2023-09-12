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
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link LowercaseStringSetProperty}.
 */
@ExtendWith(MockitoExtension.class)
class LowercaseStringSetPropertyTest {

    private static PropertyReader reader;

    @BeforeAll
    static void setUpConfiguration() {
        reader = mock(PropertyReader.class);
        List<String> stringList = Arrays.asList("test1", "Test2", "3rd TEST");
        given(reader.getObject("lowercaselist.path.test")).willReturn(stringList);
        given(reader.getObject("lowercaselist.path.wrong")).willReturn(null);
        List<Object> mixedList = Arrays.asList('b', "test", 1);
        given(reader.getObject("lowercaselist.path.mixed")).willReturn(mixedList);
    }

    @Test
    void shouldGetLowercaseStringListValue() {
        // given
        Property<Set<String>> property = new LowercaseStringSetProperty("lowercaselist.path.test", "1", "b");

        // when
        PropertyValue<Set<String>> result = property.determineValue(reader);

        // then
        assertThat(result, isValidValueOf(newLinkedHashSet("test1", "test2", "3rd test")));
    }

    @Test
    void shouldGetLowercaseStringListDefault() {
        // given
        Property<Set<String>> property =
            new LowercaseStringSetProperty("lowercaselist.path.wrong", "default", "list", "elements");

        // when
        PropertyValue<Set<String>> result = property.determineValue(reader);

        // then
        assertThat(result, isErrorValueOf(newLinkedHashSet("default", "list", "elements")));
    }

    @Test
    void shouldGetStringListDefaultForMixedListFromResource() {
        // given
        Property<Set<String>> property =
            new LowercaseStringSetProperty("lowercaselist.path.mixed", "my", "default", "values");

        // when
        PropertyValue<Set<String>> result = property.determineValue(reader);

        // then
        assertThat(result, isValidValueOf(newLinkedHashSet("b", "test", "1")));
    }

    @Test
    void shouldHandleNull() {
        // given
        Property<Set<String>> property = new LowercaseStringSetProperty("path");
        List<?> list = Arrays.asList(null, "test", null, "test");
        given(reader.getObject(property.getPath())).willReturn(list);

        // when
        PropertyValue<Set<String>> result = property.determineValue(reader);

        // then
        assertThat(result, isErrorValueOf(newLinkedHashSet("test")));
    }

    @Test
    void shouldDefineExportValue() {
        // given
        Property<Set<String>> property = new LowercaseStringSetProperty("path");

        // when
        Object exportValue = property.toExportValue(newLinkedHashSet("first", "second", "third", "fourth"));

        // then
        assertThat(exportValue, instanceOf(List.class));
        assertThat((List<?>) exportValue, contains("first", "second", "third", "fourth"));
    }

    @Test
    void shouldCreateImmutableSetForDefaultValue() {
        // given / when
        LowercaseStringSetProperty property1 = new LowercaseStringSetProperty("test.path", "abc", "def", "ghi");
        LowercaseStringSetProperty property2 = new LowercaseStringSetProperty("other.path", Arrays.asList("111", "222", "33"));

        // then
        assertThat(property1.getDefaultValue().getClass().getName(), equalTo("java.util.Collections$UnmodifiableSet"));
        assertThat(property1.getDefaultValue(), contains("abc", "def", "ghi"));
        assertThat(property2.getDefaultValue().getClass().getName(), equalTo("java.util.Collections$UnmodifiableSet"));
        assertThat(property2.getDefaultValue(), contains("111", "222", "33"));
    }

    private static Set<String> newLinkedHashSet(String... values) {
        return new LinkedHashSet<>(Arrays.asList(values));
    }
}
