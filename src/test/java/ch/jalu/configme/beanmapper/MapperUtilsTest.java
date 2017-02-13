package ch.jalu.configme.beanmapper;

import ch.jalu.configme.TestUtils;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static ch.jalu.configme.TestUtils.verifyException;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link MapperUtils}.
 */
public class MapperUtilsTest {

    @Test
    public void shouldInvokeDefaultConstructor() {
        // given / when
        SampleBean result = MapperUtils.invokeDefaultConstructor(SampleBean.class);

        // then
        assertThat(result, not(nullValue()));
    }

    @Test(expected = ConfigMeMapperException.class)
    public void shouldForwardConstructorError() {
        // given / when / then
        MapperUtils.invokeDefaultConstructor(Iterable.class);
    }

    @Test
    public void shouldHaveHiddenConstructor() {
        TestUtils.validateHasOnlyPrivateEmptyConstructor(MapperUtils.class);
    }

    private static final class SampleBean {
        SampleBean() {
        }
    }

}
