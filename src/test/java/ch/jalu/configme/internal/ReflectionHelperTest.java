package ch.jalu.configme.internal;

import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.Property;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

/**
 * Test for {@link ReflectionHelper}.
 */
@ExtendWith(MockitoExtension.class)
class ReflectionHelperTest {

    private final ReflectionHelper reflectionHelper = new ReflectionHelper();

    @Test
    void shouldReturnMethod() throws NoSuchMethodException {
        // given / when
        Method floatValueMethod = reflectionHelper.getNoArgMethod(Integer.class, "floatValue");

        // then
        assertThat(floatValueMethod, equalTo(Integer.class.getDeclaredMethod("floatValue")));
    }

    @Test
    void shouldThrowConfigMeExceptionForUnknownMethod() {
        // given / when
        ConfigMeException ex = assertThrows(ConfigMeException.class,
            () -> reflectionHelper.getNoArgMethod(Integer.class, "bogus"));

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
        String result = reflectionHelper.invokeNoArgMethod(toStringMethod, number);

        // then
        assertThat(result, equalTo("19"));
    }

    @Test
    void shouldWrapExceptionIfCallingMethodFails() throws NoSuchMethodException {
        // given
        Method privateResizeMethod = HashMap.class.getDeclaredMethod("resize");

        // when
        ConfigMeException ex = assertThrows(ConfigMeException.class,
            () -> reflectionHelper.invokeNoArgMethod(privateResizeMethod, new HashMap<>()));

        // then
        assertThat(ex.getMessage(), equalTo("Failed to call final java.util.HashMap$Node[] java.util.HashMap.resize() for {}"));
        assertThat(ex.getCause(), instanceOf(IllegalAccessException.class));
    }

    @Test
    void shouldThrowForNullReturnValue() throws NoSuchMethodException {
        // given
        Supplier<String> supplier = () -> null;
        Method supplierGetMethod = Supplier.class.getDeclaredMethod("get");

        // when
        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> reflectionHelper.invokeNoArgMethod(supplierGetMethod, supplier));

        // then
        assertThat(ex.getMessage(), equalTo("Method 'public abstract java.lang.Object java.util.function.Supplier.get()' unexpectedly returned null"));
    }

    @Test
    void shouldMakeFieldAccessible() throws NoSuchFieldException {
        // given
        Field field = getClass().getDeclaredField("reflectionHelper");
        assertThat(field.isAccessible(), equalTo(false)); // Validate assumption

        // when
        ReflectionHelper.setAccessibleIfNeeded(field);

        // then
        assertThat(field.isAccessible(), equalTo(true));
    }

    @Test
    void shouldNotSetAccessibleIfIsAccessible() {
        // given
        AccessibleObject accessibleObject = mock(AccessibleObject.class);
        given(accessibleObject.isAccessible()).willReturn(true);

        // when
        ReflectionHelper.setAccessibleIfNeeded(accessibleObject);

        // then
        verify(accessibleObject, only()).isAccessible();
    }

    @Test
    void shouldWrapSecurityException() {
        // given
        AccessibleObject accessibleObject = mock(AccessibleObject.class);
        given(accessibleObject.isAccessible()).willReturn(false);
        given(accessibleObject.toString()).willReturn("Shop#cashBox");

        SecurityException securityException = new SecurityException("Bad credit score");
        willThrow(securityException).given(accessibleObject).setAccessible(true);

        // when
        ConfigMeException ex = assertThrows(ConfigMeException.class,
            () -> ReflectionHelper.setAccessibleIfNeeded(accessibleObject));

        // then
        assertThat(ex.getMessage(), equalTo("Failed to make Shop#cashBox accessible"));
        assertThat(ex.getCause(), sameInstance(securityException));
    }
}
