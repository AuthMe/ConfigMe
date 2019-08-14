package ch.jalu.configme.properties;

import ch.jalu.configme.configurationdata.PropertyValue;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static ch.jalu.configme.TestUtils.isErrorValueOf;
import static ch.jalu.configme.TestUtils.isValidValueOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link LowercaseStringSetProperty}.
 */
public class LowercaseStringSetPropertyTest {

    private static PropertyReader reader;

    @BeforeClass
    @SuppressWarnings("unchecked")
    public static void setUpConfiguration() {
        reader = mock(PropertyReader.class);
        // need to have the List objects unchecked so we satisfy the List<?> signature
        List stringList = Arrays.asList("test1", "Test2", "3rd TEST");
        when(reader.getList("lowercaselist.path.test")).thenReturn(stringList);
        when(reader.getList("lowercaselist.path.wrong")).thenReturn(null);
        List mixedList = Arrays.asList('b', "test", 1);
        when(reader.getList("lowercaselist.path.mixed")).thenReturn(mixedList);
    }

    @Test
    public void shouldGetLowercaseStringListValue() {
        // given
        Property<Set<String>> property = new LowercaseStringSetProperty("lowercaselist.path.test", "1", "b");

        // when
        PropertyValue<Set<String>> result = property.determineValue(reader);

        // then
        assertThat(result, isValidValueOf(newLinkedHashSet("test1", "test2", "3rd test")));
    }

    @Test
    public void shouldGetLowercaseStringListDefault() {
        // given
        Property<Set<String>> property =
            new LowercaseStringSetProperty("lowercaselist.path.wrong", "default", "list", "elements");

        // when
        PropertyValue<Set<String>> result = property.determineValue(reader);

        // then
        assertThat(result, isErrorValueOf(newLinkedHashSet("default", "list", "elements")));
    }

    @Test
    public void shouldGetStringListDefaultForMixedListFromResource() {
        // given
        Property<Set<String>> property =
            new LowercaseStringSetProperty("lowercaselist.path.mixed", "my", "default", "values");

        // when
        PropertyValue<Set<String>> result = property.determineValue(reader);

        // then
        assertThat(result, isValidValueOf(newLinkedHashSet("b", "test", "1")));
    }

    @Test
    public void shouldHandleNull() {
        // given
        Property<Set<String>> property = new LowercaseStringSetProperty("path");
        List list = Arrays.asList(null, "test", null, "test");
        given(reader.getList(property.getPath())).willReturn(list);

        // when
        PropertyValue<Set<String>> result = property.determineValue(reader);

        // then
        assertThat(result, isValidValueOf(newLinkedHashSet("null", "test")));
    }

    @Test
    public void shouldDefineExportValue() {
        // given
        Property<Set<String>> property = new LowercaseStringSetProperty("path");

        // when
        Object exportValue = property.toExportValue(newLinkedHashSet("first", "second", "third", "fourth"));

        // then
        assertThat(exportValue, instanceOf(Collection.class));
        assertThat((Collection<?>) exportValue, contains("first", "second", "third", "fourth"));
    }

    @Test
    public void shouldCreateImmutableSetForDefaultValue() {
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
