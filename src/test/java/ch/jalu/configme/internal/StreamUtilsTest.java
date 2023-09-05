package ch.jalu.configme.internal;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

/**
 * Test for {@link StreamUtils}.
 */
class StreamUtilsTest {

    @Test
    void shouldCreateStreamWithSameElement() {
        // given / when
        List<String> list1 = StreamUtils.repeat("3", 3).collect(Collectors.toList());
        List<String> list2 = StreamUtils.repeat("1", 1).collect(Collectors.toList());
        List<String> list3 = StreamUtils.repeat("0", 0).collect(Collectors.toList());

        // then
        assertThat(list1, contains("3", "3", "3"));
        assertThat(list2, contains("1"));
        assertThat(list3, empty());
    }
}
