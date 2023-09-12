package ch.jalu.configme.internal;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for {@link ArrayUtils}.
 */
class ArrayUtilsTest {

    @Test
    void shouldCreateArrayOfGivenTypeAndSize() {
        // given / when
        String[] arr1 = ArrayUtils.createArrayForReferenceType(String.class, 3);
        Integer[] arr2 = ArrayUtils.createArrayForReferenceType(Integer.class, 2);
        TimeUnit[] arr3 = ArrayUtils.createArrayForReferenceType(TimeUnit.class, 0);

        // then
        assertThat(arr1.getClass(), equalTo(String[].class));
        assertThat(arr1, arrayContaining(null, null, null));

        assertThat(arr2.getClass(), equalTo(Integer[].class));
        assertThat(arr2, arrayContaining(null, null));

        assertThat(arr3.getClass(), equalTo(TimeUnit[].class));
        assertThat(arr3, emptyArray());
    }

    @Test
    void shouldThrowForInvalidComponentType() {
        // given / when
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
            () -> ArrayUtils.createArrayForReferenceType(int.class, 2));
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
            () -> ArrayUtils.createArrayForReferenceType(void.class, 2));

        // then
        assertThat(ex1.getMessage(), equalTo("The component type may not be a primitive type, but got: int"));
        assertThat(ex2.getMessage(), equalTo("The component type may not be a primitive type, but got: void"));
    }
}
