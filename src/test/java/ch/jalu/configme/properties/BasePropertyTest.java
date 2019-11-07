package ch.jalu.configme.properties;

import ch.jalu.configme.resource.PropertyReader;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for the {@link BaseProperty} abstract type.
 */
class BasePropertyTest {

    @Test
    void shouldRejectNullValue() {
        // given / when / then
        assertThrows(NullPointerException.class,
            () -> new PropertyTestImpl("my.path", null));
    }

    @Test
    void shouldRejectNullPath() {
        // given / when / then
        assertThrows(NullPointerException.class,
            () -> new PropertyTestImpl(null, (byte) 123));
    }

    @Test
    void shouldContainPathInToString() {
        // given
        String path = "some.test.path.byte";
        Property<Byte> property = new PropertyTestImpl(path, (byte) -89);

        // when
        String toString = property.toString();

        // then
        assertThat(toString, equalTo("Property '" + path + "'"));
    }


    private static final class PropertyTestImpl extends BaseProperty<Byte> {
        PropertyTestImpl(String path, Byte defaultValue) {
            super(path, defaultValue);
        }

        @Override
        protected Byte getFromReader(PropertyReader reader) {
            Integer value = reader.getInt(getPath());
            return value != null && value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE
                ? value.byteValue()
                : null;
        }

        @Override
        public Object toExportValue(Byte value) {
            return value;
        }
    }
}
