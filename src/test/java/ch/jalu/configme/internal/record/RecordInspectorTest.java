package ch.jalu.configme.internal.record;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Test for {@link RecordInspector}.
 */
@ExtendWith(MockitoExtension.class)
class RecordInspectorTest {

    @Test
    void shouldCheckIfClassExtendsRecord() {
        // given
        ReflectionHelper reflectionHelper = mock(ReflectionHelper.class);
        RecordInspector recordInspector = new RecordInspector(reflectionHelper);

        // when / then
        assertFalse(recordInspector.isRecord(Object.class));
        assertFalse(recordInspector.isRecord(Integer.class));
        assertFalse(recordInspector.isRecord(int.class));
        verifyNoInteractions(reflectionHelper);
    }

    @Test
    void shouldReturnThatClassIsRecord() throws NoSuchMethodException {
        // given
        ReflectionHelper reflectionHelper = mock(ReflectionHelper.class);
        Method isPrimitiveMethod = Class.class.getDeclaredMethod("isPrimitive");
        given(reflectionHelper.getZeroArgMethod(Class.class, "isRecord")).willReturn(isPrimitiveMethod);
        given(reflectionHelper.invokeZeroArgMethod(any(Method.class), any(Class.class))).willCallRealMethod();

        RecordInspector recordInspector = new RecordInspector(reflectionHelper) {
            @Override
            public boolean hasRecordAsSuperclass(@NotNull Class<?> clazz) {
                return true;
            }
        };

        // when / then
        assertThat(recordInspector.isRecord(int.class), equalTo(true));
        assertThat(recordInspector.isRecord(String.class), equalTo(false));
    }

    @Test
    void shouldReturnComponents() throws NoSuchMethodException {
        // given
        ReflectionHelper reflectionHelper = mock(ReflectionHelper.class);

        // Instead of Class#getRecordComponents, return method for FakeRecord#getComponents
        Method fakeGetComponentsMethod = FakeRecordType.class.getDeclaredMethod("getComponents");
        given(reflectionHelper.getZeroArgMethod(Class.class, "getRecordComponents"))
            .willReturn(fakeGetComponentsMethod);

        // Return FakeRecordComponent.class when we want to load Java 14+ RecordComponent
        Class<?> fakeRecordComponentClass = FakeRecordComponent.class;
        given(reflectionHelper.getClassOrThrow("java.lang.reflect.RecordComponent"))
            .willReturn((Class) fakeRecordComponentClass);

        // All methods on FakeRecordComponent are named as on the RecordComponent, so pass the calls through
        given(reflectionHelper.getZeroArgMethod(eq(fakeRecordComponentClass), anyString()))
            .willCallRealMethod();
        given(reflectionHelper.invokeZeroArgMethod(any(Method.class), any(Object.class))).willCallRealMethod();

        RecordInspector recordInspector = new RecordInspector(reflectionHelper) {
            @Override
            public boolean hasRecordAsSuperclass(@NotNull Class<?> clazz) {
                return true;
            }
        };

        // when
        RecordComponent[] components = recordInspector.getRecordComponents(FakeRecordType.class);

        // then
        assertThat(components, arrayWithSize(2));
        assertThat(components[0].getName(), equalTo("age"));
        assertThat(components[0].getType(), equalTo(int.class));
        assertThat(components[0].getGenericType(), equalTo(int.class));
        assertThat(components[1].getName(), equalTo("location"));
        assertThat(components[1].getType(), equalTo(String.class));
        assertThat(components[1].getGenericType(), equalTo(Object.class));
    }

    private static final class FakeRecordType {

        public static FakeRecordComponent[] getComponents() {
            FakeRecordComponent component1 = new FakeRecordComponent("age", int.class, int.class);
            FakeRecordComponent component2 = new FakeRecordComponent("location", String.class, Object.class);
            return new FakeRecordComponent[]{ component1, component2 };
        }
    }

    private static final class FakeRecordComponent {

        private final String name;
        private final Class<?> type;
        private final Type genericType;

        private FakeRecordComponent(String name, Class<?> type, Type genericType) {
            this.name = name;
            this.type = type;
            this.genericType = genericType;
        }

        public String getName() {
            return name;
        }

        public Class<?> getType() {
            return type;
        }

        public Type getGenericType() {
            return genericType;
        }
    }
}
