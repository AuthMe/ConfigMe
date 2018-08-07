package ch.jalu.configme.utils;

import org.junit.Test;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link TypeInformation}.
 */
public class TypeInformationTest {

    @Test
    public void shouldHandleSimpleClassFields() {
        // given
        TypeInformation typeInfo = createTypeFromSampleField("str");

        // when
        Class<?> safeToWriteClass = typeInfo.getSafeToWriteClass();
        Class<?> safeToReadClass = typeInfo.getSafeToReadClass();

        // then
        assertThat(safeToWriteClass, equalTo(String.class));
        assertThat(safeToReadClass, equalTo(String.class));
    }

    @Test
    public void shouldHandleReadTypeInCollectionFields() {
        // given
        Map<String, Class<?>> expectedReadClasses = new HashMap<>();
        expectedReadClasses.put("strList", String.class);
        expectedReadClasses.put("listExStr", String.class);
        expectedReadClasses.put("listSupStr", Object.class);
        expectedReadClasses.put("listSetStr", Set.class);
        expectedReadClasses.put("listExSetEx", Set.class);

        // when / then
        expectedReadClasses.forEach((key, value) -> {
            TypeInformation typeInfo = createTypeFromSampleField(key);
            assertThat("Read class for " + key,
                typeInfo.getGenericType(0).getSafeToReadClass(), equalTo(value));
        });
    }

    @Test
    public void shouldHandleWriteTypeInCollectionFields() {
        // given
        Map<String, Class<?>> expectedReadClasses = new HashMap<>();
        expectedReadClasses.put("strList", String.class);
        expectedReadClasses.put("listExStr", null);
        expectedReadClasses.put("listSupStr", null);
        expectedReadClasses.put("listSetStr", Set.class);
        expectedReadClasses.put("listExSetEx", null);

        // when / then
        expectedReadClasses.forEach((key, value) -> {
            TypeInformation typeInfo = createTypeFromSampleField(key);
            Class<?> writeClass = typeInfo.getGenericTypeAsClass(0);
            if (!Objects.equals(writeClass, value)) {
                fail("For '" + key + "' expected '" + value + "' but got '" + writeClass + "'");
            }
        });
    }


    @Test
    public void shouldHandleSimpleFieldsOfTypeVariable() {
        // given
        TypeInformation typeInfoT = createTypeFromTypeVarField("tValue");
        TypeInformation typeInfoD = createTypeFromTypeVarField("dValue");
        TypeInformation typeInfoU = createTypeFromTypeVarField("uValue");

        // when / then
        assertThat(typeInfoT.getSafeToReadClass(), equalTo(Comparable.class));
        assertThat(typeInfoD.getSafeToReadClass(), equalTo(Object.class));
        assertThat(typeInfoU.getSafeToReadClass(), equalTo(Comparable.class));
        assertThat(typeInfoT.getSafeToWriteClass(), nullValue());
        assertThat(typeInfoD.getSafeToWriteClass(), nullValue());
        assertThat(typeInfoU.getSafeToWriteClass(), nullValue());
    }

    @Test
    public void shouldHandleReadTypeInCollectionFieldsWithTypeVariables() {
        // given
        Map<String, Class<?>> expectedReadClasses = new HashMap<>();
        expectedReadClasses.put("listOfT", Comparable.class);
        expectedReadClasses.put("listOfD", Object.class);
        expectedReadClasses.put("optionalU", Comparable.class);
        expectedReadClasses.put("mapSU", Number.class);

        // when / then
        expectedReadClasses.forEach((key, value) -> {
            TypeInformation typeInfo = createTypeFromTypeVarField(key);
            assertThat("Read class for " + key,
                typeInfo.getGenericType(0).getSafeToReadClass(), equalTo(value));
        });
    }

    @Test
    public void shouldHandleWriteTypeInCollectionFieldsWithTypeVariables() {
        // given
        List<String> fieldNames = Arrays.asList("listOfT", "listOfD", "optionalU", "mapSU");

        // when / then
        fieldNames.forEach(fieldName -> {
            TypeInformation typeInfo = createTypeFromTypeVarField(fieldName);
            Class<?> writeClass = typeInfo.getGenericTypeAsClass(0);
            assertThat("Write class for '" + fieldName + "'", writeClass, nullValue());
        });
    }

