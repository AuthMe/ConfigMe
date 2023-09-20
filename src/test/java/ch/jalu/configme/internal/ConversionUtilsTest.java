package ch.jalu.configme.internal;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.properties.types.BooleanType;
import ch.jalu.configme.properties.types.PropertyType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Test for {@link ConversionUtils}.
 */
@ExtendWith(MockitoExtension.class)
class ConversionUtilsTest {

    @Test
    void shouldCreateArrayOfGivenTypeAndSize() {
        // given / when
        String[] arr1 = ConversionUtils.createArrayForReferenceType(String.class, 3);
        Integer[] arr2 = ConversionUtils.createArrayForReferenceType(Integer.class, 2);
        TimeUnit[] arr3 = ConversionUtils.createArrayForReferenceType(TimeUnit.class, 0);

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
            () -> ConversionUtils.createArrayForReferenceType(int.class, 2));
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
            () -> ConversionUtils.createArrayForReferenceType(void.class, 2));

        // then
        assertThat(ex1.getMessage(), equalTo("The component type may not be a primitive type, but got: int"));
        assertThat(ex2.getMessage(), equalTo("The component type may not be a primitive type, but got: void"));
    }

    @Test
    void shouldConvertAndNotLogError() {
        // given
        Object element = "true";
        PropertyType<Boolean> type = BooleanType.BOOLEAN;
        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);

        // when
        Boolean result = ConversionUtils.convertOrLogError(element, type, errorRecorder);

        // then
        assertThat(result, equalTo(true));
        verifyNoInteractions(errorRecorder);
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldLogErrorIfConversionIsNotPossible() {
        // given
        Object element = "true";
        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);
        PropertyType<Boolean> type = mock(PropertyType.class);
        given(type.convert(element, errorRecorder)).willReturn(null);

        // when
        Boolean result = ConversionUtils.convertOrLogError(element, type, errorRecorder);

        // then
        assertThat(result, nullValue());
        verify(errorRecorder).setHasError("Could not convert 'true'");
    }

    @Test
    void shouldConvertWithFunctionAndNotLogError() {
        // given
        Object element = "true";
        Function<Object, Boolean> convertFunction = elem -> true;
        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);

        // when
        Boolean result = ConversionUtils.convertOrLogError(element, convertFunction, errorRecorder);

        // then
        assertThat(result, equalTo(true));
        verifyNoInteractions(errorRecorder);
    }

    @Test
    void shouldLogErrorIfConversionWithFunctionIsNotPossible() {
        // given
        Object element = "true";
        Function<Object, Boolean> convertFunction = elem -> null;
        ConvertErrorRecorder errorRecorder = mock(ConvertErrorRecorder.class);

        // when
        Boolean result = ConversionUtils.convertOrLogError(element, convertFunction, errorRecorder);

        // then
        assertThat(result, nullValue());
        verify(errorRecorder).setHasError("Could not convert 'true'");
    }
}
