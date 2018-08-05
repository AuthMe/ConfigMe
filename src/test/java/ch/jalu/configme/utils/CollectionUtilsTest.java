package ch.jalu.configme.utils;

import ch.jalu.configme.TestUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link CollectionUtils}.
 */
public class CollectionUtilsTest {

    @Test
    public void shouldGetRangesFromList() {
        // given
        List<String> list = Arrays.asList("test", "1", "2", "3", "4");

        // when
        List<String> result1 = CollectionUtils.getRange(list, 2);
        List<String> result2 = CollectionUtils.getRange(list, 5);

        // then
        assertThat(result1, contains("2", "3", "4"));
        assertThat(result2, empty());
    }

    @Test
    public void shouldFindCommonEntries() {
        // given
        List<String> list1 = Arrays.asList("test", "1", "2", "3", "4");
        List<String> list2 = Arrays.asList("test", "1", "2", "xxx", "xxx");
        List<String> list3 = Arrays.asList("test", "1", "2", "3", "4", "5");
        List<String> list4 = Arrays.asList("abc", null, "def", "ghi");
        List<String> list5 = Arrays.asList("abc", null, "uvw", "xyz");

        // when
        List<String> result1 = CollectionUtils.filterCommonStart(list1, list2);
        List<String> result2 = CollectionUtils.filterCommonStart(list1, list3);
        List<String> result3 = CollectionUtils.filterCommonStart(list1, list4);
        List<String> result4 = CollectionUtils.filterCommonStart(list4, list5);

        // then
        assertThat(result1, contains("test", "1", "2"));
        assertThat(result2, contains("test", "1", "2", "3", "4"));
        assertThat(result3, empty());
        assertThat(result4, contains("abc", null));
    }

    @Test
    public void shouldHaveHiddenConstructor() {
        TestUtils.validateHasOnlyPrivateEmptyConstructor(CollectionUtils.class);
    }
}
