package com.github.authme.configme.beanmapper;

import com.github.authme.configme.TestUtils;
import com.github.authme.configme.beanmapper.command.Executor;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link Transformers}.
 * <p>
 * See also {@link NumberProducerTest} and {@link ReturnerForMatchingTypeTest}.
 */
public class TransformersTest {

    @Test
    public void shouldTransformToString() {
        // given
        Object o1 = "test string";
        Object o2 = 123.45;
        Object o3 = null;
        Transformers.StringProducer transformer = new Transformers.StringProducer();

        // when
        String r1 = transformer.safeTransform(String.class, o1);
        String r2 = transformer.safeTransform(String.class, o2);
        String r3 = transformer.safeTransform(String.class, o3);

        // then
        assertThat(r1, equalTo(o1));
        assertThat(r2, equalTo("123.45"));
        assertThat(r3, equalTo("null"));
    }

    @Test
    public void shouldTransformToEnum() {
        // given
        String s1 = "USER";
        String s2 = "Console";
        String s3 = "bogus";
        String s4 = null;
        Transformers.EnumProducer transformer = new Transformers.EnumProducer();
        Class<? extends Enum<?>> clazz = Executor.class;

        // when
        Enum e1 = transformer.safeTransform(clazz, s1);
        Enum e2 = transformer.safeTransform(clazz, s2);
        Enum e3 = transformer.safeTransform(clazz, s3);
        Enum e4 = transformer.safeTransform(clazz, s4);

        // then
        assertThat(Executor.USER.equals(e1), equalTo(true));
        assertThat(Executor.CONSOLE.equals(e2), equalTo(true));
        assertThat(e3, nullValue());
        assertThat(e4, nullValue());
    }

    @Test
    public void shouldHaveHiddenConstructor() {
        TestUtils.validateHasOnlyPrivateEmptyConstructor(Transformers.class);
    }
}
