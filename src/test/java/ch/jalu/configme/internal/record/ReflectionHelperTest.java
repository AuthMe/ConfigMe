package ch.jalu.configme.internal.record;

import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.Property;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for {@link ReflectionHelper}.
 */
class ReflectionHelperTest {

    private final ReflectionHelper reflectionHelper = new ReflectionHelper();

    @Test
    void shouldReturnMethod() throws NoSuchMethodException {
        // given / when
        Method floatValueMethod = reflectionHelper.getZeroArgMethod(Integer.class, "floatValue");

        // then
        assertThat(floatValueMethod, equalTo(Integer.class.getDeclaredMethod("floatValue")));
    }

    @Test
    void shouldThrowConfigMeExceptionForUnknownMethod() {
        // given / when
        ConfigMeException ex = assertThrows(ConfigMeException.class,
            () -> reflectionHelper.getZeroArgMethod(Integer.class, "bogus"));

        // then
        assertThat(ex.getMessage(), equalTo("Could not get Integer#bogus method"));
        assertThat(ex.getCause(), instanceOf(NoSuchMethodException.class));
    }

    @Test
    void shouldReturnClass() {
        // given / when / then
        assertThat(reflectionHelper.getClassOrThrow("java.lang.Integer"), equalTo(Integer.class));
        assertThat(reflectionHelper.getClassOrThrow("ch.jalu.configme.properties.Property"), equalTo(Property.class));
    }

    @Test
    void shouldThrowForUnknownClass() {
        // given / when
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> reflectionHelper.getClassOrThrow("java.lang.Bogus"));

        // then
        assertThat(ex.getMessage(), equalTo("Class 'java.lang.Bogus' could not be loaded"));
        assertThat(ex.getCause(), instanceOf(ClassNotFoundException.class));
    }

    @Test
    void shouldCallMethod() throws NoSuchMethodException {
        // given
        Integer number = 19;
        Method toStringMethod = Integer.class.getDeclaredMethod("toString");

        // when
        String result = reflectionHelper.invokeZeroArgMethod(toStringMethod, number);

        // then
        assertThat(result, equalTo("19"));
    }

    @Test
    void shouldWrapExceptionIfCallingMethodFails() throws NoSuchMethodException {
        // given
        Method toStringMethod = HashMap.class.getDeclaredMethod("resize");

        // when
        ConfigMeException ex = assertThrows(ConfigMeException.class,
            () -> reflectionHelper.invokeZeroArgMethod(toStringMethod, new HashMap<>()));

        // then
        assertThat(ex.getMessage(), equalTo("Failed to call final java.util.HashMap$Node[] java.util.HashMap.resize() for {}"));
        assertThat(ex.getCause(), instanceOf(IllegalAccessException.class));
    }
}
