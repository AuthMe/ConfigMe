package ch.jalu.configme.properties;

import ch.jalu.configme.properties.convertresult.PropertyValue;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static ch.jalu.configme.TestUtils.isValidValueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Test for {@link EnumSetProperty}.
 */
@ExtendWith(MockitoExtension.class)
class EnumSetPropertyTest {

    @Mock
    private PropertyReader reader;

    @Test
    void shouldReturnEnumSetValue() {
        // given
        EnumSet<TestEnum> set = EnumSet.of(TestEnum.ENTRY_A);
        EnumSetProperty<TestEnum> property =
            new EnumSetProperty<>("enum.path", TestEnum.class, set);
        given(reader.getObject(property.getPath()))
            .willReturn(new HashSet<>(Collections.singletonList(EnumSet.of(TestEnum.ENTRY_A))));

        // when
        PropertyValue<Set<Set<TestEnum>>> result = property.determineValue(reader);

        // then
        assertThat(result, isValidValueOf(new HashSet<>(Collections.singletonList(
            EnumSet.of(TestEnum.ENTRY_A)))));
    }

    @Test
    void shouldReturnEnumSetValueFromArray() {
        // given
        EnumSetProperty<TestEnum> property =
            new EnumSetProperty<>("enum.path", TestEnum.class, new TestEnum[]{TestEnum.ENTRY_A, TestEnum.ENTRY_B});
        given(reader.getObject(property.getPath()))
            .willReturn(new HashSet<>(Collections.singletonList(EnumSet.of(TestEnum.ENTRY_A, TestEnum.ENTRY_B))));

        // when
        PropertyValue<Set<Set<TestEnum>>> result = property.determineValue(reader);

        // then
        assertThat(result, isValidValueOf(new HashSet<>(Collections.singletonList(
            EnumSet.of(TestEnum.ENTRY_A, TestEnum.ENTRY_B)))));
    }

    private enum TestEnum {

        ENTRY_A,

        ENTRY_B,

        ENTRY_C

    }
}
