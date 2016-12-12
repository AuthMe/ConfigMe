package com.github.authme.configme.beanmapper;

import com.github.authme.configme.TestUtils;
import org.junit.Test;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link ConfigMeMapper}.
 */
public class ConfigMeMapperTest {

    @Test
    public void shouldReturnMapper() {
        // given / when / then
        assertThat(ConfigMeMapper.getSingleton(), not(nullValue()));
    }

    @Test
    public void shouldHaveHiddenConstructor() {
        TestUtils.validateHasOnlyPrivateEmptyConstructor(ConfigMeMapper.class);
    }

}
