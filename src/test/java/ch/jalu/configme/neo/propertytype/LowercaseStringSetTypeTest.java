package ch.jalu.configme.neo.propertytype;

import ch.jalu.configme.neo.resource.PropertyReader;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link LowercaseStringSetType}.
 */
public class LowercaseStringSetTypeTest {

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
        PropertyType<Set<String>> type = new LowercaseStringSetType();

        // when
        Set<String> result = type.getFromReader(reader, "lowercaselist.path.test");

        // then
        assertThat(result, contains("test1", "test2", "3rd test"));
    }

    @Test
    public void shouldReturnNullForMissingValues() {
        // given
        PropertyType<Set<String>> type = new LowercaseStringSetType();

        // when
        Set<String> result = type.getFromReader(reader, "lowercaselist.path.wrong");

        // then
        assertThat(result, nullValue());
    }

    @Test
    public void shouldReturnListOfStringsForListWithMixedTypes() {
        // given
        PropertyType<Set<String>> type = new LowercaseStringSetType();

        // when
        Set<String> result = type.getFromReader(reader, "lowercaselist.path.mixed");

        // then
        assertThat(result, contains("b", "test", "1"));
    }

    @Test
    public void shouldHandleNull() {
        // given
        PropertyType<Set<String>> type = new LowercaseStringSetType();
        List list = Arrays.asList(null, "test", null, "test");
        given(reader.getList("the.path.to.fetch")).willReturn(list);

        // when
        Set<String> result = type.getFromReader(reader, "the.path.to.fetch");

        // then
        assertThat(result, contains("null", "test"));
    }

    @Test
    public void shouldDefineExportValue() {
        // given
        PropertyType<Set<String>> type = new LowercaseStringSetType();

        // when
        Object exportValue = type.toExportValue(new LinkedHashSet<>(Arrays.asList("first", "second", "third", "fourth")));

        // then
        assertThat(exportValue, instanceOf(Collection.class));
        assertThat((Collection<?>) exportValue, contains("first", "second", "third", "fourth"));
    }

    @Test
    public void shouldDefineIfIsPresent() {
        // given
        PropertyType<Set<String>> type = new LowercaseStringSetType();

        // when
        boolean isPresent1 = type.isPresent(reader, "lowercaselist.path.test");
        boolean isPresent2 = type.isPresent(reader, "lowercaselist.path.wrong");

        // then
        assertThat(isPresent1, equalTo(true));
        assertThat(isPresent2, equalTo(false));
    }
}
