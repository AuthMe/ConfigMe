package com.github.authme.configme.properties;

import com.github.authme.configme.resource.PropertyResource;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * General test for {@link Property} abstract type.
 */
public class PropertyGeneralTest {

    @Test(expected = NullPointerException.class)
    public void shouldRejectNullValue() {
        // given / when
        new PropertyTestImpl("my.path", null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldRejectNullPath() {
        // given / when
        new PropertyTestImpl(null, (byte) 123);
    }

    @Test
    public void shouldContainPathInToString() {
        // given
        String path = "some.test.path.byte";
        Property<Byte> property = new PropertyTestImpl(path, (byte) -89);

        // when
        String toString = property.toString();

        // then
        assertThat(toString, equalTo("Property '" + path + "'"));
    }


    private static final class PropertyTestImpl extends Property<Byte> {
        PropertyTestImpl(String path, Byte defaultValue) {
            super(path, defaultValue);
        }

        @Override
        protected Byte getFromResource(PropertyResource resource) {
            Integer value = resource.getInt(getPath());
            return value != null && value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE
                ? value.byteValue()
                : null;
        }
    }
}
