package ch.jalu.configme.properties;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.resource.PropertyReader;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static ch.jalu.configme.TestUtils.getExceptionTypeForNullArg;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Test for the {@link BaseProperty} abstract type.
 */
class BasePropertyTest {

    @Test
    void shouldRejectNullValue() {
        // given / when / then
        assertThrows(getExceptionTypeForNullArg(),
            () -> new PropertyTestImpl("my.path", null));
    }

    @Test
    void shouldRejectNullPath() {
        // given / when / then
        assertThrows(getExceptionTypeForNullArg(),
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

    @Test
    void shouldCheckIfIsValidInResource() {
        // given
        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getInt("path.1")).willReturn(120);
        given(reader.getInt("path.2")).willReturn(9999999);
        Property<Byte> property1 = new PropertyTestImpl("path.1", (byte) -89);
        Property<Byte> property2 = new PropertyTestImpl("path.2", (byte) -89);

        // when
        boolean isValid1 = property1.isValidInResource(reader);
        boolean isValid2 = property2.isValidInResource(reader);

        // then
        assertThat(isValid1, equalTo(true));
        assertThat(isValid2, equalTo(false));
    }

    private static final class PropertyTestImpl extends BaseProperty<Byte> {
        PropertyTestImpl(String path, Byte defaultValue) {
            super(path, defaultValue);
        }

        @Override
        protected Byte getFromReader(@NotNull PropertyReader reader, @NotNull ConvertErrorRecorder errorRecorder) {
            Integer value = reader.getInt(getPath());
            return value != null && value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE
                ? value.byteValue()
                : null;
        }

        @Override
        public Object toExportValue(@NotNull Byte value) {
            return value;
        }
    }
}
