package ch.jalu.configme.beanmapper;

import ch.jalu.configme.TestUtils;
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

    @Test(expected = UnsupportedOperationException.class)
    public void shouldDisallowSettingBeanPropertyDescription() {
        // given / when / then
        ((MapperImpl) DefaultMapper.getInstance()).setBeanDescriptionFactory(new BeanDescriptionFactoryImpl());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldDisallowSettingValueTransformer() {
        // given / when / then
        ((MapperImpl) DefaultMapper.getInstance()).setValueTransformer(new CombiningValueTransformer());
    }

    @Test
    public void shouldHaveHiddenConstructor() {
        TestUtils.validateHasOnlyPrivateEmptyConstructor(DefaultMapper.class);
    }
}