    @Test
    public void shouldHandleMapType() {
        // given
        TypeInformation typeForMapField = createTypeFromTypeVarField("mapSU");

        // when
        TypeInformation genericType = typeForMapField.getGenericType(1);

        // then
        assertThat(genericType.getSafeToWriteClass(), nullValue());
        assertThat(genericType.getSafeToReadClass(), equalTo(Comparable.class));
    }

    @Test
    public void shouldReturnNullForTypesWithTooFewGenericTypes() {
        // given
        TypeInformation strType = createTypeFromSampleField("str");
        TypeInformation listType = createTypeFromSampleField("strList");

        // when / then
        assertThat(strType.getGenericType(0), nullValue());
        assertThat(strType.getGenericType(1), nullValue());
        assertThat(strType.getGenericTypeAsClass(0), nullValue());
        assertThat(strType.getGenericTypeAsClass(1), nullValue());

        assertThat(listType.getGenericType(0), not(nullValue()));
        assertThat(listType.getGenericType(1), nullValue());
        assertThat(listType.getGenericTypeAsClass(0), not(nullValue()));
        assertThat(listType.getGenericTypeAsClass(1), nullValue());
    }

    @Test
    public void shouldReturnObjectClassAsReadClassForUnknownType() {
        // given
        Type type = mock(Type.class);

        // when
        TypeInformation typeInformation = new TypeInformation(type);

        // then
        assertThat(typeInformation.getType(), equalTo(type));
        assertThat(typeInformation.getSafeToReadClass(), equalTo(Object.class));
        assertThat(typeInformation.getSafeToWriteClass(), nullValue());
    }

    @Test
    public void shouldHaveTypeInfoInToString() {
        // given
        TypeInformation type = new TypeInformation(String.class);

        // when
        String string = type.toString();

        // then
        assertThat(string, equalTo(type.getClass().getSimpleName() + "[type=class java.lang.String]"));
    }

    @Test
    public void shouldDefineHashCodeFromType() {
        // given
        TypeInformation type1 = new TypeInformation(String.class);
        TypeInformation type2 = createTypeFromSampleField("str");
        TypeInformation type3 = new TypeInformation(null);

        // when / then
        assertThat(type1.hashCode(), equalTo(type1.getType().hashCode()));
        assertThat(type2.hashCode(), equalTo(type2.getType().hashCode()));
        assertThat(type3.hashCode(), equalTo(0));
    }

    @Test
    public void shouldBaseEqualsOnWrappedType() {
        // given
        TypeInformation type1 = createTypeFromSampleField("listExStr");
        TypeInformation type2 = createTypeFromSampleField("listExStr");
        TypeInformation type3 = new TypeInformation(String.class);

        // when / then
        assertTrue(type1.equals(type1));
        assertTrue(type1.equals(type2));

        assertFalse(type1.equals(type3));
        assertFalse(type1.equals(null));
        assertFalse(type1.equals(new Object()));
    }

    private static TypeInformation createTypeFromSampleField(String name) {
        try {
            return TypeInformation.fromField(SampleFields.class.getDeclaredField(name));
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Cannot find field '" + name + "'", e);
        }
    }

    private static TypeInformation createTypeFromTypeVarField(String name) {
        try {
            return TypeInformation.fromField(FieldsWithTypeVariable.class.getDeclaredField(name));
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Cannot find field '" + name + "'", e);
        }
    }

    private static final class SampleFields {
        String str;
        List<String> strList;
        List<? extends String> listExStr;
        List<? super String> listSupStr;
        List<Set<String>> listSetStr;
        List<? extends Set<? extends Comparable>> listExSetEx;
    }

    private static final class FieldsWithTypeVariable<
        T extends Comparable,
        D,
        U extends T,
        S extends Number & AutoCloseable> {

        T tValue;
        D dValue;
        U uValue;
        List<T> listOfT;
        List<D> listOfD;
        Optional<U> optionalU;
        Map<S, U> mapSU;
    }

}
