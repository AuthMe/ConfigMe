package ch.jalu.configme.beanmapper;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;

/**
 * Test for {@link DefaultMapper}.
 */
public class DefaultMapperTest {

    @Test
    public void shouldReturnSameInstance() {
        // given
        Mapper givenInstance = DefaultMapper.getInstance();

        // when
        Mapper instance = DefaultMapper.getInstance();

        // then
        assertThat(instance, sameInstance(givenInstance));
    }
}
