package ch.jalu.configme.beanmapper;

import org.junit.Test;

import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

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
